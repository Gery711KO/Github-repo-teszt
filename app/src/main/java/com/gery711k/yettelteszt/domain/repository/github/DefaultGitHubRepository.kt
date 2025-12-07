package com.gery711k.yettelteszt.domain.repository.github

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.gery711k.yettelteszt.data.datasource.github.GitHubDataSource
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryListItemDto
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.emptyPaginatedList
import com.gery711k.yettelteszt.domain.model.toPaginatedList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

internal class DefaultGitHubRepository(
    private val gitHubDataSource: GitHubDataSource,
    private val json: Json,
    dataStore: DataStore<Preferences>,
) : GitHubRepository, CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    private var query = MutableSharedFlow<QueryData>(replay = 1)
    private var storedQuery = MutableStateFlow<QueryData?>(null)

    override val storedQueryResults =
        MutableStateFlow<Result<PaginatedList<GitHubRepositoryListItemDto>>?>(null)

    override val searchHistory = dataStore.data.map { preferences ->
        preferences[GitHubDataSource.searchHistoryStoreKey]?.let {
            json.decodeFromString<List<String>>(it).reversed()
        } ?: emptyList()
    }.stateIn(this, SharingStarted.Eagerly, emptyList())

    init {
        launch {
            query.collect { query ->
                storedQuery.value = query

                handleSearch(query.query, query.page)
            }
        }
    }

    override suspend fun searchGitHubRepositories(query: String) {
        this.query.emit(
            QueryData(
                query = query,
                page = 1
            )
        )
    }

    override suspend fun loadMore() {
        if (storedQuery.value != null) {
            storedQuery.value.let {
                it?.copy(page = it.page + 1)
            }?.let {
                query.emit(it)
            }
        }
    }

    private suspend fun handleSearch(query: String, page: Int) {
        storedQueryResults.value = Result.Loading(
            storedQueryResults.value?.data ?: emptyPaginatedList()
        )

        // Csak a demo kedveert
        delay(2.seconds)

        try {
            val response =
                gitHubDataSource.fetchGitHubRepositories(query, page)

            val newResponse = response.items.toPaginatedList(
                canLoadMore = response.total > response.items.size
            )

            storedQueryResults.update { storedResult ->
                if (page != 1) {
                    storedResult?.data?.let { oldResult ->
                        val newList =
                            oldResult.list.plus(newResponse.list).toImmutableList()

                        oldResult.copy(
                            list = newList,
                            canLoadMore = response.total > newList.size
                        )
                    } ?: newResponse
                } else {
                    response.items.toPaginatedList(
                        canLoadMore = response.total > response.items.size
                    )
                }.let { paginatedList ->
                    Result.Success(paginatedList)
                }
            }
        } catch (exception: Exception) {
            storedQueryResults.value = Result.Error(
                error = exception,
                data = storedQueryResults.value?.data
            )
        }
    }

    private data class QueryData(
        val page: Int,
        val query: String,
    )
}