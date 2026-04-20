package com.gery711k.yettelteszt.ui.screen.githubrepositorydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gery711k.yettelteszt.domain.usecase.github.GetRepositoryDetailByIdUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class GitHubRepositoryDetailScreenViewModel(
    repositoryId: Long,
    getRepositoryDetailByIdUseCase: GetRepositoryDetailByIdUseCase,
) : ViewModel() {

    val uiState = getRepositoryDetailByIdUseCase(repositoryId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5.seconds.inWholeMilliseconds),
            null
        )
}