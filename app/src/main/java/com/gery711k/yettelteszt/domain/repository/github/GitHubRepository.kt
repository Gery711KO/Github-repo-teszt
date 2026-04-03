package com.gery711k.yettelteszt.domain.repository.github

import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import kotlinx.coroutines.flow.StateFlow

interface GitHubRepository {
    val storedQueryResults: StateFlow<Result<PaginatedList<GitHubRepositoryListItem>>?>
    val searchHistory: StateFlow<List<String>>

    suspend fun searchGitHubRepositories(query: String)
    suspend fun loadMore()
}