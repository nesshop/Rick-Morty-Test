package com.ernesto.rickandmortycompose.feature.episodes.data.local

import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel

interface EpisodesLocalDataSource {
    suspend fun getEpisodesForCharacter(episodes: List<String>): List<EpisodeModel>
    suspend fun saveEpisodes(episodes: List<EpisodeModel>)
}
