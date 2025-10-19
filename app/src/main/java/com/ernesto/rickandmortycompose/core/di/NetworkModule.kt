package com.ernesto.rickandmortycompose.core.di

import com.ernesto.rickandmortycompose.BuildConfig
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharacterApiService
import com.ernesto.rickandmortycompose.feature.episodes.data.remote.EpisodeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideCharacterApiService(retrofit: Retrofit): CharacterApiService {
        return retrofit.create(CharacterApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideEpisodeApiService(retrofit: Retrofit): EpisodeApiService {
        return retrofit.create(EpisodeApiService::class.java)
    }
}