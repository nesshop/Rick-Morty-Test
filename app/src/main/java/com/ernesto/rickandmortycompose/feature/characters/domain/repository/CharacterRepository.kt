package com.ernesto.rickandmortycompose.feature.characters.domain.repository

import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.CharacterPaginator

interface CharacterRepository {
    fun getAllCharacters(): CharacterPaginator
    suspend fun getCharacterById(id: Int): CharacterModel
    fun searchCharacters(query: String): CharacterPaginator

}