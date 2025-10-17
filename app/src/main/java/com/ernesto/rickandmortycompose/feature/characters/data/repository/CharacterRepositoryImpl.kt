package com.ernesto.rickandmortycompose.feature.characters.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersPagingSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val remoteDataSource: CharactersRemoteDataSource,
    private val localDataSource: CharactersLocalDataSource
) : CharacterRepository {

    companion object {
        const val MAX_ITEMS = 20
        const val PREFETCH_DISTANCE = 5
    }

    override fun getAllCharacters(): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = MAX_ITEMS,
                prefetchDistance = PREFETCH_DISTANCE,
                initialLoadSize = MAX_ITEMS,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CharactersPagingSource(
                    remoteDataSource = remoteDataSource,
                    localDataSource = localDataSource
                )
            }).flow
    }

    override suspend fun getCharacterById(id: Int): CharacterModel {
        return try {
            localDataSource.getCharacterById(id)?.toDomain()?.let { cachedCharacter ->
                return cachedCharacter
            }

            val character = remoteDataSource.getCharacterById(id)
            localDataSource.saveCharacter(character)
            character.toDomain()
        } catch (e: Exception) {
            localDataSource.getCharacterById(id)?.toDomain() ?: throw e
        }
    }

    override fun searchCharacters(query: String): Flow<PagingData<CharacterModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = MAX_ITEMS,
                prefetchDistance = PREFETCH_DISTANCE,
                initialLoadSize = MAX_ITEMS,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CharactersPagingSource(
                    remoteDataSource = remoteDataSource,
                    localDataSource = localDataSource,
                    searchQuery = query
                )
            }
        ).flow
    }
}