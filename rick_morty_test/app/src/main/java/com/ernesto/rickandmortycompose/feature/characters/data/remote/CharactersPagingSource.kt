package com.ernesto.rickandmortycompose.feature.characters.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel

class CharactersPagingSource(
    private val remoteDataSource: CharactersRemoteDataSource,
    private val localDataSource: CharactersLocalDataSource
) : PagingSource<Int, CharacterModel>() {

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        return try {
            val page = params.key ?: 1
            val prev = if (page > 1) page - 1 else null

            val cachedCharacters = localDataSource.getAllCharacters(page)
            if (cachedCharacters != null) {
                return LoadResult.Page(
                    data = cachedCharacters.map { cached -> cached.toDomain() },
                    prevKey = prev,
                    nextKey = if (cachedCharacters.isNotEmpty()) page + 1 else null
                )
            }
            val response = remoteDataSource.getAllCharacters(page)
            val characters = response.results

            localDataSource.saveCharacters(page, characters)
            val next = if (response.info.next != null) page + 1 else null

            LoadResult.Page(
                data = characters.map { characterResponse -> characterResponse.toDomain() },
                prevKey = prev,
                nextKey = next
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}