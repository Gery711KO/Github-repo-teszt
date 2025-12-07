package com.gery711k.yettelteszt.ui.screen.githubrepositorydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryListItemDto
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GitHubRepositoryDetailScreenViewModel(
    private val repositoryId: Long,
    gitHubRepository: GitHubRepository,
) : ViewModel() {

    val uiState = gitHubRepository.storedQueryResults
        .filterIsInstance<Result.Success<PaginatedList<GitHubRepositoryListItemDto>>>()
        .map { repositoriesResult ->
            repositoriesResult.data.list.firstOrNull { it.id == repositoryId }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            null
        )
}