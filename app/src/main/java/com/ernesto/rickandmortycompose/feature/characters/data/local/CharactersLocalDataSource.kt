package com.ernesto.rickandmortycompose.feature.characters.data.local

import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.CharacterResponse

interface CharactersLocalDataSource {
    fun getAllCharacters(page: Int): List<CharacterResponse>?
    fun saveCharacters(page: Int, characters: List<CharacterResponse>)
    fun getCharacterById(id: Int): CharacterResponse?
    fun saveCharacter(character: CharacterResponse)
}