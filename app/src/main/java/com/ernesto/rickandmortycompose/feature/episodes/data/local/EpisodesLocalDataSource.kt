package com.ernesto.rickandmortycompose.feature.episodes.data.local

import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.EpisodeResponse

interface EpisodesLocalDataSource {
    suspend fun getEpisodesForCharacter(episodes: List<String>): List<EpisodeResponse>
}