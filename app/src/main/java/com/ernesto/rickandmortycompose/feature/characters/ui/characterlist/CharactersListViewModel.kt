package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.GetAllCharactersUseCase
import com.ernesto.rickandmortycompose.feature.characters.domain.usecase.SearchCharactersUseCase
import com.ernesto.rickandmortycompose.feature.characters.ui.characterlist.paging.CharacterPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class CharactersListViewModel @Inject constructor(
    private val getAllCharactersUseCase: GetAllCharactersUseCase,
    private val searchCharactersUseCase: SearchCharactersUseCase
) : ViewModel() {

    companion object {
        const val MAX_ITEMS = 20
        const val PREFETCH_DISTANCE = 5
    }
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    @OptIn(FlowPreview::class)
    val uiState: StateFlow<CharactersUiState> = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .map { query ->
            val paginator = if (query.isBlank()) {
                getAllCharactersUseCase()
            } else {
                searchCharactersUseCase(query)
            }

            val pagingFlow = Pager(
                config = PagingConfig(
                    pageSize = MAX_ITEMS,
                    prefetchDistance = PREFETCH_DISTANCE,
                    initialLoadSize = MAX_ITEMS,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { CharacterPagingSource(paginator) }
            ).flow.cachedIn(viewModelScope)

            CharactersUiState.Success(pagingFlow)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CharactersUiState.Success(emptyFlow())
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }
}