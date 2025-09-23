package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetAllCharactersUseCase
import kotlinx.coroutines.flow.Flow

class CharactersListViewModel() : ViewModel() {
    private val getAllCharactersUseCase = GetAllCharactersUseCase()

    val characters: Flow<PagingData<CharacterModel>> = getAllCharactersUseCase()
        .cachedIn(viewModelScope)
}