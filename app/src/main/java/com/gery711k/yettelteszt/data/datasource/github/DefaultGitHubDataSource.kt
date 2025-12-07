package com.gery711k.yettelteszt.data.datasource.github

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.gery711k.yettelteszt.data.api.github.GitHubApiService
import com.gery711k.yettelteszt.data.datasource.saveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class DefaultGitHubDataSource(
    private val gitHubApiService: GitHubApiService,
    private val ioDispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>,
    private val json: Json
) : GitHubDataSource {

    override suspend fun fetchGitHubRepositories(query: String, page: Int) =
        withContext(ioDispatcher) {
            saveSearchQuery(query)
            gitHubApiService.fetchGithubRepositories(query, page)
        }

    private fun CoroutineScope.saveSearchQuery(query: String) {
        launch {
            dataStore.saveData<List<String>>(
                json = json,
                key = GitHubDataSource.searchHistoryStoreKey
            ) { stored ->
                stored?.toMutableList()?.apply {
                    remove(query)
                    add(query)
                }?.distinct() ?: listOf(query)
            }
        }
    }
}