package com.gery711k.yettelteszt.data.datasource.github

import com.gery711k.yettelteszt.domain.model.github.GitHubRepositorySearchResult
import kotlinx.coroutines.flow.Flow

interface GitHubDataSource {

    suspend fun fetchGitHubRepositories(
        query: String,
        page: Int = 1,
    ): GitHubRepositorySearchResult

    suspend fun saveSearchQuery(query: String)

    fun fetchSearchHistory(): Flow<List<String>>
}