package com.ernesto.rickandmortycompose.feature.episodes.data.remote

import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel

interface EpisodesRemoteDataSource {
    suspend fun getEpisodesForCharacter(episodes: List<String>): List<EpisodeModel>
}