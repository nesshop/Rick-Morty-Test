package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class CharactersUiState(
    val characters: Flow<PagingData<CharacterModel>> = emptyFlow()
)
