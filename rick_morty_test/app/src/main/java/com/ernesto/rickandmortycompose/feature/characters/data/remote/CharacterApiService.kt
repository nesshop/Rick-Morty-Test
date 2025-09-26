package com.ernesto.rickandmortycompose.feature.characters.data.remote

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CharacterApiService {
    @GET("api/character")
    suspend fun getAllCharacters(@Query("page") page: Int): CharactersDataResponse

    @GET("api/character/{id}")
    suspend fun getCharacterById(@Query("id") id: Int): CharacterResponse

}