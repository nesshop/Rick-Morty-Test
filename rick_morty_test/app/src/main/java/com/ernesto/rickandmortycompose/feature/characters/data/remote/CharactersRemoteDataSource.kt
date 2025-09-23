package com.ernesto.rickandmortycompose.feature.characters.data.remote

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharactersDataResponse

interface CharactersRemoteDataSource {
    suspend fun getAllCharacters(page: Int): CharactersDataResponse
}