package com.ernesto.rickandmortycompose.feature.episodes.data.repository

import com.ernesto.rickandmortycompose.feature.episodes.data.local.EpisodesLocalDataSource
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.EpisodesRemoteDataSource
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.EpisodeResponse
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EpisodeRepositoryImplTest {

    private lateinit var remoteDataSource: EpisodesRemoteDataSource
    private lateinit var localDataSource: EpisodesLocalDataSource
    private lateinit var repository: EpisodeRepositoryImpl

    @Before
    fun setup() {
        remoteDataSource = mockk()
        localDataSource = mockk(relaxed = true)
        repository = EpisodeRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `GIVEN empty episode list WHEN getEpisodesForCharacter called THEN returns empty list without calling datasources`() = runTest {
        // Given
        val episodes = emptyList<String>()

        // When
        val result = repository.getEpisodesForCharacter(episodes)

        // Then
        assertTrue(result.isEmpty())
        coVerify(exactly = 0) { localDataSource.getEpisodesForCharacter(any()) }
        coVerify(exactly = 0) { remoteDataSource.getEpisodesForCharacter(any()) }
        coVerify(exactly = 0) { remoteDataSource.getSingleEpisode(any()) }
    }

    @Test
    fun `GIVEN all episodes in cache WHEN getEpisodesForCharacter called THEN returns cached episodes without remote call`() = runTest {
        // Given
        val episodeIds = listOf("1", "2", "3")
        val cachedEpisodes = listOf(
            EpisodeModel("1", "Pilot", "S01E01"),
            EpisodeModel("2", "Lawnmower Dog", "S01E02"),
            EpisodeModel("3", "Anatomy Park", "S01E03")
        )

        coEvery { localDataSource.getEpisodesForCharacter(episodeIds) } returns cachedEpisodes

        // When
        val result = repository.getEpisodesForCharacter(episodeIds)

        // Then
        assertEquals(cachedEpisodes, result)
        coVerify(exactly = 1) { localDataSource.getEpisodesForCharacter(episodeIds) }
        coVerify(exactly = 0) { remoteDataSource.getEpisodesForCharacter(any()) }
        coVerify(exactly = 0) { remoteDataSource.getSingleEpisode(any()) }
        coVerify(exactly = 0) { localDataSource.saveEpisodes(any()) }
    }

    @Test
    fun `GIVEN no episodes in cache and multiple episodes WHEN getEpisodesForCharacter called THEN calls batch remote and saves to cache`() = runTest {
        // Given
        val episodeIds = listOf("1", "2", "3")
        val remoteResponses = listOf(
            EpisodeResponse("1", "Pilot", "S01E01"),
            EpisodeResponse("2", "Lawnmower Dog", "S01E02"),
            EpisodeResponse("3", "Anatomy Park", "S01E03")
        )
        val expectedModels = remoteResponses.map { it.toDomain() }

        coEvery { localDataSource.getEpisodesForCharacter(episodeIds) } returns emptyList()
        coEvery { remoteDataSource.getEpisodesForCharacter("1,2,3") } returns remoteResponses

        // When
        val result = repository.getEpisodesForCharacter(episodeIds)

        // Then
        assertEquals(expectedModels, result)
        coVerify(exactly = 1) { localDataSource.getEpisodesForCharacter(episodeIds) }
        coVerify(exactly = 1) { remoteDataSource.getEpisodesForCharacter("1,2,3") }
        coVerify(exactly = 0) { remoteDataSource.getSingleEpisode(any()) }
        coVerify(exactly = 1) { localDataSource.saveEpisodes(expectedModels) }
    }

    @Test
    fun `GIVEN no episodes in cache and single episode WHEN getEpisodesForCharacter called THEN calls single remote and saves to cache`() = runTest {
        // Given
        val episodeIds = listOf("1")
        val remoteResponse = EpisodeResponse("1", "Pilot", "S01E01")
        val expectedModel = remoteResponse.toDomain()

        coEvery { localDataSource.getEpisodesForCharacter(episodeIds) } returns emptyList()
        coEvery { remoteDataSource.getSingleEpisode("1") } returns remoteResponse

        // When
        val result = repository.getEpisodesForCharacter(episodeIds)

        // Then
        assertEquals(listOf(expectedModel), result)
        coVerify(exactly = 1) { localDataSource.getEpisodesForCharacter(episodeIds) }
        coVerify(exactly = 0) { remoteDataSource.getEpisodesForCharacter(any()) }
        coVerify(exactly = 1) { remoteDataSource.getSingleEpisode("1") }
        coVerify(exactly = 1) { localDataSource.saveEpisodes(listOf(expectedModel)) }
    }

    @Test
    fun `GIVEN partial episodes in cache WHEN getEpisodesForCharacter called THEN calls remote for all and saves to cache`() = runTest {
        // Given
        val episodeIds = listOf("1", "2", "3")
        val cachedEpisodes = listOf(
            EpisodeModel("1", "Pilot", "S01E01")
        )
        val remoteResponses = listOf(
            EpisodeResponse("1", "Pilot", "S01E01"),
            EpisodeResponse("2", "Lawnmower Dog", "S01E02"),
            EpisodeResponse("3", "Anatomy Park", "S01E03")
        )
        val expectedModels = remoteResponses.map { it.toDomain() }

        coEvery { localDataSource.getEpisodesForCharacter(episodeIds) } returns cachedEpisodes
        coEvery { remoteDataSource.getEpisodesForCharacter("1,2,3") } returns remoteResponses

        // When
        val result = repository.getEpisodesForCharacter(episodeIds)

        // Then
        assertEquals(expectedModels, result)
        coVerify(exactly = 1) { localDataSource.getEpisodesForCharacter(episodeIds) }
        coVerify(exactly = 1) { remoteDataSource.getEpisodesForCharacter("1,2,3") }
        coVerify(exactly = 1) { localDataSource.saveEpisodes(expectedModels) }
    }

    @Test
    fun `GIVEN two episodes WHEN getEpisodesForCharacter called THEN uses batch request`() = runTest {
        // Given
        val episodeIds = listOf("1", "2")
        val remoteResponses = listOf(
            EpisodeResponse("1", "Pilot", "S01E01"),
            EpisodeResponse("2", "Lawnmower Dog", "S01E02")
        )

        coEvery { localDataSource.getEpisodesForCharacter(episodeIds) } returns emptyList()
        coEvery { remoteDataSource.getEpisodesForCharacter("1,2") } returns remoteResponses

        // When
        repository.getEpisodesForCharacter(episodeIds)

        // Then
        coVerify(exactly = 1) { remoteDataSource.getEpisodesForCharacter("1,2") }
        coVerify(exactly = 0) { remoteDataSource.getSingleEpisode(any()) }
    }

    @Test
    fun `GIVEN remote throws exception WHEN getEpisodesForCharacter called THEN exception propagates`() = runTest {
        // Given
        val episodeIds = listOf("1", "2")
        val exception = RuntimeException("Network error")

        coEvery { localDataSource.getEpisodesForCharacter(episodeIds) } returns emptyList()
        coEvery { remoteDataSource.getEpisodesForCharacter("1,2") } throws exception

        // When & Then
        try {
            repository.getEpisodesForCharacter(episodeIds)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals("Network error", e.message)
        }

        // Verify cache was not updated
        coVerify(exactly = 0) { localDataSource.saveEpisodes(any()) }
    }

    @Test
    fun `GIVEN episodes with correct order WHEN getEpisodesForCharacter called THEN maintains order`() = runTest {
        // Given
        val episodeIds = listOf("3", "1", "2")
        val remoteResponses = listOf(
            EpisodeResponse("3", "Anatomy Park", "S01E03"),
            EpisodeResponse("1", "Pilot", "S01E01"),
            EpisodeResponse("2", "Lawnmower Dog", "S01E02")
        )

        coEvery { localDataSource.getEpisodesForCharacter(episodeIds) } returns emptyList()
        coEvery { remoteDataSource.getEpisodesForCharacter("3,1,2") } returns remoteResponses

        // When
        val result = repository.getEpisodesForCharacter(episodeIds)

        // Then
        assertEquals("3", result[0].id)
        assertEquals("1", result[1].id)
        assertEquals("2", result[2].id)
    }
}