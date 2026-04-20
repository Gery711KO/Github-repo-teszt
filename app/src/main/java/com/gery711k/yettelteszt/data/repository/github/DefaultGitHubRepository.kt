package com.gery711k.yettelteszt.data.repository.github

import com.gery711k.yettelteszt.data.datasource.github.GitHubDataSource
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.model.github.QueryData
import com.gery711k.yettelteszt.domain.model.toErrorOrDefault
import com.gery711k.yettelteszt.domain.model.toLoadingOrDefault
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

@OptIn(FlowPreview::class)
internal class DefaultGitHubRepository(
    private val gitHubDataSource: GitHubDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GitHubRepository {

    private val _storedQuery = MutableStateFlow<QueryData?>(null)
    private val _storedQueryResults =
        MutableStateFlow<Result<PaginatedList<GitHubRepositoryListItem>>?>(null)

    override val storedQueryResults = _storedQueryResults.asStateFlow()
    override val searchHistory = gitHubDataSource.fetchSearchHistory()
    override val storedQuery = _storedQuery.asStateFlow()

    override suspend fun searchGitHubRepositories(query: String, page: Int) {
        withContext(ioDispatcher) {
            handleSearch(
                QueryData(
                    queryString = query,
                    page = page
                )
            )
        }
    }

    override suspend fun saveSearchQuery(query: String) {
        withContext(ioDispatcher) {
            gitHubDataSource.saveSearchQuery(query)
        }
    }

    private suspend fun handleSearch(query: QueryData) {
        _storedQuery.value = query
        _storedQueryResults.update { it.toLoadingOrDefault() }

        try {
            val response = gitHubDataSource.fetchGitHubRepositories(
                query = query.queryString,
                page = query.page
            )

            _storedQueryResults.update { storedResult ->
                val newList = if (query.page == 1) {
                    response.items.toImmutableList()
                } else {
                    val currentList = storedResult?.data?.list ?: emptyList()
                    (currentList + response.items).toImmutableList()
                }

                Result.Success(
                    PaginatedList(
                        list = newList,
                        canLoadMore = response.total > newList.size
                    )
                )
            }
        } catch (_: CancellationException) {
            _storedQuery.value = null
        } catch (exception: Exception) {
            _storedQueryResults.update { currentResult ->
                currentResult.toErrorOrDefault(exception)
            }
        }
    }
}