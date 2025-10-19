package com.ernesto.rickandmortycompose.feature.episodes.domain.repository

import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel

interface EpisodeRepository {
    suspend fun getEpisodesForCharacter(episodes: List<String>): List<EpisodeModel>
}
