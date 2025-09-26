package com.ernesto.rickandmortycompose.feature.characters.data.remote

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharactersRemoteDataSourceImpl @Inject constructor(private val apiService: CharacterApiService) :
    CharactersRemoteDataSource {
    override suspend fun getAllCharacters(page: Int): CharactersDataResponse {
        return apiService.getAllCharacters(page)
    }

    override suspend fun getCharacterById(id: Int): CharacterResponse {
        return apiService.getCharacterById(id)
    }
}