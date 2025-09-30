package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetAllCharactersUseCase
import com.ernesto.rickandmortycompose.feature.testutils.CharacterModelDiffCallback
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetAllCharactersUseCase
    private lateinit var viewModel: CharactersListViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN use case returns PagingData WHEN viewModel is created THEN characters flow emits expected PagingData`() =
        runTest {
            //GIVEN
            val characters = listOf(
                CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url1"),
                CharacterModel(2, "Morty", "Alive", "Human", "", "Male", "url2")
            )
            val expectedPagingData = PagingData.from(characters)
            coEvery { useCase() } returns flowOf(expectedPagingData)

            //WHEN
            viewModel = CharactersListViewModel(useCase)

            //THEN
            viewModel.uiState.test {
                assert(awaitItem() is CharactersUiState.Loading)
                val successState = awaitItem()
                assert(successState is CharactersUiState.Success)

                val pagingFlow = (successState as CharactersUiState.Success).characters

                pagingFlow.test {
                    val emitted = awaitItem()

                    val differ = AsyncPagingDataDiffer(
                        diffCallback = CharacterModelDiffCallback(),
                        updateCallback = object : ListUpdateCallback {
                            override fun onInserted(position: Int, count: Int) {}
                            override fun onRemoved(position: Int, count: Int) {}
                            override fun onMoved(fromPosition: Int, toPosition: Int) {}
                            override fun onChanged(position: Int, count: Int, payload: Any?) {}
                        },
                        mainDispatcher = testDispatcher,
                        workerDispatcher = testDispatcher
                    )
                    differ.submitData(emitted)
                    advanceUntilIdle()
                    assertEquals(characters, differ.snapshot().items)
                    cancelAndConsumeRemainingEvents()
                }
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN use case throws exception WHEN viewModel is created THEN uiState is Error`() =
        runTest {
            //GIVEN
            coEvery { useCase() } throws RuntimeException("Network error")

            //WHEN
            viewModel = CharactersListViewModel(useCase)

            //THEN
            viewModel.uiState.test {
                assert(awaitItem() is CharactersUiState.Loading)
                val errorState = awaitItem()
                assert(errorState is CharactersUiState.Error)
                assertEquals("Network error", (errorState as CharactersUiState.Error).message)
                cancelAndConsumeRemainingEvents()
            }
        }
}