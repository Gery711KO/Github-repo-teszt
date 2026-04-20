package com.gery711k.yettelteszt.domain.usecase.github

import app.cash.turbine.test
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.model.github.QueryData
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitHubUseCasesTest {

    private val repository: GitHubRepository = mockk(
        relaxUnitFun = true
    )
    
    private lateinit var searchUseCase: SearchGitHubRepositoriesUseCase
    private lateinit var loadMoreUseCase: LoadMoreUseCase
    private lateinit var getResultsUseCase: GetStoredQueryResultsUseCase
    private lateinit var getHistoryUseCase: GetSearchHistoryUseCase
    private lateinit var getDetailUseCase: GetRepositoryDetailByIdUseCase


    @BeforeEach
    fun setup() {
        searchUseCase = SearchGitHubRepositoriesUseCase(repository)
        loadMoreUseCase = LoadMoreUseCase(repository)
        getResultsUseCase = GetStoredQueryResultsUseCase(repository)
        getHistoryUseCase = GetSearchHistoryUseCase(repository)
        getDetailUseCase = GetRepositoryDetailByIdUseCase(repository)
    }

    @Test
    fun `SearchGitHubRepositoriesUseCase calls repository`() = runTest {
        // given
        val query = "test"

        // when
        searchUseCase(query)
        
        // then
        coVerify { repository.searchGitHubRepositories(query) }
    }

    @Test
    fun `LoadMoreUseCase calls repository`() = runTest {
        // given
        val queryData = QueryData(queryString = "test", page = 1)

        coEvery { repository.storedQuery } returns MutableStateFlow(queryData)
        coEvery { repository.storedQueryResults } returns MutableStateFlow(
            Result.Success(
                PaginatedList(
                    list = persistentListOf(
                        mockk<GitHubRepositoryListItem> { every { id } returns 123L },
                        mockk<GitHubRepositoryListItem> { every { id } returns 456L },
                    ),
                    canLoadMore = true
                )
            )
        )
        
        // when
        loadMoreUseCase()
        
        // then
        coVerify {
            repository.searchGitHubRepositories(queryData.queryString, queryData.page + 1)
        }
    }

    @Test
    fun `GetStoredQueryResultsUseCase returns flow from repository`() {
        // given
        val mockFlow = MutableStateFlow<Result<PaginatedList<GitHubRepositoryListItem>>?>(null)
        every { repository.storedQueryResults } returns mockFlow
        
        // when
        val result = getResultsUseCase()
        
        // then
        assertEquals(mockFlow, result)
    }

    @Test
    fun `GetSearchHistoryUseCase returns flow from repository`() {
        // given
        val mockFlow = MutableStateFlow<List<String>>(emptyList())
        every { repository.searchHistory } returns mockFlow
        
        // when
        val result = getHistoryUseCase()
        
        // then
        assertEquals(mockFlow, result)
    }

    @Test
    fun `GetRepositoryDetailByIdUseCase returns the correct item`() = runTest {
        // given
        val repositoryId = 123L
        val githubItem1 = mockk<GitHubRepositoryListItem> { every { id } returns repositoryId }
        val githubItem2 = mockk<GitHubRepositoryListItem> { every { id } returns 456L }
        val mockFlow = MutableStateFlow(
            Result.Success(
                PaginatedList(
                    list = persistentListOf(githubItem1, githubItem2),
                    canLoadMore = true
                )
            )
        )

        every { repository.storedQueryResults } returns mockFlow

        // when
        getDetailUseCase(repositoryId).test {
            // then
            assertEquals(githubItem1, awaitItem())
        }
    }
}