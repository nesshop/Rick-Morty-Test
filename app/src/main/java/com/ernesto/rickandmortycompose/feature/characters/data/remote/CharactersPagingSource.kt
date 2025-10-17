package com.ernesto.rickandmortycompose.feature.characters.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import retrofit2.HttpException

class CharactersPagingSource(
    private val remoteDataSource: CharactersRemoteDataSource,
    private val localDataSource: CharactersLocalDataSource,
    private val searchQuery: String? = null
) : PagingSource<Int, CharacterModel>() {

    override fun getRefreshKey(state: PagingState<Int, CharacterModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterModel> {
        val page = params.key ?: 1
        val prev = if (page > 1) page - 1 else null

        return try {
            if (searchQuery.isNullOrBlank()) {
                localDataSource.getAllCharacters(page)?.let { cachedCharacters ->
                    return LoadResult.Page(
                        data = cachedCharacters.map { cached -> cached.toDomain() },
                        prevKey = prev,
                        nextKey = if (cachedCharacters.isNotEmpty()) page + 1 else null
                    )
                }
            }

            val response = try {
                remoteDataSource.getAllCharacters(page, searchQuery)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    return LoadResult.Page(
                        data = emptyList(),
                        prevKey = prev,
                        nextKey = null
                    )
                } else {
                    throw e
                }
            }

            val characters = response.results

            if (searchQuery.isNullOrBlank()) {
                localDataSource.saveCharacters(page, characters)
            } else {
                characters.forEach { localDataSource.saveCharacter(it) }
            }

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
