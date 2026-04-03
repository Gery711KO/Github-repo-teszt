package com.gery711k.yettelteszt.data.datasource.github

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gery711k.yettelteszt.data.api.github.GitHubApiService
import com.gery711k.yettelteszt.data.datasource.saveData
import com.gery711k.yettelteszt.data.mappers.toDomain
import com.gery711k.yettelteszt.domain.datastore.GitHubDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
            gitHubApiService.fetchGithubRepositories(query, page).toDomain()
        }

    override suspend fun saveSearchQuery(query: String) {
        dataStore.saveData<List<String>>(
            json = json,
            key = searchHistoryStoreKey
        ) { stored ->
            stored?.toMutableList()?.apply {
                remove(query)
                add(query)
            }?.distinct() ?: listOf(query)
        }
    }

    override fun fetchSearchHistory(): Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[searchHistoryStoreKey]?.let {
            json.decodeFromString<List<String>>(it).reversed()
        } ?: emptyList()
    }

    companion object {
        private val searchHistoryStoreKey =
            stringPreferencesKey("searchHistory")
    }
}