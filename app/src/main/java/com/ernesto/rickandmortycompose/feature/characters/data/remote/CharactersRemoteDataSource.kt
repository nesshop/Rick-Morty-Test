package com.ernesto.rickandmortycompose.feature.characters.data.remote

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse

interface CharactersRemoteDataSource {
    suspend fun getAllCharacters(page: Int, searchQuery: String? = null): CharactersDataResponse
    suspend fun getCharacterById(id: Int): CharacterResponse

}