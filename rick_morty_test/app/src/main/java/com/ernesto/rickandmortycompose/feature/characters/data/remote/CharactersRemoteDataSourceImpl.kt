package com.ernesto.rickandmortycompose.feature.characters.data.remote

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CharactersRemoteDataSourceImpl: CharactersRemoteDataSource {

    // TODO: Add to di module
    private val apiService = Retrofit.Builder()
        .baseUrl("https://rickandmortyapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CharacterApiService::class.java)
    override suspend fun getAllCharacters(page: Int): CharactersDataResponse {
        return apiService.getAllCharacters(page)
    }
}