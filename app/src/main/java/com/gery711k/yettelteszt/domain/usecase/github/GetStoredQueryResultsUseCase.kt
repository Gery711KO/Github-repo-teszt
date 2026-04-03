package com.gery711k.yettelteszt.domain.usecase.github

import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import kotlinx.coroutines.flow.StateFlow

class GetStoredQueryResultsUseCase(
    private val gitHubRepository: GitHubRepository
) {
    operator fun invoke(): StateFlow<Result<PaginatedList<GitHubRepositoryListItem>>?> {
        return gitHubRepository.storedQueryResults
    }
}