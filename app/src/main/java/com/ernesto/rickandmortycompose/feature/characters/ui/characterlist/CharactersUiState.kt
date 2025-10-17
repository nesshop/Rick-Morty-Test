package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import kotlinx.coroutines.flow.Flow

sealed class CharactersUiState{
    object Loading : CharactersUiState()
    data class Success(val characters: Flow<PagingData<CharacterModel>>) : CharactersUiState()
    data class Error(val message: String) : CharactersUiState()
}


