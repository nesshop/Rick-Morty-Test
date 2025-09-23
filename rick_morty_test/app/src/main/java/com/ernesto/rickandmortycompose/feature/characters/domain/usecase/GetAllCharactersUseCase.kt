package com.ernesto.rickandmortycompose.feature.characters.domain.usecase

import androidx.paging.PagingData
import com.ernesto.rickandmortycompose.feature.characters.data.local.CharactersLocalDataSourceImpl
import com.ernesto.rickandmortycompose.feature.characters.data.remote.CharactersRemoteDataSourceImpl
import com.ernesto.rickandmortycompose.feature.characters.data.repository.CharacterRepositoryImpl
import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.ernesto.rickandmortycompose.feature.characters.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class GetAllCharactersUseCase() {
    private val remoteDataSourceImpl = CharactersRemoteDataSourceImpl()
    private val localDataSourceImpl = CharactersLocalDataSourceImpl()
    private val repository: CharacterRepository =
        CharacterRepositoryImpl(remoteDataSourceImpl, localDataSourceImpl)

    operator fun invoke(): Flow<PagingData<CharacterModel>> {
        return repository.getAllCharacters()
    }
}