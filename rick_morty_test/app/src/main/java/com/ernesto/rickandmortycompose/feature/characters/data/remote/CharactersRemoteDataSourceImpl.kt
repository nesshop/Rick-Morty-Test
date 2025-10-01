package com.ernesto.rickandmortycompose.feature.characters.data.remote

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharactersRemoteDataSourceImpl @Inject constructor(private val apiService: CharacterApiService) :
    CharactersRemoteDataSource {
    override suspend fun getAllCharacters(page: Int, searchQuery: String?): CharactersDataResponse {
        return apiService.getAllCharacters(page, searchQuery)
    }

    override suspend fun getCharacterById(id: Int): CharacterResponse {
        return apiService.getCharacterById(id)
    }
}