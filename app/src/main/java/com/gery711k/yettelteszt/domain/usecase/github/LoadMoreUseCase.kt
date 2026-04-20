package com.gery711k.yettelteszt.domain.usecase.github

import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository

class LoadMoreUseCase(
    private val gitHubRepository: GitHubRepository
) {
    suspend operator fun invoke() {
        val currentResults = gitHubRepository.storedQueryResults.value
        val currentQuery = gitHubRepository.storedQuery.value ?: return

        if (currentResults is Result.Loading || currentResults?.data?.canLoadMore == false) {
            return
        }

        gitHubRepository.searchGitHubRepositories(
            query = currentQuery.queryString,
            page = currentQuery.page + 1,
        )
    }
}