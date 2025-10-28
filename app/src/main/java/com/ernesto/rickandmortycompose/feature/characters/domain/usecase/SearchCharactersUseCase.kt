package com.ernesto.rickandmortycompose.feature.characters.domain.usecase

import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.CharacterPaginator
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import javax.inject.Inject

class SearchCharactersUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
) {
    operator fun invoke(query: String): CharacterPaginator {
        return characterRepository.searchCharacters(query)
    }
}