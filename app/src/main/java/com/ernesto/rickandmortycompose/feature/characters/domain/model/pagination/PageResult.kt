package com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination

data class PageResult<T>(
    val data: List<T>,
    val currentPage: Int,
    val hasNextPage: Boolean
)
