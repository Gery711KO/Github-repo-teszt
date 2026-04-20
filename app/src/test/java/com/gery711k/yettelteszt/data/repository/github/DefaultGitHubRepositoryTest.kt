package com.gery711k.yettelteszt.data.repository.github

import app.cash.turbine.test
import com.gery711k.yettelteszt.data.datasource.github.GitHubDataSource
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryOwner
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositorySearchResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultGitHubRepositoryTest {

    private val gitHubDataSource: GitHubDataSource = mockk(relaxUnitFun = true)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var repository: DefaultGitHubRepository

    @BeforeEach
    fun setup() {
        every { gitHubDataSource.fetchSearchHistory() } returns flowOf(emptyList())
        coEvery { gitHubDataSource.saveSearchQuery(any()) } returns Unit
        coEvery { gitHubDataSource.fetchGitHubRepositories(any(), any()) } coAnswers {
            GitHubRepositorySearchResult(
                total = 1,
                items = persistentListOf(
                   createRepo(123L, "repo1")
                )
            )
        }

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

            advanceUntilIdle()

            // then
            val result = repository.storedQueryResults.value
            assertTrue(result is Result.Success)
            assertEquals(1, (result as Result.Success).data.list.size)
            assertEquals("repo1", result.data.list[0].name)

            coVerify { gitHubDataSource.fetchGitHubRepositories(query, 1) }
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
    fun `searchGitHubRepositories should show loading state first`() = runTest(testDispatcher) {
        // given
        val query = "test"

        // when
        repository.storedQueryResults.test {
            repository.searchGitHubRepositories(query)

            // skip the initial null
            skipItems(1)

            // then
            assertTrue(awaitItem() is Result.Loading)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `searchGitHubRepositories should set the storedQuery to null if cancelled`() = runTest(testDispatcher) {
        // given
        val query = "test"

        coEvery {
            gitHubDataSource.fetchGitHubRepositories(any(), any())
        } throws CancellationException("Test cancellation")

        // when
        repository.searchGitHubRepositories(query)

        advanceUntilIdle()

        // then
        assertEquals(null, repository.storedQuery.value)
    }

    @Test
    fun `searchHistory should reflect data source flow`() = runTest(testDispatcher) {
        // given
        val history = listOf("query1", "query2")
        every { gitHubDataSource.fetchSearchHistory() } returns flowOf(history)

        // Need to recreate repository to collect from the new flow
        repository = DefaultGitHubRepository(gitHubDataSource, testDispatcher)

        // then
        assertEquals(history, repository.searchHistory.first())
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

    @Test
    fun `saveSearchQuery should call the correct dataSource function`() = runTest {
        // given
        val query = "test"

        // when
        repository.saveSearchQuery(query)

        // then
        coVerify { gitHubDataSource.saveSearchQuery(query) }
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
