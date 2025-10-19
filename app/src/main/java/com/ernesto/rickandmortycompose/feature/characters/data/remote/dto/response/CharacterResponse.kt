package com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response

import com.ernesto.rickandmortycompose.feature.characters.domain.model.CharacterModel
import com.google.gson.annotations.SerializedName

data class CharacterResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("species")
    val species: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("episode")
    val episode: List<String>
)

fun CharacterResponse.toDomain() = CharacterModel(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    image = image,
    episodes = episode.map { it.extractEpisodeNumber() }
)

fun String.extractEpisodeNumber(): String = substringAfterLast("/")
