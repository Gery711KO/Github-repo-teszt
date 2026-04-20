package com.gery711k.yettelteszt.domain.repository.github

import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.model.github.QueryData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface GitHubRepository {

    val storedQuery: StateFlow<QueryData?>
    val storedQueryResults: StateFlow<Result<PaginatedList<GitHubRepositoryListItem>>?>

    val searchHistory: Flow<List<String>>

    suspend fun searchGitHubRepositories(query: String, page: Int = 1)
    suspend fun saveSearchQuery(query: String)
}