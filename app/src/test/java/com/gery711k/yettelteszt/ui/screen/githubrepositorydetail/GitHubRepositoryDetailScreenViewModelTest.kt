package com.gery711k.yettelteszt.ui.screen.githubrepositorydetail

import app.cash.turbine.test
import com.gery711k.yettelteszt.domain.model.github.GitHubRepositoryListItem
import com.gery711k.yettelteszt.domain.usecase.github.GetRepositoryDetailByIdUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private val getRepositoryDetailByIdUseCase: GetRepositoryDetailByIdUseCase = mockk()

    private val repositoryId = 123L

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState show correct item from results if it exists`() = runTest {
        // given
        val item = mockk<GitHubRepositoryListItem> { every { id } returns repositoryId }

        every { getRepositoryDetailByIdUseCase(repositoryId) } returns MutableStateFlow(item)
        val viewModel = createViewModel()

        // when
        viewModel.uiState.test {
            skipItems(1)

            // then
            assertEquals(item, awaitItem())
        }

    }

    @Test
    fun `uiState is null if repository not found`() = runTest {
        // given
        every { getRepositoryDetailByIdUseCase(repositoryId) } returns MutableStateFlow(null)
        val viewModel = createViewModel()

        // when
        viewModel.uiState.test {
            // then
            assertEquals(null, awaitItem())
        }
    }

    private fun createViewModel() = GitHubRepositoryDetailScreenViewModel(
        repositoryId = repositoryId,
        getRepositoryDetailByIdUseCase = getRepositoryDetailByIdUseCase
    )
}