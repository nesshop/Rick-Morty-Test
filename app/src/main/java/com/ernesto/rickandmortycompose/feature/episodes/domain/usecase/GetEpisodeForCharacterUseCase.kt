package com.ernesto.rickandmortycompose.feature.episodes.domain.usecase

import com.ernesto.rickandmortycompose.feature.episodes.domain.repository.EpisodeRepository
import javax.inject.Inject

class GetEpisodeForCharacterUseCase @Inject constructor(private val repository: EpisodeRepository) {
    suspend operator fun invoke(episodes: List<String>) = repository.getEpisodesForCharacter(episodes)
}