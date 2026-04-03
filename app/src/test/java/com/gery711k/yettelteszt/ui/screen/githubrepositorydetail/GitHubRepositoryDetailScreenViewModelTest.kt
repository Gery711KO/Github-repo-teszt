package com.gery711k.yettelteszt.ui.screen.githubrepositorydetail

import com.gery711k.yettelteszt.domain.model.PaginatedList
import com.gery711k.yettelteszt.domain.model.Result
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.usecase.github.GetStoredQueryResultsUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GitHubRepositoryDetailScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getResultsUseCase: GetStoredQueryResultsUseCase = mockk()
    private val mockResultsFlow = MutableStateFlow<Result<PaginatedList<GitHubRepositoryListItem>>?>(null)

    private lateinit var viewModel: GitHubRepositoryDetailScreenViewModel
    private val repositoryId = 123L

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getResultsUseCase() } returns mockResultsFlow
        
        viewModel = GitHubRepositoryDetailScreenViewModel(
            repositoryId = repositoryId,
            getStoredQueryResultsUseCase = getResultsUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState filters correct repository from results`() = runTest {
        // given
        val item1 = mockk<GitHubRepositoryListItem> { every { id } returns 123L }
        val item2 = mockk<GitHubRepositoryListItem> { every { id } returns 456L }
        
        val mockResult = Result.Success(
            data = PaginatedList(
                list = persistentListOf(item1, item2),
                canLoadMore = false
            )
        )
        mockResultsFlow.value = mockResult

        // when
        advanceUntilIdle()

        // then
        assertEquals(item1, viewModel.uiState.value)
    }

    @Test
    fun `uiState is null if repository not found`() = runTest {
        // given
        val item2 = mockk<GitHubRepositoryListItem> { every { id } returns 456L }
        
        val mockResult = Result.Success(
            data = PaginatedList(
                list = persistentListOf(item2),
                canLoadMore = false
            )
        )
        mockResultsFlow.value = mockResult

        // when
        advanceUntilIdle()

        // then
        assertEquals(null, viewModel.uiState.value)
    }
}