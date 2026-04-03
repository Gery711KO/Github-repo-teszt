package com.gery711k.yettelteszt.data.datasource.github

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gery711k.yettelteszt.data.api.github.GitHubApiService
import com.gery711k.yettelteszt.data.mappers.toDomain
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryListItemDto
import com.gery711k.yettelteszt.data.model.github.GitHubRepositoryOwnerDto
import com.gery711k.yettelteszt.data.model.github.GitHubRepositorySearchResultDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultGitHubDataSourceTest {

    private val gitHubApiService: GitHubApiService = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val dataStore: DataStore<Preferences> = mockk(relaxed = true)
    private val json = Json { ignoreUnknownKeys = true }

    private lateinit var dataSource: DefaultGitHubDataSource

    @BeforeEach
    fun setup() {
        dataSource = createDataSource()
    }

    @Test
    fun `fetchGitHubRepositories should call apiService and returns correct response`() = runTest {
        // given
        val expectedApiResponse = apiResponse
        coEvery {
            gitHubApiService.fetchGithubRepositories("repo", 1)
        } returns expectedApiResponse

        // when
        val result = dataSource.fetchGitHubRepositories(
            query = "repo",
            page = 1,
        )

        // then
        assertEquals(expectedApiResponse.toDomain(), result)
        coVerify { gitHubApiService.fetchGithubRepositories("repo", 1) }
    }

    @Test
    fun `fetchSearchHistory should return list in reversed order`() = runTest {
        // given
        val history = listOf("query1", "query2")
        val preferences = mockk<Preferences>()
        
        every { preferences[dataStoreKey] } returns json.encodeToString(history)
        every { dataStore.data } returns flowOf(preferences)

        // when
        val result = dataSource.fetchSearchHistory().first()

        // then
        assertEquals(listOf("query2", "query1"), result)
    }

    @Test
    fun `fetchSearchHistory should return empty list when no history exists`() = runTest {
        // given
        val preferences = mockk<Preferences>()
        
        every { preferences[dataStoreKey] } returns null
        every { dataStore.data } returns flowOf(preferences)

        // when
        val result = dataSource.fetchSearchHistory().first()

        // then
        assertEquals(emptyList<String>(), result)
    }

    @Test
    fun `saveSearchQuery should update DataStore with new query at the end of the list`() = runTest {
        // given
        val query = "new query"
        val existingHistory = listOf("old query")
        val preferences = mockk<Preferences>()
        val mutablePreferences = mockk<MutablePreferences>(relaxed = true)

        every { mutablePreferences[dataStoreKey] } returns json.encodeToString(existingHistory)
        every { preferences.toMutablePreferences() } returns mutablePreferences
        
        val transformSlot = slot<suspend (Preferences) -> Preferences>()
        coEvery { dataStore.updateData(capture(transformSlot)) } coAnswers {
            transformSlot.captured(preferences)
        }

        // when
        dataSource.saveSearchQuery(query)

        // then
        val expectedList = listOf("old query", "new query")
        coVerify {
            mutablePreferences[dataStoreKey] = json.encodeToString(expectedList)
        }
    }

    @Test
    fun `saveSearchQuery should move existing query to the end of the list`() = runTest {
        // given
        val query = "query1"
        val existingHistory = listOf("query1", "query2")
        val preferences = mockk<Preferences>()
        val mutablePreferences = mockk<MutablePreferences>(relaxed = true)

        every { mutablePreferences[dataStoreKey] } returns json.encodeToString(existingHistory)
        every { preferences.toMutablePreferences() } returns mutablePreferences
        
        val transformSlot = slot<suspend (Preferences) -> Preferences>()
        coEvery { dataStore.updateData(capture(transformSlot)) } coAnswers {
            transformSlot.captured(preferences)
        }

        // when
        dataSource.saveSearchQuery(query)

        // then
        val expectedList = listOf("query2", "query1")
        coVerify {
            mutablePreferences[dataStoreKey] = json.encodeToString(expectedList)
        }
    }

    private fun createDataSource() = DefaultGitHubDataSource(
        gitHubApiService = gitHubApiService,
        ioDispatcher = testDispatcher,
        dataStore = dataStore,
        json = json
    )
    
    companion object {
        private const val SHARED_PREF_KEY = "searchHistory"

        val dataStoreKey = stringPreferencesKey(SHARED_PREF_KEY)

        val apiResponse = GitHubRepositorySearchResultDto(
            total = 2,
            items = listOf(
                GitHubRepositoryListItemDto(
                    id = 1L,
                    name = "repo1",
                    description = "desc1",
                    repositoryLink = "link1",
                    stars = 10,
                    forksCount = 5,
                    createdAt = LocalDateTime.now(),
                    lastUpdatedAt = LocalDateTime.now(),
                    owner = GitHubRepositoryOwnerDto(1L, "user1", "avatar1", "url1")
                ),
                GitHubRepositoryListItemDto(
                    id = 2L,
                    name = "repo2",
                    description = "desc2",
                    repositoryLink = "link2",
                    stars = 20,
                    forksCount = 10,
                    createdAt = LocalDateTime.now(),
                    lastUpdatedAt = LocalDateTime.now(),
                    owner = GitHubRepositoryOwnerDto(2L, "user2", "avatar2", "url2")
                )
            )
        )
    }
}
