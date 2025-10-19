package com.ernesto.rickandmortycompose.feature.characters.ui.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ernesto.rickandmortycompose.R
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetCharacterByIdUseCase
import com.ernesto.rickandmortycompose.feature.episodes.domain.usecase.GetEpisodeForCharacterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
    private val getEpisodeForCharacterUseCase: GetEpisodeForCharacterUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadCharacter(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val result = getCharacterByIdUseCase(id)
                _uiState.value = DetailUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(
                    e.message
                        ?: context.getString(R.string.characters_detail_view_model_loading_error_text)
                )
            }
        }
    }

    fun loadEpisodes(episodes: List<String>) {
        viewModelScope.launch {
            try {
                val episodes = getEpisodeForCharacterUseCase(episodes)
                val currentState = _uiState.value
                if (currentState is DetailUiState.Success) {
                    _uiState.value = currentState.copy(episodes = episodes)
                }
            }catch (exception: Exception) {
                _uiState.value = DetailUiState.Error(exception.message ?: context.getString(R.string.characters_detail_view_model_loading_error_text))
            }
        }
    }
}