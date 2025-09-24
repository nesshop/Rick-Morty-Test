package com.ernesto.rickandmortycompose.feature.characters.data.local

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharactersLocalDataSourceImpl @Inject constructor() : CharactersLocalDataSource {

    private val cache: MutableMap<Int, List<CharacterResponse>> = mutableMapOf()
    override fun getAllCharacters(page: Int): List<CharacterResponse>? {
        return cache[page]
    }

    override fun saveCharacters(
        page: Int,
        characters: List<CharacterResponse>
    ) {
        cache[page] = characters
    }
}