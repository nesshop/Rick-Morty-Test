package com.ernesto.rickandmortycompose.core.di

import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSourceImpl
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSourceImpl
import com.ernesto.rickandmortycompose.feature.characters.data.repository.CharacterRepositoryImpl
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
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


}