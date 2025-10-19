package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import android.content.Context
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import app.cash.turbine.test
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetAllCharactersUseCase
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.SearchCharactersUseCase
import com.ernesto.rickandmortycompose.feature.testutils.CharacterModelDiffCallback
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
    private lateinit var getAllCharactersUseCase: GetAllCharactersUseCase
    private lateinit var searchCharactersUseCase: SearchCharactersUseCase
    private lateinit var viewModel: CharactersListViewModel
    private lateinit var context : Context

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAllCharactersUseCase = mockk()
        searchCharactersUseCase = mockk()
        context = mockk()
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
                CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList()),
                CharacterModel(2, "Morty", "Alive", "Human", "", "Male", "url2", emptyList())
            )
            val expectedPagingData = PagingData.from(characters)
            coEvery { getAllCharactersUseCase() } returns flowOf(expectedPagingData)
            coEvery { searchCharactersUseCase(any()) } returns flowOf(PagingData.empty())

            //WHEN
            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase, context)
            advanceUntilIdle()

            //THEN
            viewModel.uiState.test {
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
            coEvery { getAllCharactersUseCase() } throws RuntimeException("Network error")
            coEvery { searchCharactersUseCase(any()) } returns flowOf(PagingData.empty())

            //WHEN
            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase, context)
            advanceUntilIdle()

            //THEN
            viewModel.uiState.test {
                val errorState = awaitItem()
                assert(errorState is CharactersUiState.Error)
                assertEquals("Network error", (errorState as CharactersUiState.Error).message)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    @Suppress("UnusedFlow")
    fun `GIVEN empty search query WHEN viewModel is initialized THEN loads all characters`() =
        runTest {
            //GIVEN
            val characters = listOf(
                CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList()),
                CharacterModel(2, "Morty", "Alive", "Human", "", "Male", "url2", emptyList())
            )
            val expectedPagingData = PagingData.from(characters)
            coEvery { getAllCharactersUseCase() } returns flowOf(expectedPagingData)

            //WHEN
            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase, context)
            advanceUntilIdle()

            //THEN
            coVerify(exactly = 1) { getAllCharactersUseCase() }
            coVerify(exactly = 0) { searchCharactersUseCase(any()) }

            val state = viewModel.uiState.value
            assert(state is CharactersUiState.Success)
        }

    @Test
    fun `GIVEN search query WHEN updateSearchQuery is called THEN calls searchCharactersUseCase with debounce`() =
        runTest {
            //GIVEN
            val query = "Rick"
            val searchResults = listOf(
                CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList())
            )
            val expectedPagingData = PagingData.from(searchResults)
            coEvery { getAllCharactersUseCase() } returns flowOf(PagingData.empty())
            coEvery { searchCharactersUseCase(query) } returns flowOf(expectedPagingData)

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase, context)
            advanceUntilIdle()

            //WHEN
            viewModel.updateSearchQuery(query)
            advanceUntilIdle()

            //THEN
            assertEquals(query, viewModel.searchQuery.value)
            coVerify { searchCharactersUseCase(query) }
        }

    @Test
    fun `GIVEN search query WHEN clearSearchQuery is called THEN resets to all characters`() =
        runTest {
            //GIVEN
            val query = "Rick"
            coEvery { getAllCharactersUseCase() } returns flowOf(PagingData.empty())
            coEvery { searchCharactersUseCase(query) } returns flowOf(PagingData.empty())

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase, context)
            advanceUntilIdle()
            viewModel.updateSearchQuery(query)
            advanceUntilIdle()

            //WHEN
            viewModel.clearSearchQuery()
            advanceUntilIdle()

            //THEN
            assertEquals("", viewModel.searchQuery.value)

            coVerify(atLeast = 2) { getAllCharactersUseCase() }
        }

    @Test
    fun `GIVEN rapid typing WHEN updateSearchQuery is called multiple times THEN debounces and only searches final query`() =
        runTest {
            //GIVEN
            coEvery { getAllCharactersUseCase() } returns flowOf(PagingData.empty())
            coEvery { searchCharactersUseCase(any()) } returns flowOf(PagingData.empty())

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase, context)
            advanceUntilIdle()

            //WHEN
            viewModel.updateSearchQuery("R")
            advanceTimeBy(100)
            viewModel.updateSearchQuery("Ri")
            advanceTimeBy(100)
            viewModel.updateSearchQuery("Ric")
            advanceTimeBy(100)
            viewModel.updateSearchQuery("Rick")
            advanceUntilIdle()

            //THEN
            coVerify(exactly = 1) { searchCharactersUseCase("Rick") }
            coVerify(exactly = 0) { searchCharactersUseCase("R") }
            coVerify(exactly = 0) { searchCharactersUseCase("Ri") }
            coVerify(exactly = 0) { searchCharactersUseCase("Ric") }
        }

    @Test
    fun `GIVEN same query twice WHEN updateSearchQuery is called THEN distinctUntilChanged prevents duplicate search`() =
        runTest {
            //GIVEN
            val query = "Rick"
            coEvery { getAllCharactersUseCase() } returns flowOf(PagingData.empty())
            coEvery { searchCharactersUseCase(query) } returns flowOf(PagingData.empty())

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase, context)
            advanceUntilIdle()

            //WHEN
            viewModel.updateSearchQuery(query)
            advanceUntilIdle()
            viewModel.updateSearchQuery(query)
            advanceUntilIdle()

            //THEN
            coVerify(exactly = 1) { searchCharactersUseCase(query) }
        }

    @Test
    fun `GIVEN blank query with spaces WHEN updateSearchQuery is called THEN uses getAllCharacters`() =
        runTest {
            //GIVEN
            coEvery { getAllCharactersUseCase() } returns flowOf(PagingData.empty())

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase, context)
            advanceUntilIdle()

            //WHEN
            viewModel.updateSearchQuery("   ")
            advanceUntilIdle()

            //THEN
            coVerify(atLeast = 2) { getAllCharactersUseCase() }
            coVerify(exactly = 0) { searchCharactersUseCase(any()) }
        }

}