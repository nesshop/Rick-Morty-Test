package com.ernesto.rickandmortycompose.feature.characters.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class CharacterModel(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val image: String,
    val episodes: List<String>
)