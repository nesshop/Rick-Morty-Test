package com.ernesto.rickandmortycompose.feature.characters.data.remote

import androidx.paging.PagingSource
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.CharacterPaginator
import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.PageResult
import com.ernesto.rickandmortycompose.feature.characters.ui.characterlist.paging.CharacterPagingSource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterPagingSourceTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var paginator: CharacterPaginator

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        paginator = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GIVEN successful page load WHEN load is called THEN returns Page with correct data`() = runTest {
        // GIVEN
        val characters = listOf(
            CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList()),
            CharacterModel(2, "Morty", "Alive", "Human", "", "Male", "url2", emptyList())
        )
        val pageResult = PageResult(data = characters, currentPage = 1, hasNextPage = true)
        coEvery { paginator.loadPage(1, 20) } returns pageResult

        val pagingSource = CharacterPagingSource(paginator)

        // WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(1, 20, false))

        // THEN
        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertEquals(2, result.data.size)
        assertEquals("Rick", result.data.first().name)
        assertEquals(null, result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `GIVEN no next page WHEN load is called THEN nextKey is null`() = runTest {
        // GIVEN
        val characters = listOf(CharacterModel(1, "Rick", "Alive", "Human", "", "Male", "url1", emptyList()))
        val pageResult = PageResult(data = characters, currentPage = 42, hasNextPage = false)
        coEvery { paginator.loadPage(42, 20) } returns pageResult

        val pagingSource = CharacterPagingSource(paginator)

        // WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(42, 20, false))

        // THEN
        assertTrue(result is PagingSource.LoadResult.Page)
        result as PagingSource.LoadResult.Page
        assertEquals(41, result.prevKey)
        assertEquals(null, result.nextKey)
    }

    @Test
    fun `GIVEN paginator throws exception WHEN load is called THEN returns Error`() = runTest {
        // GIVEN
        val exception = RuntimeException("Network error")
        coEvery { paginator.loadPage(1, 20) } throws exception

        val pagingSource = CharacterPagingSource(paginator)

        // WHEN
        val result = pagingSource.load(PagingSource.LoadParams.Refresh(1, 20, false))

        // THEN
        assertTrue(result is PagingSource.LoadResult.Error)
        result as PagingSource.LoadResult.Error
        assertEquals("Network error", result.throwable.message)
    }
}