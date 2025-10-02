package com.ernesto.rickandmortycompose.feature.characters.data.repository

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterRepositoryImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var remoteDataSource: CharactersRemoteDataSource
    private lateinit var localDataSource: CharactersLocalDataSource
    private lateinit var repository: CharacterRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mockk()
        localDataSource = mockk()
        repository = CharacterRepositoryImpl(remoteDataSource, localDataSource)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `WHEN getAllCharacters called THEN returns PagingData Flow`() = runTest {
        // WHEN
        val result = repository.getAllCharacters()

        // THEN
        assertNotNull(result)
        assertTrue(result is Flow<PagingData<CharacterModel>>)
    }

    @Test
    fun `WHEN getAllCharacters called multiple times THEN creates different Flow instances`() =
        runTest {
            // WHEN
            val result1 = repository.getAllCharacters()
            val result2 = repository.getAllCharacters()

            // THEN
            assertNotEquals(result1, result2)
        }

    @Test
    fun `WHEN searchCharacters called THEN returns PagingData Flow`() = runTest {
        // WHEN
        val result = repository.searchCharacters("Rick")

        // THEN
        assertNotNull(result)
        assertTrue(result is Flow<PagingData<CharacterModel>>)
    }

    @Test
    fun `WHEN searchCharacters called with empty query THEN returns PagingData Flow`() = runTest {
        // WHEN
        val result = repository.searchCharacters("")

        // THEN
        assertNotNull(result)
        assertTrue(result is Flow<PagingData<CharacterModel>>)
    }

    @Test
    fun `WHEN searchCharacters called multiple times with same query THEN creates different Flow instances`() =
        runTest {
            // WHEN
            val result1 = repository.searchCharacters("Rick")
            val result2 = repository.searchCharacters("Rick")

            // THEN
            assertNotEquals(result1, result2)
        }

    @Test
    fun `WHEN searchCharacters called with different queries THEN creates different Flow instances`() =
        runTest {
            // WHEN
            val result1 = repository.searchCharacters("Rick")
            val result2 = repository.searchCharacters("Morty")

            // THEN
            assertNotEquals(result1, result2)
        }

    @Test
    fun `GIVEN character exists in local cache WHEN getCharacterById THEN returns cached character without calling remote`() =
        runTest {
            // GIVEN
            val cachedCharacter = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
            coEvery { localDataSource.getCharacterById(1) } returns cachedCharacter

            // WHEN
            val result = repository.getCharacterById(1)

            // THEN
            assertEquals(cachedCharacter.toDomain(), result)
            coVerify(exactly = 1) { localDataSource.getCharacterById(1) }
            coVerify(exactly = 0) { remoteDataSource.getCharacterById(any()) }
            coVerify(exactly = 0) { localDataSource.saveCharacter(any()) }
        }

    @Test
    fun `GIVEN character not in local but exists in remote WHEN getCharacterById THEN fetches saves and returns character`() =
        runTest {
            // GIVEN
            val remoteCharacter = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
            coEvery { localDataSource.getCharacterById(1) } returns null andThen remoteCharacter
            coEvery { remoteDataSource.getCharacterById(1) } returns remoteCharacter
            coEvery { localDataSource.saveCharacter(remoteCharacter) } just Runs

            // WHEN
            val result = repository.getCharacterById(1)

            // THEN
            assertEquals(remoteCharacter.toDomain(), result)
            coVerify(exactly = 1) { remoteDataSource.getCharacterById(1) }
            coVerify(exactly = 1) { localDataSource.saveCharacter(remoteCharacter) }
        }

    @Test
    fun `GIVEN character not in local and remote fails WHEN getCharacterById THEN throws exception`() =
        runTest {
            // GIVEN
            val exception = RuntimeException("Network error")
            coEvery { localDataSource.getCharacterById(1) } returns null
            coEvery { remoteDataSource.getCharacterById(1) } throws exception

            // WHEN & THEN
            val thrownException = assertFailsWith<RuntimeException> {
                repository.getCharacterById(1)
            }
            assertEquals("Network error", thrownException.message)
            coVerify(exactly = 2) { localDataSource.getCharacterById(1) }
            coVerify(exactly = 1) { remoteDataSource.getCharacterById(1) }
        }

    @Test
    fun `GIVEN character in cache but remote fails WHEN getCharacterById from remote THEN returns cached version as fallback`() =
        runTest {
            // GIVEN
            val cachedCharacter = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
            coEvery { localDataSource.getCharacterById(1) } returns null andThen cachedCharacter
            coEvery { remoteDataSource.getCharacterById(1) } throws RuntimeException("Network error")

            // WHEN
            val result = repository.getCharacterById(1)

            // THEN
            assertEquals(cachedCharacter.toDomain(), result)
            coVerify(exactly = 2) { localDataSource.getCharacterById(1) }
            coVerify(exactly = 1) { remoteDataSource.getCharacterById(1) }
        }

    @Test
    fun `GIVEN remote throws IOException WHEN getCharacterById THEN attempts fallback to cache`() =
        runTest {
            // GIVEN
            val cachedCharacter = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
            coEvery { localDataSource.getCharacterById(1) } returns null andThen cachedCharacter
            coEvery { remoteDataSource.getCharacterById(1) } throws IOException("Connection timeout")

            // WHEN
            val result = repository.getCharacterById(1)

            // THEN
            assertEquals(cachedCharacter.toDomain(), result)
            coVerify(exactly = 2) { localDataSource.getCharacterById(1) }
        }

    @Test
    fun `GIVEN save to cache fails WHEN getCharacterById THEN still returns character from remote`() =
        runTest {
            // GIVEN
            val remoteCharacter = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
            coEvery { localDataSource.getCharacterById(1) } returns null
            coEvery { remoteDataSource.getCharacterById(1) } returns remoteCharacter
            coEvery { localDataSource.saveCharacter(remoteCharacter) } throws RuntimeException("Cache write failed")

            // WHEN & THEN
            assertFailsWith<RuntimeException> {
                repository.getCharacterById(1)
            }
        }

    @Test
    fun `WHEN repository is created THEN has correct page configuration`() {
        // THEN
        assertEquals(20, CharacterRepositoryImpl.MAX_ITEMS)
        assertEquals(5, CharacterRepositoryImpl.PREFETCH_DISTANCE)
    }

    @Test
    fun `GIVEN multiple characters WHEN getCharacterById called sequentially THEN caches work independently`() =
        runTest {
            // GIVEN
            val rick = CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
            val morty = CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2")

            coEvery { localDataSource.getCharacterById(1) } returns rick
            coEvery { localDataSource.getCharacterById(2) } returns null
            coEvery { remoteDataSource.getCharacterById(2) } returns morty
            coEvery { localDataSource.saveCharacter(morty) } just Runs

            // WHEN
            val result1 = repository.getCharacterById(1)
            val result2 = repository.getCharacterById(2)

            // THEN
            assertEquals(rick.toDomain(), result1)
            assertEquals(morty.toDomain(), result2)
            coVerify(exactly = 1) { localDataSource.getCharacterById(1) }
            coVerify(exactly = 1) { localDataSource.getCharacterById(2) }
            coVerify(exactly = 0) { remoteDataSource.getCharacterById(1) }
            coVerify(exactly = 1) { remoteDataSource.getCharacterById(2) }
        }

    @Test
    fun `GIVEN character ID is 0 WHEN getCharacterById THEN processes normally`() = runTest {
        // GIVEN
        val character = CharacterResponse(0, "Unknown", "Unknown", "Unknown", "", "Unknown", "")
        coEvery { localDataSource.getCharacterById(0) } returns character

        // WHEN
        val result = repository.getCharacterById(0)

        // THEN
        assertEquals(character.toDomain(), result)
    }

    @Test
    fun `GIVEN negative character ID WHEN getCharacterById THEN processes normally`() = runTest {
        // GIVEN
        coEvery { localDataSource.getCharacterById(-1) } returns null
        coEvery { remoteDataSource.getCharacterById(-1) } throws IllegalArgumentException("Invalid ID")

        // WHEN & THEN
        assertFailsWith<IllegalArgumentException> {
            repository.getCharacterById(-1)
        }
    }
}