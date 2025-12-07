package com.gery711k.yettelteszt.data.datasource.github

import androidx.datastore.preferences.core.stringPreferencesKey
import com.gery711k.yettelteszt.data.model.github.GitHubRepositorySearchResultDto

interface GitHubDataSource {

    suspend fun fetchGitHubRepositories(
        query: String,
        page: Int = 1,
    ): GitHubRepositorySearchResultDto

    companion object {
        val searchHistoryStoreKey =
            stringPreferencesKey("searchHistory")
    }
}