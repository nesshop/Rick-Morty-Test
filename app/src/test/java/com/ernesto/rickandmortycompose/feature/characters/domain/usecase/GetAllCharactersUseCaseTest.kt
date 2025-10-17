package com.ernesto.rickandmortycompose.feature.characters.domain.usecase

import androidx.paging.PagingData
import app.cash.turbine.test
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Test

class GetAllCharactersUseCaseTest {

    private val repository: CharacterRepository = mockk()
    private val useCase = GetAllCharactersUseCase(repository)

    @Test
    fun `WHEN use case is invoked THEN repository getAllCharacters is called`() = runTest {
        //GIVEN
        val mockPagingData = PagingData.from(emptyList<CharacterModel>())
        coEvery { repository.getAllCharacters() } returns flowOf(mockPagingData)

        //WHEN
        useCase.invoke()

        //THEN
        coVerify(exactly = 1) { repository.getAllCharacters() }
    }

    @Test
    fun `WHEN use case is invoked THEN returns flow that emits PagingData`() = runTest {
        //GIVEN
        val characters = listOf(
            CharacterModel(1, "Rick", "Alive", "Human", "", "Male", ""),
            CharacterModel(2, "Morty", "Alive", "Human", "", "Male", "")
        )
        val mockPagingData = PagingData.from(characters)
        coEvery { repository.getAllCharacters() } returns flowOf(mockPagingData)

        //WHEN
        val result = useCase.invoke()

        //THEN
        result.test {
            val emittedData = awaitItem()
            assertNotNull(emittedData)
            awaitComplete()
        }
    }

    @Test
    fun `WHEN repository throws exception THEN use case propagates exception`() = runTest {
        //GIVEN
        val exception = RuntimeException("Network error")
        coEvery { repository.getAllCharacters() } throws exception

        //WHEN & THEN
        assertThrows(RuntimeException::class.java) {
            runTest {
                useCase.invoke().test {
                    awaitError()
                }
            }
        }
    }
}