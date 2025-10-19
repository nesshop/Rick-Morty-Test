package com.ernesto.rickandmortycompose.feature.episodes.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class EpisodeModel(
    val id: String,
    val name: String,
    val episode: String
)
