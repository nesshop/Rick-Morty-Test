package com.ernesto.rickandmortycompose.feature.characters.data.local

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharactersLocalDataSourceImpl @Inject constructor() : CharactersLocalDataSource {

    private val cache: MutableMap<Int, List<CharacterResponse>> = mutableMapOf()
    private val cacheById: MutableMap<Int, CharacterResponse> = mutableMapOf()
    override fun getAllCharacters(page: Int): List<CharacterResponse>? {
        return cache[page]
    }

    override fun saveCharacters(
        page: Int,
        characters: List<CharacterResponse>
    ) {
        cache[page] = characters
        characters.forEach { character ->
            cacheById[character.id] = character
        }
    }

    override fun getCharacterById(id: Int): CharacterResponse {
        return cacheById[id] ?: cache.values.flatten().first { it.id == id }
    }

    override fun saveCharacter(character: CharacterResponse) {
        cacheById[character.id] = character
    }
}