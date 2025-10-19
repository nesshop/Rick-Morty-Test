package com.ernesto.rickandmortycompose.feature.episodes.data.remote

import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.EpisodeResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodeRemoteDataSourceImpl @Inject constructor(private val apiService: EpisodeApiService) : EpisodesRemoteDataSource {
    override suspend fun getEpisodesForCharacter(episodes: String): List<EpisodeResponse> {
        return apiService.getEpisodes(episodes)
    }

    override suspend fun getSingleEpisode(id: String): EpisodeResponse {
        return apiService.getSingleEpisode(id)
    }
}