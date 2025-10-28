package com.ernesto.rickandmortycompose.feature.characters.data.paginator

import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.InfoResponse
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import retrofit2.HttpException
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterPaginatorImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var remoteDataSource: CharactersRemoteDataSource
    private lateinit var localDataSource: CharactersLocalDataSource

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        remoteDataSource = mockk()
        localDataSource = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN local cache available and no search query WHEN loadPage is called THEN return data from cache`() =
        runTest {
            // GIVEN
            val page = 1
            val cached = listOf(
                CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList()),
                CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2", emptyList())
            )
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = null)
            coEvery { localDataSource.getAllCharacters(page) } returns cached

            // WHEN
            val result = paginator.loadPage(page, 20)

            // THEN
            assertEquals(2, result.data.size)
            assertEquals("Rick", result.data.first().name)
            assertEquals("Morty", result.data.last().name)
            assertEquals(page, result.currentPage)
            assertTrue(result.hasNextPage)
            coVerify(exactly = 0) { remoteDataSource.getAllCharacters(any(), any()) }
        }

    @Test
    fun `GIVEN no cache and remote success WHEN loadPage is called THEN return data from remote and save`() =
        runTest {
            // GIVEN
            val page = 1
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = null)
            coEvery { localDataSource.getAllCharacters(page) } returns null

            val remoteCharactersList = listOf(
                CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url", emptyList())
            )
            val infoWithNext = InfoResponse(
                count = 826,
                pages = 42,
                next = "https://rickandmortyapi.com/api/character?page=2",
                prev = null
            )
            coEvery {
                remoteDataSource.getAllCharacters(page, null)
            } returns CharactersDataResponse(
                info = infoWithNext,
                results = remoteCharactersList
            )
            coEvery { localDataSource.saveCharacters(page, remoteCharactersList) } just Runs

            // WHEN
            val result = paginator.loadPage(page, 20)

            // THEN
            assertEquals(1, result.data.size)
            assertEquals("Morty", result.data.first().name)
            assertEquals(page, result.currentPage)
            assertTrue(result.hasNextPage)
            coVerify { localDataSource.saveCharacters(page, remoteCharactersList) }
        }

    @Test
    fun `GIVEN no next page in response WHEN loadPage is called THEN hasNextPage is false`() =
        runTest {
            // GIVEN
            val page = 42
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = null)
            coEvery { localDataSource.getAllCharacters(page) } returns null

            val remoteCharacters = listOf(
                CharacterResponse(4, "Beth", "Alive", "Human", "", "Female", "url4", emptyList())
            )
            val infoResponseNoNext = InfoResponse(
                count = 826,
                pages = 42,
                next = null,
                prev = "https://rickandmortyapi.com/api/character?page=41"
            )
            coEvery {
                remoteDataSource.getAllCharacters(page, null)
            } returns CharactersDataResponse(
                info = infoResponseNoNext,
                results = remoteCharacters
            )
            coEvery { localDataSource.saveCharacters(page, remoteCharacters) } just Runs

            // WHEN
            val result = paginator.loadPage(page, 20)

            // THEN
            assertEquals(1, result.data.size)
            assertEquals("Beth", result.data.first().name)
            assertEquals(page, result.currentPage)
            assertFalse(result.hasNextPage)
        }

    @Test
    fun `GIVEN remote throws exception WHEN loadPage is called THEN throw exception`() =
        runTest {
            // GIVEN
            val page = 1
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = null)
            coEvery { localDataSource.getAllCharacters(page) } returns null
            coEvery {
                remoteDataSource.getAllCharacters(page, null)
            } throws RuntimeException("Network error")

            // WHEN & THEN
            val exception = assertFailsWith<RuntimeException> {
                paginator.loadPage(page, 20)
            }
            assertEquals("Network error", exception.message)
        }

    @Test
    fun `GIVEN search query WHEN loadPage is called THEN skips local cache and calls remote with query`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "Rick"
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = query)

            val remoteCharacters = listOf(
                CharacterResponse(1, "Rick Sanchez", "Alive", "Human", "", "Male", "url1", emptyList()),
                CharacterResponse(2, "Rick Prime", "Unknown", "Human", "", "Male", "url2", emptyList())
            )
            val infoResponse = InfoResponse(
                count = 2,
                pages = 1,
                next = null,
                prev = null
            )
            coEvery {
                remoteDataSource.getAllCharacters(page, query)
            } returns CharactersDataResponse(
                info = infoResponse,
                results = remoteCharacters
            )
            coEvery { localDataSource.saveCharacter(any()) } just Runs

            // WHEN
            val result = paginator.loadPage(page, 20)

            // THEN
            assertEquals(2, result.data.size)
            assertEquals("Rick Sanchez", result.data.first().name)
            assertEquals(page, result.currentPage)
            assertFalse(result.hasNextPage)
            coVerify(exactly = 0) { localDataSource.getAllCharacters(any()) }
            coVerify(exactly = 0) { localDataSource.saveCharacters(any(), any()) }
            coVerify(exactly = 2) { localDataSource.saveCharacter(any()) }
        }

    @Test
    fun `GIVEN search query with results WHEN loadPage is called THEN saves each character individually`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "Morty"
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = query)

            val character1 = CharacterResponse(3, "Morty Smith", "Alive", "Human", "", "Male", "url3", emptyList())
            val character2 = CharacterResponse(4, "Evil Morty", "Alive", "Human", "", "Male", "url4", emptyList())
            val remoteCharacters = listOf(character1, character2)

            val infoResponse = InfoResponse(count = 2, pages = 1, next = null, prev = null)
            coEvery {
                remoteDataSource.getAllCharacters(page, query)
            } returns CharactersDataResponse(
                info = infoResponse,
                results = remoteCharacters
            )
            coEvery { localDataSource.saveCharacter(any()) } just Runs

            // WHEN
            val result = paginator.loadPage(page, 20)

            // THEN
            assertEquals(2, result.data.size)
            coVerify { localDataSource.saveCharacter(character1) }
            coVerify { localDataSource.saveCharacter(character2) }
            coVerify(exactly = 0) { localDataSource.saveCharacters(any(), any()) }
        }

    @Test
    fun `GIVEN blank search query WHEN loadPage is called THEN behaves like normal getAllCharacters`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "   "
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = query)

            val cached = listOf(
                CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList())
            )
            coEvery { localDataSource.getAllCharacters(page) } returns cached

            // WHEN
            val result = paginator.loadPage(page, 20)

            // THEN
            assertEquals(1, result.data.size)
            assertEquals("Rick", result.data.first().name)
            coVerify(exactly = 1) { localDataSource.getAllCharacters(page) }
            coVerify(exactly = 0) { remoteDataSource.getAllCharacters(any(), any()) }
        }

    @Test
    fun `GIVEN search query returns 404 WHEN loadPage is called THEN returns empty PageResult with hasNextPage false`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "NonExistentCharacter"
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = query)

            val httpException = mockk<HttpException>()
            every { httpException.code() } returns 404
            coEvery { remoteDataSource.getAllCharacters(page, query) } throws httpException

            // WHEN
            val result = paginator.loadPage(page, 20)

            // THEN
            assertEquals(0, result.data.size)
            assertEquals(page, result.currentPage)
            assertFalse(result.hasNextPage)
        }

    @Test
    fun `GIVEN search query returns 500 error WHEN loadPage is called THEN throws exception`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "Rick"
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = query)

            val httpException = mockk<HttpException>()
            every { httpException.code() } returns 500
            coEvery { remoteDataSource.getAllCharacters(page, query) } throws httpException

            // WHEN & THEN
            val exception = assertFailsWith<HttpException> {
                paginator.loadPage(page, 20)
            }
            assertEquals(500, exception.code())
        }

    @Test
    fun `GIVEN search query with multiple pages WHEN loadPage is called THEN hasNextPage is true`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "Rick"
            val paginator = CharacterPaginatorImpl(remoteDataSource, localDataSource, searchQuery = query)

            val remoteCharacters = listOf(
                CharacterResponse(1, "Rick Sanchez", "Alive", "Human", "", "Male", "url1", emptyList())
            )
            val infoResponse = InfoResponse(
                count = 50,
                pages = 3,
                next = "https://rickandmortyapi.com/api/character?page=2&name=Rick",
                prev = null
            )
            coEvery {
                remoteDataSource.getAllCharacters(page, query)
            } returns CharactersDataResponse(
                info = infoResponse,
                results = remoteCharacters
            )
            coEvery { localDataSource.saveCharacter(any()) } just Runs

            // WHEN
            val result = paginator.loadPage(page, 20)

            // THEN
            assertEquals(1, result.data.size)
            assertEquals(page, result.currentPage)
            assertTrue(result.hasNextPage)
        }
}