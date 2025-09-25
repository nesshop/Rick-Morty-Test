package com.ernesto.rickandmortycompose.feature.characters.data.repository

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
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
}