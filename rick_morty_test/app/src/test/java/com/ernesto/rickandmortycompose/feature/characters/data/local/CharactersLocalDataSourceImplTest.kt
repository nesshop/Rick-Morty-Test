package com.ernesto.rickandmortycompose.feature.characters.data.local

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class CharactersLocalDataSourceImplTest {

    private val localDataSource = CharactersLocalDataSourceImpl()

    @Test
    fun `GIVEN empty cache WHEN getAllCharacters THEN returns null`() = runTest {
        //GIVEN
        val page = 1

        //WHEN
        val result = localDataSource.getAllCharacters(page)

        //THEN
        assertNull(result)
    }

    @Test
    fun `GIVEN characters saved in cache WHEN getAllCharacters THEN returns same characters`() = runTest {
        // GIVEN
        val page = 1
        val characters = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
        )
        localDataSource.saveCharacters(page, characters)

        // WHEN
        val result = localDataSource.getAllCharacters(page)

        // THEN
        assertEquals(characters, result)
    }

    @Test
    fun `GIVEN characters saved for page 1 WHEN save different characters for same page THEN overwrites cache`() = runTest {
        // GIVEN
        val page = 1
        val characters1 = listOf(CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1"))
        val characters2 = listOf(CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2"))
        localDataSource.saveCharacters(page, characters1)

        // WHEN
        localDataSource.saveCharacters(page, characters2)
        val result = localDataSource.getAllCharacters(page)

        // THEN
        assertEquals(characters2, result)
    }
}