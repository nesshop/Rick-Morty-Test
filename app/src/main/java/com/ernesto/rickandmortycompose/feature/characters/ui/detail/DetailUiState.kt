package com.ernesto.rickandmortycompose.feature.characters.ui.detail

import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(
        val character: CharacterModel,
        val episodes: List<EpisodeModel> = emptyList()
    ) : DetailUiState()

    data class Error(val message: String) : DetailUiState()
}