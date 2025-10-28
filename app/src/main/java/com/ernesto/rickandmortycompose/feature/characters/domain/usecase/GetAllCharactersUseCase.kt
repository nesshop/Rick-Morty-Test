package com.ernesto.rickandmortycompose.feature.characters.domain.usecase

import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.CharacterPaginator
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import javax.inject.Inject

class GetAllCharactersUseCase @Inject constructor(private val repository: CharacterRepository) {

    operator fun invoke(): CharacterPaginator = repository.getAllCharacters()
}