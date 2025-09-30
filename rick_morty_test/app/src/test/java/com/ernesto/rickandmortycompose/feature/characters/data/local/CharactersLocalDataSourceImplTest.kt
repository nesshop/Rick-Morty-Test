package com.ernesto.rickandmortycompose.feature.characters.data.local

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CharactersLocalDataSourceImplTest {

    private lateinit var localDataSource: CharactersLocalDataSourceImpl

    @Before
    fun setup() {
        localDataSource = CharactersLocalDataSourceImpl()
    }

    @Test
    fun `GIVEN characters saved THEN getAllCharacters returns them`() {
        val characters = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1"),
            CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2")
        )

        localDataSource.saveCharacters(page = 1, characters = characters)

        val result = localDataSource.getAllCharacters(1)
        assertEquals(characters, result)
    }

    @Test
    fun `GIVEN characters saved THEN getCharacterById returns correct character`() {
        val character = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
        localDataSource.saveCharacter(character)

        val result = localDataSource.getCharacterById(1)
        assertEquals(character, result)
    }

    @Test
    fun `GIVEN page saved THEN getCharacterById returns correct character from cacheById`() {
        val characters = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1"),
            CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2")
        )
        localDataSource.saveCharacters(page = 1, characters = characters)

        val rick = localDataSource.getCharacterById(1)
        val morty = localDataSource.getCharacterById(2)

        assertEquals(characters[0], rick)
        assertEquals(characters[1], morty)
    }
}
