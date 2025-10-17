package com.ernesto.rickandmortycompose.feature.characters.ui.detail

import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val character: CharacterModel) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}