package com.gery711k.yettelteszt.data.repository.github

import com.gery711k.yettelteszt.domain.datastore.GitHubDataSource
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryOwner
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositorySearchResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultGitHubRepositoryTest {

    private val gitHubDataSource: GitHubDataSource = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: DefaultGitHubRepository

    @BeforeEach
    fun setup() {
        every { gitHubDataSource.fetchSearchHistory() } returns flowOf(emptyList())
        coEvery { gitHubDataSource.saveSearchQuery(any()) } returns Unit

        repository = DefaultGitHubRepository(gitHubDataSource, testDispatcher)
    }

    @Test
    fun `searchGitHubRepositories should update results to Success on successful API call`() =
        runTest(testDispatcher) {
            // given
            val query = "test"
            val mockResponse = GitHubRepositorySearchResult(
                total = 1,
                items = persistentListOf(
                    createRepo(id = 1L, name = "repo1"),
                )
            )
            coEvery { gitHubDataSource.fetchGitHubRepositories(query, 1) } returns mockResponse

            // when
            repository.searchGitHubRepositories(query)

            // Wait for the delay(2.seconds) in handleSearch
            advanceTimeBy(3.seconds)

            // then
            val result = repository.storedQueryResults.value
            assertTrue(result is Result.Success)
            assertEquals(1, (result as Result.Success).data.list.size)
            assertEquals("repo1", result.data.list[0].name)

            coVerify { gitHubDataSource.saveSearchQuery(query) }
            coVerify { gitHubDataSource.fetchGitHubRepositories(query, 1) }
        }

    @Test
    fun `searchGitHubRepositories should cancel previous request when new one arrives`() =
        runTest(testDispatcher) {
            // given
            val firstQuery = "first"
            val secondQuery = "second"
            val mockResponse = GitHubRepositorySearchResult(
                total = 1,
                items = persistentListOf(createRepo(1, "repo2"))
            )
            coEvery { gitHubDataSource.fetchGitHubRepositories(secondQuery, 1) } returns mockResponse

            // when
            repository.searchGitHubRepositories(firstQuery)
            repository.searchGitHubRepositories(secondQuery)
            advanceTimeBy(3.seconds)

            // then
            val result = repository.storedQueryResults.value
            assertTrue(result is Result.Success)
            assertEquals("repo2", (result as Result.Success).data.list[0].name)

            coVerify(exactly = 0) { gitHubDataSource.fetchGitHubRepositories(firstQuery, 1) }
            coVerify(exactly = 1) { gitHubDataSource.fetchGitHubRepositories(secondQuery, 1) }
        }

    @Test
    fun `searchGitHubRepositories should update results to Error on API failure`() =
        runTest(testDispatcher) {
            // given
            val query = "test"
            val exception = RuntimeException("API Error")
            coEvery { gitHubDataSource.fetchGitHubRepositories(any(), any()) } throws exception

            // when
            repository.searchGitHubRepositories(query)
            advanceTimeBy(3.seconds)

            // then
            val result = repository.storedQueryResults.value
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).error)
        }

    @Test
    fun `loadMore should fetch next page and append results`() = runTest(testDispatcher) {
        // given
        val query = "test"
        val firstPageResponse = GitHubRepositorySearchResult(
            total = 2,
            items = persistentListOf(createRepo(1, "repo1"))
        )
        val secondPageResponse = GitHubRepositorySearchResult(
            total = 2,
            items = persistentListOf(createRepo(2, "repo2"))
        )

        coEvery { gitHubDataSource.fetchGitHubRepositories(query, 1) } returns firstPageResponse
        coEvery { gitHubDataSource.fetchGitHubRepositories(query, 2) } returns secondPageResponse

        repository.searchGitHubRepositories(query)
        advanceTimeBy(3.seconds)

        // when
        repository.loadMore()
        advanceTimeBy(3.seconds)

        // then
        val result = repository.storedQueryResults.value
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(2, successResult.data.list.size)
        assertEquals("repo1", successResult.data.list[0].name)
        assertEquals("repo2", successResult.data.list[1].name)
        assertEquals(false, successResult.data.canLoadMore)
    }

    @Test
    fun `searchGitHubRepositories should show loading state first`() = runTest(testDispatcher) {
        // given
        val query = "test"
        coEvery { gitHubDataSource.fetchGitHubRepositories(any(), any()) } returns
                GitHubRepositorySearchResult(0, persistentListOf())

        // when
        repository.searchGitHubRepositories(query)

        // Wait just a bit to enter handleSearch but not finish the delay
        advanceTimeBy(1.seconds)

        // then
        assertTrue(repository.storedQueryResults.value is Result.Loading)
    }

    @Test
    fun `searchHistory should reflect data source flow`() = runTest(testDispatcher) {
        // given
        val history = listOf("query1", "query2")
        every { gitHubDataSource.fetchSearchHistory() } returns flowOf(history)

        // Need to recreate repository to collect from the new flow
        repository = DefaultGitHubRepository(gitHubDataSource, testDispatcher)

        // then
        assertEquals(history, repository.searchHistory.value)
    }

    @Test
    fun `loadMore should do nothing if no query was performed`() = runTest(testDispatcher) {
        // when
        repository.loadMore()
        advanceTimeBy(3.seconds)

        // then
        assertEquals(null, repository.storedQueryResults.value)
        coVerify(exactly = 0) { gitHubDataSource.fetchGitHubRepositories(any(), any()) }
    }

    @Test
    fun `canLoadMore should be true if total is greater than current count`() =
        runTest(testDispatcher) {
            // given
            val query = "test"
            val mockResponse = GitHubRepositorySearchResult(
                total = 10,
                items = persistentListOf(createRepo(1, "repo1"))
            )
            coEvery { gitHubDataSource.fetchGitHubRepositories(query, 1) } returns mockResponse
            coEvery { gitHubDataSource.saveSearchQuery(any()) } returns Unit

            // when
            repository.searchGitHubRepositories(query)
            advanceTimeBy(3.seconds)

            // then
            val result = repository.storedQueryResults.value
            assertTrue((result as Result.Success).data.canLoadMore)
        }

    private fun createRepo(id: Long, name: String) = GitHubRepositoryListItem(
        id = id,
        name = name,
        description = "desc",
        repositoryLink = "link",
        stars = 0,
        forksCount = 0,
        createdAt = LocalDateTime.now(),
        lastUpdatedAt = LocalDateTime.now(),
        owner = GitHubRepositoryOwner("owner_name", "avatar_url", "url")
    )
}
