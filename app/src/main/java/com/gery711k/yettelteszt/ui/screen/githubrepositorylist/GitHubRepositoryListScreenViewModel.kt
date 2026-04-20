package com.gery711k.yettelteszt.ui.screen.githubrepositorylist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.usecase.github.GetSearchHistoryUseCase
import com.gery711k.yettelteszt.domain.usecase.github.GetStoredQueryResultsUseCase
import com.gery711k.yettelteszt.domain.usecase.github.LoadMoreUseCase
import com.gery711k.yettelteszt.domain.usecase.github.SearchGitHubRepositoriesUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class GitHubRepositoryListScreenViewModel(
    private val searchGitHubRepositoriesUseCase: SearchGitHubRepositoriesUseCase,
    private val loadMoreUseCase: LoadMoreUseCase,
    getStoredQueryResultsUseCase: GetStoredQueryResultsUseCase,
    getSearchHistoryUseCase: GetSearchHistoryUseCase,
) : ViewModel() {

    private val searchQuery = MutableStateFlow<String?>(null)

    val uiState = combine(
        getStoredQueryResultsUseCase(),
        getSearchHistoryUseCase()
    ) { storedQueryResult, searchHistory ->
        DashboardScreenUiState(
            listItems = storedQueryResult,
            searchHistory = searchHistory.toImmutableList(),
            canLoadMore = (storedQueryResult as? Result.Success)?.data?.canLoadMore == true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
        initialValue = null
    )

    init {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Timber.tag("Unhandled error").e(throwable)
            }
        ) {
            searchQuery.filterNotNull().debounce(300).collectLatest { query ->
                searchGitHubRepositoriesUseCase(query)
            }
        }
    }

    fun searchRepositories(query: String) {
        searchQuery.value = query
    }

    fun loadMore() {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Timber.tag("Unhandled error").e(throwable)
            }
        ) {
            loadMoreUseCase()
        }
    }
}

@Immutable
data class DashboardScreenUiState(
    val canLoadMore: Boolean,
    val listItems: Result<PaginatedList<GitHubRepositoryListItem>>?,
    val searchHistory: ImmutableList<String>,
)