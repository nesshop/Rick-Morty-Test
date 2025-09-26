package com.ernesto.rickandmortycompose.feature.characters.domain.usecase

import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import javax.inject.Inject

class GetCharacterByIdUseCase @Inject constructor(private val repository: CharacterRepository) {

    suspend operator fun invoke(id: Int) = repository.getCharacterById(id)
}