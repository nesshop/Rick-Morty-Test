package com.ernesto.rickandmortycompose.feature.episodes.data.local

import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel

import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EpisodesLocalDataSourceImplTest {

    private lateinit var localDataSource: EpisodesLocalDataSourceImpl

    @Before
    fun setup() {
        localDataSource = EpisodesLocalDataSourceImpl()
    }

    @Test
    fun `GIVEN empty cache WHEN getEpisodesForCharacter called THEN returns empty list`() = runTest {
        // Given
        val episodeIds = listOf("1", "2")

        // When
        val result = localDataSource.getEpisodesForCharacter(episodeIds)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `GIVEN episodes saved WHEN getEpisodesForCharacter called with existing ids THEN returns cached episodes`() = runTest {
        // Given
        val episodes = listOf(
            EpisodeModel("1", "Pilot", "S01E01"),
            EpisodeModel("2", "Lawnmower Dog", "S01E02")
        )
        localDataSource.saveEpisodes(episodes)

        // When
        val result = localDataSource.getEpisodesForCharacter(listOf("1", "2"))

        // Then
        assertEquals(2, result.size)
        assertEquals(episodes, result)
    }

    @Test
    fun `GIVEN episodes saved WHEN getEpisodesForCharacter called with partial ids THEN returns only existing episodes`() = runTest {
        // Given
        val episodes = listOf(
            EpisodeModel("1", "Pilot", "S01E01"),
            EpisodeModel("2", "Lawnmower Dog", "S01E02")
        )
        localDataSource.saveEpisodes(episodes)

        // When
        val result = localDataSource.getEpisodesForCharacter(listOf("1", "3"))

        // Then
        assertEquals(1, result.size)
        assertEquals("S01E01", result.first().episode)
    }

    @Test
    fun `GIVEN episodes saved WHEN getEpisodesForCharacter called with non-existing ids THEN returns empty list`() = runTest {
        // Given
        val episodes = listOf(
            EpisodeModel("1", "Pilot", "S01E01")
        )
        localDataSource.saveEpisodes(episodes)

        // When
        val result = localDataSource.getEpisodesForCharacter(listOf("3", "4"))

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `GIVEN empty list WHEN saveEpisodes called THEN cache remains empty`() = runTest {
        // Given
        val episodes = emptyList<EpisodeModel>()

        // When
        localDataSource.saveEpisodes(episodes)

        // Then
        val result = localDataSource.getEpisodesForCharacter(listOf("1"))
        assertTrue(result.isEmpty())
    }

    @Test
    fun `GIVEN duplicate episode WHEN saveEpisodes called THEN episode is overwritten`() = runTest {
        // Given
        val episode1 = EpisodeModel("1", "Pilot", "S01E01")
        val episode2 = EpisodeModel("1", "Pilot Updated", "S01E01")

        localDataSource.saveEpisodes(listOf(episode1))

        // When
        localDataSource.saveEpisodes(listOf(episode2))

        // Then
        val result = localDataSource.getEpisodesForCharacter(listOf("1"))
        assertEquals(1, result.size)
        assertEquals("Pilot Updated", result.first().name)
    }

    @Test
    fun `GIVEN multiple saves WHEN getEpisodesForCharacter called THEN returns all cached episodes`() = runTest {
        // Given
        val batch1 = listOf(
            EpisodeModel("1", "Pilot", "S01E01")
        )
        val batch2 = listOf(
            EpisodeModel("2", "Lawnmower Dog", "S01E02"),
            EpisodeModel("3", "Anatomy Park", "S01E03")
        )

        localDataSource.saveEpisodes(batch1)
        localDataSource.saveEpisodes(batch2)

        // When
        val result = localDataSource.getEpisodesForCharacter(listOf("1", "2", "3"))

        // Then
        assertEquals(3, result.size)
    }

    @Test
    fun `GIVEN empty episode ids list WHEN getEpisodesForCharacter called THEN returns empty list`() = runTest {
        // Given
        val episodes = listOf(
            EpisodeModel("1", "Pilot", "S01E01")
        )
        localDataSource.saveEpisodes(episodes)

        // When
        val result = localDataSource.getEpisodesForCharacter(emptyList())

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `GIVEN episodes with same episode code but different ids WHEN saveEpisodes called THEN last one is kept`() = runTest {
        // Given
        val episode1 = EpisodeModel("1", "Pilot Version 1", "S01E01")
        val episode2 = EpisodeModel("2", "Pilot Version 2", "S01E01")

        // When
        localDataSource.saveEpisodes(listOf(episode1, episode2))

        // Then
        val result = localDataSource.getEpisodesForCharacter(listOf("2"))
        assertEquals(1, result.size)
        assertEquals("Pilot Version 2", result.first().name)
        assertEquals("2", result.first().id)
    }
}