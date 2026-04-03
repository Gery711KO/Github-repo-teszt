package com.gery711k.yettelteszt.data.repository.github

import com.gery711k.yettelteszt.domain.datastore.GitHubDataSource
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.model.toErrorOrDefault
import com.gery711k.yettelteszt.domain.model.toLoadingOrDefault
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
internal class DefaultGitHubRepository(
    private val gitHubDataSource: GitHubDataSource,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : GitHubRepository, CoroutineScope {
    override val coroutineContext: CoroutineContext = ioDispatcher

    private val queryCollector = MutableSharedFlow<QueryData>(replay = 1)
    private val storedQuery = MutableStateFlow<QueryData?>(null)

    override val storedQueryResults =
        MutableStateFlow<Result<PaginatedList<GitHubRepositoryListItem>>?>(null)

    override val searchHistory = gitHubDataSource.fetchSearchHistory()
        .stateIn(this, SharingStarted.Eagerly, emptyList())

    init {
        launch {
            queryCollector.collectLatest { query ->
                storedQuery.value = query

                handleSearch(query)
            }
        }
    }

    override suspend fun searchGitHubRepositories(query: String) {
        this.queryCollector.emit(
            QueryData(
                queryString = query,
                page = 1
            )
        )
    }

    override suspend fun loadMore() {
        if (storedQuery.value != null) {
            storedQuery.value.let { query ->
                query?.copy(page = query.page + 1)
            }?.let { query ->
                queryCollector.emit(query)
            }
        }
    }

    private suspend fun handleSearch(query: QueryData) {
        storedQueryResults.update { it.toLoadingOrDefault() }

        gitHubDataSource.saveSearchQuery(query.queryString)

        // For demo purposes
        delay(2.seconds)

        try {
            val response = gitHubDataSource.fetchGitHubRepositories(
                query = query.queryString,
                page = query.page
            )

            storedQueryResults.update { storedResult ->
                val paginatedList = storedResult?.data?.let { oldResult ->
                    val newList = oldResult.list.plus(response.items).toImmutableList()

                    oldResult.copy(
                        list = newList,
                        canLoadMore = response.total > newList.size
                    )
                }.takeIf {
                    query.page != 1
                } ?: response.items.toPaginatedList(
                    canLoadMore = response.total > response.items.size
                )

                Result.Success(paginatedList)
            }
        } catch (_: CancellationException) {
            /*
             * Ignore cancellation exception, it can only happen if I search for a new query
             * when a search is already in progress.
             */
        } catch (exception: Exception) {
            storedQueryResults.update { currentResult ->
                currentResult.toErrorOrDefault(exception)
            }
        }
    }

    private fun <T : Any> List<T>.toPaginatedList(canLoadMore: Boolean) =
        PaginatedList(
            list = this.toImmutableList(),
            canLoadMore = canLoadMore
        )

    private data class QueryData(
        val page: Int,
        val queryString: String,
    )
}