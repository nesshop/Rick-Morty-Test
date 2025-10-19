package com.ernesto.rickandmortycompose.feature.characters.data.remote

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.InfoResponse
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersPagingSourceTest {

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
    fun `GIVEN local cache available and no search query WHEN load is called THEN return data from cache`() =
        runTest {
            // GIVEN
            val page = 1
            val cached = listOf(
                CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList()),
                CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url2", emptyList())
            )
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)
            coEvery { localDataSource.getAllCharacters(page) } returns cached

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page
            assertEquals(2, result.data.size)
            assertEquals("Rick", result.data.first().name)
            assertEquals("Morty", result.data.last().name)
            assertEquals(null, result.prevKey)
            assertEquals(page + 1, result.nextKey)
            coVerify(exactly = 0) { remoteDataSource.getAllCharacters(any(), any()) }
        }

    @Test
    fun `GIVEN no cache and remote success WHEN load is called THEN return data from remote and save`() =
        runTest {
            // GIVEN
            val page = 1
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)
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
                remoteDataSource.getAllCharacters(
                    page,
                    null
                )
            } returns CharactersDataResponse(
                info = infoWithNext,
                results = remoteCharactersList
            )
            coEvery { localDataSource.saveCharacters(page, remoteCharactersList) } just Runs

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page
            assertEquals(1, result.data.size)
            assertEquals("Morty", result.data.first().name)
            assertEquals(null, result.prevKey)
            assertEquals(page + 1, result.nextKey)
            coVerify { localDataSource.saveCharacters(page, remoteCharactersList) }
        }

    @Test
    fun `GIVEN page greater than 1 WHEN load is called THEN prevKey is page - 1`() = runTest {
        // GIVEN
        val page = 2
        val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)
        coEvery { localDataSource.getAllCharacters(page) } returns null

        val remoteCharactersList = listOf(
            CharacterResponse(3, "Summer", "Alive", "Human", "", "Female", "url3", emptyList())
        )
        val infoResponseWithNext = InfoResponse(
            count = 826,
            pages = 42,
            next = "https://rickandmortyapi.com/api/character?page=3",
            prev = "https://rickandmortyapi.com/api/character?page=1"
        )
        coEvery { remoteDataSource.getAllCharacters(page, null) } returns CharactersDataResponse(
            info = infoResponseWithNext,
            results = remoteCharactersList
        )
        coEvery { localDataSource.saveCharacters(page, remoteCharactersList) } just Runs

        // WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

        // THEN
        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertEquals(1, result.data.size)
        assertEquals("Summer", result.data.first().name)
        assertEquals(page - 1, result.prevKey)
        assertEquals(page + 1, result.nextKey)
    }

    @Test
    fun `GIVEN no next page in response WHEN load is called THEN nextKey is null`() = runTest {
        // GIVEN
        val page = 42 // Última página
        val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)
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
        coEvery { remoteDataSource.getAllCharacters(page, null) } returns CharactersDataResponse(
            info = infoResponseNoNext,
            results = remoteCharacters
        )
        coEvery { localDataSource.saveCharacters(page, remoteCharacters) } just Runs

        // WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

        // THEN
        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertEquals(1, result.data.size)
        assertEquals("Beth", result.data.first().name)
        assertEquals(page - 1, result.prevKey)
        assertEquals(null, result.nextKey)
    }

    @Test
    fun `GIVEN empty cache list WHEN load is called THEN nextKey is null`() = runTest {
        // GIVEN
        val page = 1
        val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)
        coEvery { localDataSource.getAllCharacters(page) } returns emptyList()

        // WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

        // THEN
        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertEquals(0, result.data.size)
        assertEquals(null, result.prevKey)
        assertEquals(null, result.nextKey)
    }

    @Test
    fun `GIVEN remote throws exception WHEN load is called THEN return LoadResult Error`() =
        runTest {
            // GIVEN
            val page = 1
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)
            coEvery { localDataSource.getAllCharacters(page) } returns null
            coEvery {
                remoteDataSource.getAllCharacters(
                    page,
                    null
                )
            } throws RuntimeException("Network error")

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Error)
            result as PagingSource.LoadResult.Error
            assertEquals("Network error", result.throwable.message)
        }

    @Test
    fun `GIVEN search query WHEN load is called THEN skips local cache and calls remote with query`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "Rick"
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource, query)

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
                remoteDataSource.getAllCharacters(
                    page,
                    query
                )
            } returns CharactersDataResponse(
                info = infoResponse,
                results = remoteCharacters
            )
            coEvery { localDataSource.saveCharacter(any()) } just Runs

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page
            assertEquals(2, result.data.size)
            assertEquals("Rick Sanchez", result.data.first().name)
            assertEquals(null, result.prevKey)
            assertEquals(null, result.nextKey)
            coVerify(exactly = 0) { localDataSource.getAllCharacters(any()) }
            coVerify(exactly = 0) { localDataSource.saveCharacters(any(), any()) }
            coVerify(exactly = 2) { localDataSource.saveCharacter(any()) }
        }

    @Test
    fun `GIVEN search query with results WHEN load is called THEN saves each character individually`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "Morty"
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource, query)

            val character1 =
                CharacterResponse(3, "Morty Smith", "Alive", "Human", "", "Male", "url3", emptyList())
            val character2 =
                CharacterResponse(4, "Evil Morty", "Alive", "Human", "", "Male", "url4", emptyList())
            val remoteCharacters = listOf(character1, character2)

            val infoResponse = InfoResponse(
                count = 2,
                pages = 1,
                next = null,
                prev = null
            )
            coEvery {
                remoteDataSource.getAllCharacters(
                    page,
                    query
                )
            } returns CharactersDataResponse(
                info = infoResponse,
                results = remoteCharacters
            )
            coEvery { localDataSource.saveCharacter(any()) } just Runs

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Page)
            coVerify { localDataSource.saveCharacter(character1) }
            coVerify { localDataSource.saveCharacter(character2) }
            coVerify(exactly = 0) { localDataSource.saveCharacters(any(), any()) }
        }

    @Test
    fun `GIVEN blank search query WHEN load is called THEN behaves like normal getAllCharacters`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "   "
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource, query)

            val cached = listOf(
                CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList())
            )
            coEvery { localDataSource.getAllCharacters(page) } returns cached

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page
            assertEquals(1, result.data.size)
            assertEquals("Rick", result.data.first().name)
            coVerify(exactly = 1) { localDataSource.getAllCharacters(page) }
            coVerify(exactly = 0) { remoteDataSource.getAllCharacters(any(), any()) }
        }

    @Test
    fun `GIVEN search query returns 404 WHEN load is called THEN returns empty page with null nextKey`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "NonExistentCharacter"
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource, query)

            val httpException = mockk<HttpException>()
            every { httpException.code() } returns 404
            coEvery { remoteDataSource.getAllCharacters(page, query) } throws httpException

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page
            assertEquals(0, result.data.size)
            assertEquals(null, result.prevKey)
            assertEquals(null, result.nextKey)
        }

    @Test
    fun `GIVEN search query returns 404 on page 2 WHEN load is called THEN prevKey is page - 1`() =
        runTest {
            // GIVEN
            val page = 2
            val query = "Rick"
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource, query)

            val httpException = mockk<HttpException>()
            every { httpException.code() } returns 404
            coEvery { remoteDataSource.getAllCharacters(page, query) } throws httpException

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page
            assertEquals(0, result.data.size)
            assertEquals(page - 1, result.prevKey)
            assertEquals(null, result.nextKey)
        }

    @Test
    fun `GIVEN search query returns 500 error WHEN load is called THEN returns LoadResult Error`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "Rick"
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource, query)

            val httpException = mockk<HttpException>()
            every { httpException.code() } returns 500
            coEvery { remoteDataSource.getAllCharacters(page, query) } throws httpException

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Error)
            result as PagingSource.LoadResult.Error
            assertTrue(result.throwable is HttpException)
            assertEquals(500, (result.throwable as HttpException).code())
        }

    @Test
    fun `GIVEN search query with multiple pages WHEN load is called THEN nextKey is calculated correctly`() =
        runTest {
            // GIVEN
            val page = 1
            val query = "Rick"
            val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource, query)

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
                remoteDataSource.getAllCharacters(
                    page,
                    query
                )
            } returns CharactersDataResponse(
                info = infoResponse,
                results = remoteCharacters
            )
            coEvery { localDataSource.saveCharacter(any()) } just Runs

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Page)
            result as PagingSource.LoadResult.Page
            assertEquals(null, result.prevKey)
            assertEquals(page + 1, result.nextKey)
        }

    @Test
    fun `GIVEN valid anchor position WHEN getRefreshKey is called THEN returns correct key`() {
        // GIVEN
        val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)
        val pages = listOf(
            PagingSource.LoadResult.Page(
                data = listOf(CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList())),
                prevKey = null,
                nextKey = 2
            )
        )
        val pagingState = PagingState(
            pages = pages,
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // WHEN
        val refreshKey = pagingSource.getRefreshKey(pagingState)

        // THEN
        assertNotNull(refreshKey)
    }

    @Test
    fun `GIVEN null anchor position WHEN getRefreshKey is called THEN returns null`() {
        // GIVEN
        val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)
        val pagingState = PagingState<Int, CharacterModel>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0
        )

        // WHEN
        val refreshKey = pagingSource.getRefreshKey(pagingState)

        // THEN
        assertNull(refreshKey)
    }
}