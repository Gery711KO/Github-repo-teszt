package com.gery711k.yettelteszt.domain.usecase.github

import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope

class SearchGitHubRepositoriesUseCase(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke(query: String) {
        supervisorScope {
            listOf(
                async { gitHubRepository.saveSearchQuery(query) },
                async { gitHubRepository.searchGitHubRepositories(query = query) }
            ).awaitAll()
        }
    }
}