package com.gery711k.yettelteszt.ui.screen.githubrepositorylist

import app.cash.turbine.test
import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.usecase.github.GetSearchHistoryUseCase
import com.gery711k.yettelteszt.domain.usecase.github.GetStoredQueryResultsUseCase
import com.gery711k.yettelteszt.domain.usecase.github.LoadMoreUseCase
import com.gery711k.yettelteszt.domain.usecase.github.SearchGitHubRepositoriesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubRepositoryListScreenViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val searchUseCase: SearchGitHubRepositoriesUseCase = mockk()
    private val loadMoreUseCase: LoadMoreUseCase = mockk()
    private val getResultsUseCase: GetStoredQueryResultsUseCase = mockk()
    private val getHistoryUseCase: GetSearchHistoryUseCase = mockk()

    private val mockResultsFlow = MutableStateFlow<Result<PaginatedList<GitHubRepositoryListItem>>?>(null)
    private val mockHistoryFlow = MutableStateFlow<List<String>>(emptyList())

    private lateinit var viewModel: GitHubRepositoryListScreenViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getResultsUseCase() } returns mockResultsFlow
        every { getHistoryUseCase() } returns mockHistoryFlow
        
        viewModel = GitHubRepositoryListScreenViewModel(
            searchGitHubRepositoriesUseCase = searchUseCase,
            loadMoreUseCase = loadMoreUseCase,
            getStoredQueryResultsUseCase = getResultsUseCase,
            getSearchHistoryUseCase = getHistoryUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState updates when use case flows emit`() = runTest(testDispatcher) {
        // given
        val mockItems = listOf<GitHubRepositoryListItem>(mockk())
        val mockResult = Result.Success(
            data = PaginatedList(
                list = mockItems.toImmutableList(),
                canLoadMore = true,
            )
        )
        val mockHistory = listOf("query1")

        // when
        viewModel.uiState.test {
            // skip initial value with empty lists
            awaitItem()

            // update flows
            mockResultsFlow.value = mockResult
            mockHistoryFlow.value = mockHistory

            // then
            val currentState = expectMostRecentItem()
            assertEquals(mockResult, currentState?.listItems)
            assertEquals(mockHistory.toImmutableList(), currentState?.searchHistory)
            assertEquals(true, currentState?.canLoadMore)
        }
    }

    @Test
    fun `searchRepositories calls use case`() = runTest(testDispatcher) {
        // given
        val query = "android"
        coEvery { searchUseCase(query) } returns Unit

        // when
        viewModel.searchRepositories(query)
        advanceUntilIdle()

        // then
        coVerify { searchUseCase(query) }
    }

    @Test
    fun `loadMore calls use case`() = runTest(testDispatcher) {
        // given
        coEvery { loadMoreUseCase() } returns Unit

        // when
        viewModel.loadMore()
        advanceUntilIdle()

        // then
        coVerify { loadMoreUseCase() }
    }
}