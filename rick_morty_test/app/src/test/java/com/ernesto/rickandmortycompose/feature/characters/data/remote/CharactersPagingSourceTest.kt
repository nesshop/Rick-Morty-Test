package com.ernesto.rickandmortycompose.feature.characters.data.remote

import androidx.paging.PagingSource
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.InfoResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharactersPagingSourceTest {

    private val remoteDataSource: CharactersRemoteDataSource = mockk()
    private val localDataSource: CharactersLocalDataSource = mockk()
    private val pagingSource = CharactersPagingSource(remoteDataSource, localDataSource)

    @Test
    fun `GIVEN local cache available WHEN load is called THEN return data from cache`() = runTest {
        //GIVEN
        val page = 1
        val cached = listOf(
            CharacterResponse(1, "Rick", "Alive", "Human", "", "Male", "url1")
        )
        coEvery { localDataSource.getAllCharacters(page) } returns cached

        //WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

        //THEN
        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertEquals(1, result.data.size)
        assertEquals("Rick", result.data.first().name)
        assertEquals(null, result.prevKey)
        assertEquals(page + 1, result.nextKey)
        coVerify(exactly = 0) { remoteDataSource.getAllCharacters(any()) }
    }

    @Test
    fun `GIVEN no cache and remote success WHEN load is called THEN return data from remote and save`() =
        runTest {
            // GIVEN
            val page = 1
            coEvery { localDataSource.getAllCharacters(page) } returns null
            val remoteCharactersList = listOf(
                CharacterResponse(2, "Morty", "Alive", "Human", "", "Male", "url")
            )
            val infoWithNext = InfoResponse(
                count = 826,
                pages = 42,
                next = "https://rickandmortyapi.com/api/character?page=2",
                prev = null
            )
            coEvery { remoteDataSource.getAllCharacters(page) } returns CharactersDataResponse(
                info = infoWithNext,
                results = remoteCharactersList
            )
            coEvery { localDataSource.saveCharacters(page, remoteCharactersList) } returns Unit

            //WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            //THEN
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
        //GIVEN
        val page = 2
        coEvery { localDataSource.getAllCharacters(page) } returns null

        val remoteCharactersList = listOf(
            CharacterResponse(3, "Summer", "Alive", "Human", "", "Female", "url3")
        )
        val infoResponseWithNext = InfoResponse(
            count = 826,
            pages = 42,
            next = "https://rickandmortyapi.com/api/character?page=3",
            prev = "https://rickandmortyapi.com/api/character?page=1"
        )
        coEvery { remoteDataSource.getAllCharacters(page) } returns CharactersDataResponse(
            info = infoResponseWithNext,
            results = remoteCharactersList
        )
        coEvery { localDataSource.saveCharacters(page, remoteCharactersList) } returns Unit

        //WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

        //THEN
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
        val page = 1
        coEvery { localDataSource.getAllCharacters(page) } returns null

        val remoteCharacters = listOf(
            CharacterResponse(4, "Beth", "Alive", "Human", "", "Female", "url4")
        )
        val infoResponseNoNext = InfoResponse(
            count = 826,
            pages = 42,
            next = null,
            prev = null
        )
        coEvery { remoteDataSource.getAllCharacters(page) } returns CharactersDataResponse(
            info = infoResponseNoNext,
            results = remoteCharacters
        )
        coEvery { localDataSource.saveCharacters(page, remoteCharacters) } returns Unit

        // WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

        // THEN
        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertEquals(1, result.data.size)
        assertEquals("Beth", result.data.first().name)
        assertEquals(null, result.prevKey)
        assertEquals(null, result.nextKey)
    }

    @Test
    fun `GIVEN remote throws exception WHEN load is called THEN return LoadResult_Error`() =
        runTest {
            // GIVEN
            val page = 1
            coEvery { localDataSource.getAllCharacters(page) } returns null
            coEvery { remoteDataSource.getAllCharacters(page) } throws RuntimeException("Network error")

            // WHEN
            val result = pagingSource.load(PagingSource.LoadParams.Refresh(page, 20, false))

            // THEN
            assertTrue(result is PagingSource.LoadResult.Error)
        }
}