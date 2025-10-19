package com.ernesto.rickandmortycompose.feature.episodes.data.repository

import com.ernesto.rickandmortycompose.feature.episodes.data.local.EpisodesLocalDataSource
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.EpisodesRemoteDataSource
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel
import com.ernesto.rickandmortycompose.feature.episodes.domain.repository.EpisodeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRepositoryImpl @Inject constructor(
    private val remoteDataSource: EpisodesRemoteDataSource,
    private val localDataSource: EpisodesLocalDataSource
) : EpisodeRepository {

    override suspend fun getEpisodesForCharacter(episodes: List<String>): List<EpisodeModel> {
        if (episodes.isEmpty()) return emptyList()

        val cachedEpisodes = localDataSource.getEpisodesForCharacter(episodes)
        if (cachedEpisodes.size == episodes.size) return cachedEpisodes

        val remoteEpisodes = if (episodes.size >= MIN_EPISODES_FOR_BATCH_REQUEST) {
            remoteDataSource.getEpisodesForCharacter(episodes.joinToString(","))
                .map { episodeResponse ->
                    episodeResponse.toDomain()
                }
        } else {
            listOf(remoteDataSource.getSingleEpisode(episodes.first()).toDomain())
        }

        localDataSource.saveEpisodes(remoteEpisodes)
        return remoteEpisodes
    }

    companion object {
        private const val MIN_EPISODES_FOR_BATCH_REQUEST = 2
    }
}
