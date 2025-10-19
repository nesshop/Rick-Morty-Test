package com.ernesto.rickandmortycompose.core.di

import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSourceImpl
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSourceImpl
import com.ernesto.rickandmortycompose.feature.characters.data.repository.CharacterRepositoryImpl
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import com.ernesto.rickandmortycompose.feature.episodes.data.local.EpisodesLocalDataSource
import com.ernesto.rickandmortycompose.feature.episodes.data.local.EpisodesLocalDataSourceImpl
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.EpisodeRemoteDataSourceImpl
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.EpisodesRemoteDataSource
import com.ernesto.rickandmortycompose.feature.episodes.data.repository.EpisodeRepositoryImpl
import com.ernesto.rickandmortycompose.feature.episodes.domain.repository.EpisodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCharacterRepository(
        characterRepositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    abstract fun bindCharactersLocalDataSource(
        charactersLocalDataSourceImpl: CharactersLocalDataSourceImpl
    ): CharactersLocalDataSource

    @Binds
    abstract fun bindCharactersRemoteDataSource(
        charactersRemoteDataSourceImpl: CharactersRemoteDataSourceImpl
    ): CharactersRemoteDataSource

    @Binds
    abstract fun bindEpisodeRepository(
        episodeRepositoryImpl: EpisodeRepositoryImpl
    ): EpisodeRepository

    @Binds
    abstract fun bindEpisodesRemoteDataSource(
        episodeRemoteDataSourceImpl: EpisodeRemoteDataSourceImpl
    ): EpisodesRemoteDataSource

    @Binds
    abstract fun bindEpisodesLocalDataSource(
        episodesLocalDataSourceImpl: EpisodesLocalDataSourceImpl
    ): EpisodesLocalDataSource

}