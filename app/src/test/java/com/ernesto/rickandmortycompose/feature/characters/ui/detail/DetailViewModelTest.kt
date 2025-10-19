package com.ernesto.rickandmortycompose.feature.characters.ui.detail

import android.content.Context
import app.cash.turbine.test
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetCharacterByIdUseCase
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
}
