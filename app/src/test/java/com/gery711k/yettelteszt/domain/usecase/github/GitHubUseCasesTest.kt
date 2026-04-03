package com.gery711k.yettelteszt.domain.usecase.github

import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.repository.github.GitHubRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitHubUseCasesTest {

    private val repository: GitHubRepository = mockk()
    
    private lateinit var searchUseCase: SearchGitHubRepositoriesUseCase
    private lateinit var loadMoreUseCase: LoadMoreUseCase
    private lateinit var getResultsUseCase: GetStoredQueryResultsUseCase
    private lateinit var getHistoryUseCase: GetSearchHistoryUseCase

    @BeforeEach
    fun setup() {
        searchUseCase = SearchGitHubRepositoriesUseCase(repository)
        loadMoreUseCase = LoadMoreUseCase(repository)
        getResultsUseCase = GetStoredQueryResultsUseCase(repository)
        getHistoryUseCase = GetSearchHistoryUseCase(repository)
    }

    @Test
    fun `SearchGitHubRepositoriesUseCase calls repository`() = runTest {
        // given
        val query = "test"
        coEvery { repository.searchGitHubRepositories(query) } returns Unit
        
        // when
        searchUseCase(query)
        
        // then
        coVerify { repository.searchGitHubRepositories(query) }
    }

    @Test
    fun `LoadMoreUseCase calls repository`() = runTest {
        // given
        coEvery { repository.loadMore() } returns Unit
        
        // when
        loadMoreUseCase()
        
        // then
        coVerify { repository.loadMore() }
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
}