package com.gery711k.yettelteszt.ui.screen.githubrepositorydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gery711k.yettelteszt.domain.usecase.github.GetStoredQueryResultsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GitHubRepositoryDetailScreenViewModel(
    private val repositoryId: Long,
    getStoredQueryResultsUseCase: GetStoredQueryResultsUseCase,
) : ViewModel() {

    val uiState = getStoredQueryResultsUseCase()
        .map { repositoriesResult ->
            repositoriesResult?.data?.list?.firstOrNull { it.id == repositoryId }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            null
        )
}