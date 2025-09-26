package com.ernesto.rickandmortycompose.feature.characters.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetCharacterByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val getCharacterByIdUseCase: GetCharacterByIdUseCase) :
    ViewModel() {

    private val _character = MutableStateFlow<CharacterModel?>(null)
    val character: StateFlow<CharacterModel?> = _character

    fun loadCharacter(id: Int) {
        viewModelScope.launch {
            val result = getCharacterByIdUseCase(id)
            _character.value = result
        }
    }
}