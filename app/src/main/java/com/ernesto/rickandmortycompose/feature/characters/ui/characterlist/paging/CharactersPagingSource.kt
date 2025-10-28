package com.ernesto.rickandmortycompose.feature.characters.ui.characterlist.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.CharacterPaginator

class CharacterPagingSource(
    private val paginator: CharacterPaginator
) : PagingSource<Int, CharacterModel>() {

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val page = params.key ?: 1

        return try {
            val result = paginator.loadPage(page, params.loadSize)

            LoadResult.Page(
                data = result.data,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (result.hasNextPage) page + 1 else null
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}