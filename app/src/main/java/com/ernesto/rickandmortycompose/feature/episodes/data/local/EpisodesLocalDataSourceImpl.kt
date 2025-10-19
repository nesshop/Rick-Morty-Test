package com.ernesto.rickandmortycompose.feature.episodes.data.local

import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpisodesLocalDataSourceImpl @Inject constructor() : EpisodesLocalDataSource {

    private val cachedEpisodes = mutableMapOf<String, EpisodeModel>()
    override suspend fun getEpisodesForCharacter(episodes: List<String>): List<EpisodeModel> {
        return episodes.mapNotNull { cachedEpisodes[it] }
    }

    override suspend fun saveEpisodes(episodes: List<EpisodeModel>) {
        episodes.forEach { episode ->
            cachedEpisodes[episode.id] = episode
        }
    }
}