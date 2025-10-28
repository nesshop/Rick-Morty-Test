package com.ernesto.rickandmortycompose.feature.characters.data.paginator

import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.CharacterPaginator
import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.PageResult
import retrofit2.HttpException

class CharacterPaginatorImpl(
    private val remoteDataSource: CharactersRemoteDataSource,
    private val localDataSource: CharactersLocalDataSource,
    private val searchQuery: String? = null
) : CharacterPaginator {
    override suspend fun loadPage(
        page: Int,
        pageSize: Int
    ): PageResult<CharacterModel> {
        return try {

            if (searchQuery.isNullOrBlank()) {
                localDataSource.getAllCharacters(page)?.let { cachedCharacters ->
                    if (cachedCharacters.isNotEmpty()) {
                        return PageResult(
                            data = cachedCharacters.map { it.toDomain() },
                            currentPage = page,
                            hasNextPage = true
                        )
                    }
                }
            }

            val response = try {
                remoteDataSource.getAllCharacters(page, searchQuery)
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    return PageResult(
                        data = emptyList(),
                        currentPage = page,
                        hasNextPage = false
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

            PageResult(
                data = characters.map { it.toDomain() },
                currentPage = page,
                hasNextPage = response.info.next != null
            )
        } catch (exception: Exception) {
            throw exception
        }
    }
}