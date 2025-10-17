package com.ernesto.rickandmortycompose.feature.characters.data.local

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
        //GIVEN
        val characters = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1"),
            CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2")
        )

        //WHEN
        localDataSource.saveCharacters(page = 1, characters = characters)

        //THEN
        val result = localDataSource.getAllCharacters(1)
        assertEquals(characters, result)
    }

    @Test
    fun `GIVEN characters saved THEN getCharacterById returns correct character`() {
        //GIVEN
        val character = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")

        //WHEN
        localDataSource.saveCharacter(character)

        //THEN
        val result = localDataSource.getCharacterById(1)
        assertEquals(character, result)
    }

    @Test
    fun `GIVEN page saved THEN getCharacterById returns correct character from cacheById`() {
        //GIVEN
        val characters = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1"),
            CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2")
        )

        //WHEN
        localDataSource.saveCharacters(page = 1, characters = characters)

        //THEN
        val rick = localDataSource.getCharacterById(1)
        val morty = localDataSource.getCharacterById(2)

        assertEquals(characters[0], rick)
        assertEquals(characters[1], morty)
    }

    @Test
    fun `GIVEN no characters saved WHEN getCharacterById THEN returns null`() {
        // WHEN
        val result = localDataSource.getCharacterById(1)

        // THEN
        assertNull(result)
    }

    @Test
    fun `GIVEN no characters in page WHEN getAllCharacters THEN returns null`() {
        // WHEN
        val result = localDataSource.getAllCharacters(1)

        // THEN
        assertNull(result)
    }

    @Test
    fun `GIVEN multiple pages saved WHEN getAllCharacters THEN returns correct page`() {
        // GIVEN
        val page1Characters = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
        )
        val page2Characters = listOf(
            CharacterResponse(3, "Summer", "Alive", "Human", "", "Female", "url3")
        )

        localDataSource.saveCharacters(page = 1, page1Characters)
        localDataSource.saveCharacters(page = 2, page2Characters)

        // WHEN
        val resultPage1 = localDataSource.getAllCharacters(1)
        val resultPage2 = localDataSource.getAllCharacters(2)

        // THEN
        assertEquals(page1Characters, resultPage1)
        assertEquals(page2Characters, resultPage2)
    }

    @Test
    fun `GIVEN multiple pages saved WHEN getCharacterById THEN finds character from any page`() {
        // GIVEN
        val page1Characters = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
        )
        val page2Characters = listOf(
            CharacterResponse(3, "Summer", "Alive", "Human", "", "Female", "url3")
        )

        localDataSource.saveCharacters(page = 1, page1Characters)
        localDataSource.saveCharacters(page = 2, page2Characters)

        // WHEN
        val rick = localDataSource.getCharacterById(1)
        val summer = localDataSource.getCharacterById(3)

        // THEN
        assertEquals(page1Characters[0], rick)
        assertEquals(page2Characters[0], summer)
    }

    @Test
    fun `GIVEN character saved individually then by page WHEN getCharacterById THEN returns page version`() {
        // GIVEN
        val individualCharacter = CharacterResponse(1, "Rick individual", "Alive", "Human", "", "Male", "url1")
        val pageCharacter = CharacterResponse(1, "Rick from page", "Dead", "Robot", "", "Female", "url2")

        localDataSource.saveCharacter(individualCharacter)
        localDataSource.saveCharacters(page = 1, listOf(pageCharacter)) // Sobrescribe

        // WHEN
        val result = localDataSource.getCharacterById(1)

        // THEN
        assertEquals(pageCharacter, result)
    }

    @Test
    fun `GIVEN page overwritten WHEN getAllCharacters THEN returns latest version`() {
        // GIVEN
        val originalCharacters = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
        )
        val updatedCharacters = listOf(
            CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2"),
            CharacterResponse(3, "Summer", "Alive", "Human", "", "Female", "url3")
        )

        localDataSource.saveCharacters(page = 1, originalCharacters)
        localDataSource.saveCharacters(page = 1, updatedCharacters)

        // WHEN
        val result = localDataSource.getAllCharacters(1)

        // THEN
        assertEquals(updatedCharacters, result)
        assertEquals(2, result?.size)
    }

    @Test
    fun `GIVEN page overwritten WHEN getCharacterById THEN returns character from latest page version`() {
        // GIVEN
        val originalCharacters = listOf(
            CharacterResponse(1, "Rick v1", "Alive", "Human", "", "Male", "url1")
        )
        val updatedCharacters = listOf(
            CharacterResponse(1, "Rick v2", "Dead", "Robot", "", "Female", "url2")
        )

        localDataSource.saveCharacters(page = 1, originalCharacters)
        localDataSource.saveCharacters(page = 1, updatedCharacters)

        // WHEN
        val result = localDataSource.getCharacterById(1)

        // THEN
        assertEquals(updatedCharacters[0], result)
    }

    @Test
    fun `GIVEN empty list saved WHEN getAllCharacters THEN returns empty list`() {
        // GIVEN
        localDataSource.saveCharacters(page = 1, emptyList())

        // WHEN
        val result = localDataSource.getAllCharacters(1)

        // THEN
        assertNotNull(result)
        assertTrue(result!!.isEmpty())
    }

    @Test
    fun `GIVEN character overwritten individually WHEN getCharacterById THEN returns latest version`() {
        // GIVEN
        val originalCharacter = CharacterResponse(1, "Rick v1", "Alive", "Human", "", "Male", "url1")
        val updatedCharacter = CharacterResponse(1, "Rick v2", "Dead", "Robot", "", "Female", "url2")

        localDataSource.saveCharacter(originalCharacter)
        localDataSource.saveCharacter(updatedCharacter)

        // WHEN
        val result = localDataSource.getCharacterById(1)

        // THEN
        assertEquals(updatedCharacter, result)
    }
}
