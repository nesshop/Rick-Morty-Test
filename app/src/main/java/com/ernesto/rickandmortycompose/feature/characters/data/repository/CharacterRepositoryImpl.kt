package com.ernesto.rickandmortycompose.feature.characters.data.repository

import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.paginator.CharacterPaginatorImpl
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSource
import com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response.toDomain
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.model.pagination.CharacterPaginator
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val remoteDataSource: CharactersRemoteDataSource,
    private val localDataSource: CharactersLocalDataSource
) : CharacterRepository {

    override fun getAllCharacters(): CharacterPaginator {
        return CharacterPaginatorImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            searchQuery = null
        )
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

    override fun searchCharacters(query: String): CharacterPaginator {
        return CharacterPaginatorImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource,
            searchQuery = query
        )
    }
}