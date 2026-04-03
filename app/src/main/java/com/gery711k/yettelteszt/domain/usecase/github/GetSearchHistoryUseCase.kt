package com.gery711k.yettelteszt.domain.usecase.github

import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import kotlinx.coroutines.flow.StateFlow

class GetSearchHistoryUseCase(
    private val gitHubRepository: GitHubRepository
) {
    operator fun invoke(): StateFlow<List<String>> {
        return gitHubRepository.searchHistory
    }
}