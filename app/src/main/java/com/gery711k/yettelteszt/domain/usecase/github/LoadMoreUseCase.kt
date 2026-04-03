package com.gery711k.yettelteszt.domain.usecase.github

import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository

class LoadMoreUseCase(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke() {
        gitHubRepository.loadMore()
    }
}