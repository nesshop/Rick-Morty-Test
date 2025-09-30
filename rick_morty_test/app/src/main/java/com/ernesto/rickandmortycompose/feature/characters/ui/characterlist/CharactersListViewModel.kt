package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetAllCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharactersListViewModel @Inject constructor(
    getAllCharactersUseCase: GetAllCharactersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharactersUiState>(CharactersUiState.Loading)
    val uiState: StateFlow<CharactersUiState> = _uiState

    init {
        loadCharacters(getAllCharactersUseCase)
    }

    private fun loadCharacters(getAllCharactersUseCase: GetAllCharactersUseCase) {
        viewModelScope.launch {
            try {
                val characters = getAllCharactersUseCase().cachedIn(viewModelScope)
                _uiState.value = CharactersUiState.Success(characters)
                } catch (exception: Exception) {
                _uiState.value = CharactersUiState.Error(exception.message ?: "Error loading characters list")
            }
        }
    }
}