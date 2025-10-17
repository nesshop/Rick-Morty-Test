package com.ernesto.rickandmortycompose.feature.characters.domain.usecase

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchCharactersUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
) {
    operator fun invoke(query: String): Flow<PagingData<CharacterModel>> {
        return characterRepository.searchCharacters(query)
    }
}