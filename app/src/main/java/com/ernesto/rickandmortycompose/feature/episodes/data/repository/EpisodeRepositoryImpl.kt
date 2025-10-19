package com.ernesto.rickandmortycompose.feature.episodes.data.repository

import com.ernesto.rickandmortycompose.feature.episodes.data.local.EpisodesLocalDataSource
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.EpisodesRemoteDataSource
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
        return remoteDataSource.getEpisodesForCharacter(episodes)
    }
}