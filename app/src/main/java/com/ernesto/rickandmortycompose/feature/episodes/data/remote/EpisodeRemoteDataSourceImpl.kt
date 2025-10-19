package com.ernesto.rickandmortycompose.feature.episodes.data.remote

import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRemoteDataSourceImpl @Inject constructor(private val apiService: EpisodeApiService) : EpisodesRemoteDataSource {
    override suspend fun getEpisodesForCharacter(episodes: List<String>): List<EpisodeModel> {
        return apiService.getEpisodes(episodes.joinToString(separator = ",")).map { episodeResponse ->
            episodeResponse.toDomain()
        }
    }
}