package com.ernesto.rickandmortycompose.feature.episodes.data.remote

import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.EpisodeResponse

interface EpisodesRemoteDataSource {
    suspend fun getEpisodesForCharacter(episodes: String): List<EpisodeResponse>
    suspend fun getSingleEpisode(id: String): EpisodeResponse
}