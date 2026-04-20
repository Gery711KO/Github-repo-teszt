package com.gery711k.yettelteszt.domain.usecase.github

import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import kotlinx.coroutines.flow.map

class GetRepositoryDetailByIdUseCase(
    private val gitHubRepository: GitHubRepository,
) {

    operator fun invoke(repositoryId: Long) =
        gitHubRepository.storedQueryResults.map { repositoriesResult ->
            repositoriesResult?.data?.list?.firstOrNull { it.id == repositoryId }
        }
}