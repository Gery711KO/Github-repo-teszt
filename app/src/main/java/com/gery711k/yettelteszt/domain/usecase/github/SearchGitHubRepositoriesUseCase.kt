package com.gery711k.yettelteszt.domain.usecase.github

import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository

class SearchGitHubRepositoriesUseCase(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke(query: String) {
        gitHubRepository.searchGitHubRepositories(query)
    }
}