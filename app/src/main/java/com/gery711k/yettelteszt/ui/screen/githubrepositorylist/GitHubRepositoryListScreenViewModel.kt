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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GitHubRepositoryListScreenViewModel(
    private val searchGitHubRepositoriesUseCase: SearchGitHubRepositoriesUseCase,
    private val loadMoreUseCase: LoadMoreUseCase,
    getStoredQueryResultsUseCase: GetStoredQueryResultsUseCase,
    getSearchHistoryUseCase: GetSearchHistoryUseCase,
) : ViewModel() {

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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun searchRepositories(query: String) {
        viewModelScope.launch {
            searchGitHubRepositoriesUseCase(query)
        }
    }

    fun loadMore() {
        viewModelScope.launch {
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