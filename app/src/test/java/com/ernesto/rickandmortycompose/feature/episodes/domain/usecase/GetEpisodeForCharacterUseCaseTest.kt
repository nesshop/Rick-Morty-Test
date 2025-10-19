package com.ernesto.rickandmortycompose.feature.episodes.domain.usecase

import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel
import com.ernesto.rickandmortycompose.feature.episodes.domain.repository.EpisodeRepository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetEpisodeForCharacterUseCaseTest {

    private lateinit var repository: EpisodeRepository
    private lateinit var useCase: GetEpisodeForCharacterUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetEpisodeForCharacterUseCase(repository)
    }

    @Test
    fun `GIVEN repository returns episodes WHEN invoke called THEN returns episodes`() = runTest {
        // Given
        val episodeIds = listOf("1", "2", "3")
        val expectedEpisodes = listOf(
            EpisodeModel("1", "Pilot", "S01E01"),
            EpisodeModel("2", "Lawnmower Dog", "S01E02"),
            EpisodeModel("3", "Anatomy Park", "S01E03")
        )

        coEvery { repository.getEpisodesForCharacter(episodeIds) } returns expectedEpisodes

        // When
        val result = useCase(episodeIds)

        // Then
        assertEquals(expectedEpisodes, result)
        coVerify(exactly = 1) { repository.getEpisodesForCharacter(episodeIds) }
    }

    @Test
    fun `GIVEN empty episode list WHEN invoke called THEN returns empty list`() = runTest {
        // Given
        val episodeIds = emptyList<String>()

        coEvery { repository.getEpisodesForCharacter(episodeIds) } returns emptyList()

        // When
        val result = useCase(episodeIds)

        // Then
        assertTrue(result.isEmpty())
        coVerify(exactly = 1) { repository.getEpisodesForCharacter(episodeIds) }
    }

    @Test
    fun `GIVEN single episode WHEN invoke called THEN returns single episode`() = runTest {
        // Given
        val episodeIds = listOf("1")
        val expectedEpisode = listOf(
            EpisodeModel("1", "Pilot", "S01E01")
        )

        coEvery { repository.getEpisodesForCharacter(episodeIds) } returns expectedEpisode

        // When
        val result = useCase(episodeIds)

        // Then
        assertEquals(expectedEpisode, result)
        coVerify(exactly = 1) { repository.getEpisodesForCharacter(episodeIds) }
    }

    @Test
    fun `GIVEN repository throws exception WHEN invoke called THEN exception propagates`() = runTest {
        // Given
        val episodeIds = listOf("1", "2")
        val exception = RuntimeException("Repository error")

        coEvery { repository.getEpisodesForCharacter(episodeIds) } throws exception

        // When & Then
        try {
            useCase(episodeIds)
            assert(false) { "Expected exception to be thrown" }
        } catch (e: RuntimeException) {
            assertEquals("Repository error", e.message)
        }

        coVerify(exactly = 1) { repository.getEpisodesForCharacter(episodeIds) }
    }
}