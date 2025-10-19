package com.ernesto.rickandmortycompose.feature.characters.ui.detail

import android.content.Context
import app.cash.turbine.test
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetCharacterByIdUseCase
import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel
import com.ernesto.rickandmortycompose.feature.episodes.domain.usecase.GetEpisodeForCharacterUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: GetCharacterByIdUseCase
    private lateinit var getEpisodeForCharacterUseCase: GetEpisodeForCharacterUseCase
    private lateinit var viewModel: DetailViewModel
    private lateinit var context: Context

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        useCase = mockk()
        getEpisodeForCharacterUseCase = mockk()
        context = mockk()
        viewModel = DetailViewModel(useCase, getEpisodeForCharacterUseCase, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN useCase returns character WHEN loadCharacter called THEN uiState emits Loading and Success`() =
        runTest {
            val character = CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url", emptyList())
            coEvery { useCase(1) } returns character

            viewModel.uiState.test {
                viewModel.loadCharacter(1)
                advanceUntilIdle()

                assert(awaitItem() is DetailUiState.Loading)
                val success = awaitItem()
                assert(success is DetailUiState.Success)
                assertEquals(character, (success as DetailUiState.Success).character)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN useCase throws exception WHEN loadCharacter called THEN uiState emits Loading and Error`() =
        runTest {
            coEvery { useCase(1) } throws RuntimeException("Network error")

            viewModel.uiState.test {
                viewModel.loadCharacter(1)
                advanceUntilIdle()

                assert(awaitItem() is DetailUiState.Loading)
                val error = awaitItem()
                assert(error is DetailUiState.Error)
                assertEquals("Network error", (error as DetailUiState.Error).message)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN useCase returns episodes and state is Success WHEN loadEpisodes called THEN uiState updates with episodes`() =
        runTest {
            // Given
            val character = CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url", emptyList())
            val episodeIds = listOf("1", "2", "3")
            val episodes = listOf(
                EpisodeModel("1","Pilot", "S01E01"),
                EpisodeModel("1","Lawnmower Dog", "S01E02"),
                EpisodeModel("1","Anatomy Park", "S01E03")
            )

            coEvery { useCase(1) } returns character
            coEvery { getEpisodeForCharacterUseCase(episodeIds) } returns episodes

            viewModel.uiState.test {
                // When
                viewModel.loadCharacter(1)
                advanceUntilIdle()

                skipItems(2) // Skip Loading and initial Success

                viewModel.loadEpisodes(episodeIds)
                advanceUntilIdle()

                // Then
                val updatedState = awaitItem()
                assert(updatedState is DetailUiState.Success)
                assertEquals(episodes, (updatedState as DetailUiState.Success).episodes)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN empty episode list WHEN loadEpisodes called THEN episodes remain empty in Success state`() =
        runTest {
            // Given
            val character = CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url", emptyList())
            val episodeIds = emptyList<String>()
            val episodes = emptyList<EpisodeModel>()

            coEvery { useCase(1) } returns character
            coEvery { getEpisodeForCharacterUseCase(episodeIds) } returns episodes

            // When
            viewModel.loadCharacter(1)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.loadEpisodes(episodeIds)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - verify current state without waiting for emission
            val currentState = viewModel.uiState.value
            assert(currentState is DetailUiState.Success)
            assertEquals(emptyList<EpisodeModel>(), (currentState as DetailUiState.Success).episodes)
        }

    @Test
    fun `GIVEN useCase throws exception WHEN loadEpisodes called THEN uiState emits Error`() =
        runTest {
            // Given
            val character = CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url", emptyList())
            val episodeIds = listOf("1", "2", "3")

            coEvery { useCase(1) } returns character
            coEvery { getEpisodeForCharacterUseCase(episodeIds) } throws RuntimeException("Failed to load episodes")

            viewModel.uiState.test {
                // When
                viewModel.loadCharacter(1)
                advanceUntilIdle()

                skipItems(2) // Skip Loading and initial Success

                viewModel.loadEpisodes(episodeIds)
                advanceUntilIdle()

                // Then
                val error = awaitItem()
                assert(error is DetailUiState.Error)
                assertEquals("Failed to load episodes", (error as DetailUiState.Error).message)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `GIVEN state is not Success WHEN loadEpisodes called THEN uiState does not update`() =
        runTest {
            // Given
            val episodeIds = listOf("1", "2", "3")
            val episodes = listOf(
                EpisodeModel("1","Pilot", "S01E01" )
            )

            coEvery { useCase(1) } throws RuntimeException("Character not found")
            coEvery { getEpisodeForCharacterUseCase(episodeIds) } returns episodes

            viewModel.uiState.test {
                // When
                viewModel.loadCharacter(1)
                advanceUntilIdle()

                skipItems(1) // Skip Loading

                val errorState = awaitItem()
                assert(errorState is DetailUiState.Error)

                viewModel.loadEpisodes(episodeIds)
                advanceUntilIdle()

                // Then - state should remain Error, no new emission
                expectNoEvents()

                cancelAndConsumeRemainingEvents()
            }
        }
}
