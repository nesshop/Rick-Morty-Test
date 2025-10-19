package com.ernesto.rickandmortycompose.feature.episodes.data.local

import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.EpisodeResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodesLocalDataSourceImpl @Inject constructor() : EpisodesLocalDataSource {
    override suspend fun getEpisodesForCharacter(episodes: List<String>): List<EpisodeResponse> {
        TODO("Not yet implemented")
    }
}