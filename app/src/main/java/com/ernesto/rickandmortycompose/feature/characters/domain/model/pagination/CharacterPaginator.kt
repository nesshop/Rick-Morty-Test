package com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination

import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

interface CharacterPaginator {
    suspend fun loadPage(page: Int, pageSize: Int) : PageResult<CharacterModel>
}