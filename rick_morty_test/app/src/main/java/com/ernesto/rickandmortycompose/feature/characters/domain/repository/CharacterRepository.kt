package com.ernesto.rickandmortycompose.feature.characters.domain.repository

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getAllCharacters(): Flow<PagingData<CharacterModel>>
    suspend fun getCharacterById(id: Int): CharacterModel
    fun searchCharacters(query: String): Flow<PagingData<CharacterModel>>

}