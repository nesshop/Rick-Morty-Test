package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.CharacterPaginator
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetAllCharactersUseCase
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.SearchCharactersUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getAllCharactersUseCase = mockk()
        searchCharactersUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN empty search query WHEN viewModel is initialized THEN loads all characters`() =
        runTest {
            // GIVEN
            val mockPaginator = mockk<CharacterPaginator>(relaxed = true)
            every { getAllCharactersUseCase() } returns mockPaginator

            // WHEN
            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

            // Colecta el StateFlow para activarlo
            backgroundScope.launch {
                viewModel.uiState.collect()
            }

            advanceUntilIdle()

            // THEN
            verify(exactly = 1) { getAllCharactersUseCase() }
            verify(exactly = 0) { searchCharactersUseCase(any()) }

            val state = viewModel.uiState.value
            assert(state is CharactersUiState.Success)
        }

    @Test
    fun `GIVEN search query WHEN updateSearchQuery is called THEN calls searchCharactersUseCase with debounce`() =
        runTest {
            // GIVEN
            val query = "Rick"
            val allCharactersPaginator = mockk<CharacterPaginator>(relaxed = true)
            val searchPaginator = mockk<CharacterPaginator>(relaxed = true)

            every { getAllCharactersUseCase() } returns allCharactersPaginator
            every { searchCharactersUseCase(query) } returns searchPaginator

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

            // Colecta el StateFlow para activarlo
            backgroundScope.launch {
                viewModel.uiState.collect()
            }

            advanceUntilIdle()

            // WHEN
            viewModel.updateSearchQuery(query)
            advanceUntilIdle()

            // THEN
            assertEquals(query, viewModel.searchQuery.value)
            verify { searchCharactersUseCase(query) }
        }

    @Test
    fun `GIVEN search query WHEN clearSearchQuery is called THEN resets to all characters`() =
        runTest {
            // GIVEN
            val query = "Rick"
            val allCharactersPaginator = mockk<CharacterPaginator>(relaxed = true)
            val searchPaginator = mockk<CharacterPaginator>(relaxed = true)

            every { getAllCharactersUseCase() } returns allCharactersPaginator
            every { searchCharactersUseCase(query) } returns searchPaginator

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

            // Colecta el StateFlow para activarlo
            backgroundScope.launch {
                viewModel.uiState.collect()
            }

            advanceUntilIdle()
            viewModel.updateSearchQuery(query)
            advanceUntilIdle()

            // WHEN
            viewModel.clearSearchQuery()
            advanceUntilIdle()

            // THEN
            assertEquals("", viewModel.searchQuery.value)
            verify(atLeast = 2) { getAllCharactersUseCase() }
        }

    @Test
    fun `GIVEN rapid typing WHEN updateSearchQuery is called multiple times THEN debounces and only searches final query`() =
        runTest {
            // GIVEN
            val allCharactersPaginator = mockk<CharacterPaginator>(relaxed = true)
            val searchPaginator = mockk<CharacterPaginator>(relaxed = true)

            every { getAllCharactersUseCase() } returns allCharactersPaginator
            every { searchCharactersUseCase(any()) } returns searchPaginator

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

            // Colecta el StateFlow para activarlo
            backgroundScope.launch {
                viewModel.uiState.collect()
            }

            advanceUntilIdle()

            // WHEN
            viewModel.updateSearchQuery("R")
            advanceTimeBy(100)
            viewModel.updateSearchQuery("Ri")
            advanceTimeBy(100)
            viewModel.updateSearchQuery("Ric")
            advanceTimeBy(100)
            viewModel.updateSearchQuery("Rick")
            advanceUntilIdle()

            // THEN
            verify(exactly = 1) { searchCharactersUseCase("Rick") }
            verify(exactly = 0) { searchCharactersUseCase("R") }
            verify(exactly = 0) { searchCharactersUseCase("Ri") }
            verify(exactly = 0) { searchCharactersUseCase("Ric") }
        }

    @Test
    fun `GIVEN same query twice WHEN updateSearchQuery is called THEN distinctUntilChanged prevents duplicate search`() =
        runTest {
            // GIVEN
            val query = "Rick"
            val allCharactersPaginator = mockk<CharacterPaginator>(relaxed = true)
            val searchPaginator = mockk<CharacterPaginator>(relaxed = true)

            every { getAllCharactersUseCase() } returns allCharactersPaginator
            every { searchCharactersUseCase(query) } returns searchPaginator

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

            // Colecta el StateFlow para activarlo
            backgroundScope.launch {
                viewModel.uiState.collect()
            }

            advanceUntilIdle()

            // WHEN
            viewModel.updateSearchQuery(query)
            advanceUntilIdle()
            viewModel.updateSearchQuery(query)
            advanceUntilIdle()

            // THEN
            verify(exactly = 1) { searchCharactersUseCase(query) }
        }

    @Test
    fun `GIVEN blank query with spaces WHEN updateSearchQuery is called THEN uses getAllCharacters`() =
        runTest {
            // GIVEN
            val allCharactersPaginator = mockk<CharacterPaginator>(relaxed = true)
            every { getAllCharactersUseCase() } returns allCharactersPaginator

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

            // Colecta el StateFlow para activarlo
            backgroundScope.launch {
                viewModel.uiState.collect()
            }

            advanceUntilIdle()

            // WHEN
            viewModel.updateSearchQuery("   ")
            advanceUntilIdle()

            // THEN
            verify(atLeast = 2) { getAllCharactersUseCase() }
            verify(exactly = 0) { searchCharactersUseCase(any()) }
        }

    @Test
    fun `GIVEN search query changes from Rick to Morty WHEN updateSearchQuery is called THEN searches both correctly`() =
        runTest {
            // GIVEN
            val allCharactersPaginator = mockk<CharacterPaginator>(relaxed = true)
            val rickPaginator = mockk<CharacterPaginator>(relaxed = true)
            val mortyPaginator = mockk<CharacterPaginator>(relaxed = true)

            every { getAllCharactersUseCase() } returns allCharactersPaginator
            every { searchCharactersUseCase("Rick") } returns rickPaginator
            every { searchCharactersUseCase("Morty") } returns mortyPaginator

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

            // Colecta el StateFlow para activarlo
            backgroundScope.launch {
                viewModel.uiState.collect()
            }

            advanceUntilIdle()

            // WHEN
            viewModel.updateSearchQuery("Rick")
            advanceUntilIdle()
            viewModel.updateSearchQuery("Morty")
            advanceUntilIdle()

            // THEN
            verify(exactly = 1) { searchCharactersUseCase("Rick") }
            verify(exactly = 1) { searchCharactersUseCase("Morty") }
        }

    @Test
    fun `GIVEN viewModel initialized THEN searchQuery initial value is empty`() {
        // GIVEN
        val mockPaginator = mockk<CharacterPaginator>(relaxed = true)
        every { getAllCharactersUseCase() } returns mockPaginator

        // WHEN
        viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

        // THEN
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `GIVEN searchQuery value WHEN clearSearchQuery is called THEN searchQuery becomes empty string`() =
        runTest {
            // GIVEN
            val allCharactersPaginator = mockk<CharacterPaginator>(relaxed = true)
            val searchPaginator = mockk<CharacterPaginator>(relaxed = true)

            every { getAllCharactersUseCase() } returns allCharactersPaginator
            every { searchCharactersUseCase(any()) } returns searchPaginator

            viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

            backgroundScope.launch {
                viewModel.uiState.collect()
            }

            viewModel.updateSearchQuery("Rick")
            advanceUntilIdle()

            // WHEN
            viewModel.clearSearchQuery()

            // THEN
            assertEquals("", viewModel.searchQuery.value)
        }

    @Test
    fun `GIVEN viewModel initialized THEN initial uiState is Success`() = runTest {
        // GIVEN
        val mockPaginator = mockk<CharacterPaginator>(relaxed = true)
        every { getAllCharactersUseCase() } returns mockPaginator

        // WHEN
        viewModel = CharactersListViewModel(getAllCharactersUseCase, searchCharactersUseCase)

        backgroundScope.launch {
            viewModel.uiState.collect()
        }

        advanceUntilIdle()

        // THEN
        val state = viewModel.uiState.value
        assert(state is CharactersUiState.Success)
    }

    @Test
    fun `GIVEN constants THEN MAX_ITEMS is 20 and PREFETCH_DISTANCE is 5`() {
        // THEN
        assertEquals(20, CharactersListViewModel.MAX_ITEMS)
        assertEquals(5, CharactersListViewModel.PREFETCH_DISTANCE)
    }
}