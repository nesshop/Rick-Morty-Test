package com.ernesto.rickandmortycompose.feature.characters.data.local

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse

class CharactersLocalDataSourceImpl : CharactersLocalDataSource {

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