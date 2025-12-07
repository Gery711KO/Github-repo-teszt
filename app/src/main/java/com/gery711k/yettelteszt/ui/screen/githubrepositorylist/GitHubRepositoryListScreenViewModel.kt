package com.gery711k.yettelteszt.ui.screen.githubrepositorylist

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryListItemDto
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GitHubRepositoryListScreenViewModel(
    private val gitHubRepository: GitHubRepository,
) : ViewModel() {

    val uiState = combine(
        gitHubRepository.storedQueryResults,
        gitHubRepository.searchHistory
    ) { itemState, searchHistory ->
        DashboardScreenUiState(
            listItems = itemState,
            searchHistory = searchHistory.toImmutableList(),
            canLoadMore = (itemState as? Result.Success)?.data?.canLoadMore == true
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun searchRepositories(query: String) {
        viewModelScope.launch {
            gitHubRepository.searchGitHubRepositories(query)
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            gitHubRepository.loadMore()
        }
    }
}

@Immutable
data class DashboardScreenUiState(
    val canLoadMore: Boolean,
    val listItems: Result<PaginatedList<GitHubRepositoryListItemDto>>?,
    val searchHistory: ImmutableList<String>
)