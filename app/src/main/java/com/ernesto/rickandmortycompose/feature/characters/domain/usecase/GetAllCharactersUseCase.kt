package com.ernesto.rickandmortycompose.feature.characters.domain.usecase

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCharactersUseCase @Inject constructor(private val repository: CharacterRepository) {

    operator fun invoke(): Flow<PagingData<CharacterModel>> = repository.getAllCharacters()
}