package com.ernesto.rickandmortycompose.feature.characters.data.repository

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterRepositoryImplTest {

    private val remoteDataSource: CharactersRemoteDataSource = mockk()
    private val localDataSource: CharactersLocalDataSource = mockk()
    private val repository = CharacterRepositoryImpl(remoteDataSource, localDataSource)

    @Test
    fun `WHEN getAllCharacters called THEN returns Flow`() = runTest {
        //WHEN
        val result = repository.getAllCharacters()

        //THEN
        assertNotNull(result)
        assertTrue(result is Flow<PagingData<CharacterModel>>)
        assertTrue(result is Flow<*>)
    }

    @Test
    fun `WHEN getAllCharacters called THEN uses correct page size`() = runTest {
        //THEN
        assertEquals(20, CharacterRepositoryImpl.MAX_ITEMS)
    }
    @Test
    fun `WHEN getAllCharacters called THEN uses correct prefetch distance`() = runTest {
        //THEN
        assertEquals(5, CharacterRepositoryImpl.PREFETCH_DISTANCE)
    }

    @Test
    fun `WHEN getAllCharacters called multiple times THEN creates different Flow instances`() =
        runTest {
            //WHEN
            val result1 = repository.getAllCharacters()
            val result2 = repository.getAllCharacters()

            //THEN
            assertNotEquals(result1, result2)
            assertTrue(result1 is Flow<PagingData<CharacterModel>>)
            assertTrue(result2 is Flow<PagingData<CharacterModel>>)
        }

    @Test
    fun `WHEN repository is created THEN implements CharacterRepository interface`() {
        // THEN
        assertTrue(
            "Repository should implement CharacterRepository",
            repository is CharacterRepository
        )
    }

    @Test
    fun `WHEN repository is created THEN is properly initialized`() {
        // THEN
        assertNotNull("Repository should be properly initialized", repository)
    }

    @Test
    fun `GIVEN character exists in local THEN returns cached character`() = runTest {
        val cached = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
        coEvery { localDataSource.getCharacterById(1) } returns cached

        val result = repository.getCharacterById(1)

        assertEquals(cached.toDomain(), result)
        coVerify(exactly = 0) { remoteDataSource.getCharacterById(any()) }
    }

    @Test
    fun `GIVEN character not in local but exists in remote THEN saves and returns character`() = runTest {
        val remoteChar = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url")
        coEvery { localDataSource.getCharacterById(1) } returns null
        coEvery { remoteDataSource.getCharacterById(1) } returns remoteChar
        coEvery { localDataSource.saveCharacter(remoteChar) } just Runs

        val result = repository.getCharacterById(1)

        assertEquals(remoteChar.toDomain(), result)
        coVerify { localDataSource.saveCharacter(remoteChar) }
    }
}