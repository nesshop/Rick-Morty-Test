package com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response

import com.ernesto.rickandmortycompose.feature.episodes.domain.model.EpisodeModel
import com.google.gson.annotations.SerializedName

data class EpisodeResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("episode")
    val episode: String
)

fun EpisodeResponse.toDomain() = EpisodeModel(
    id = id,
    name = name,
    episode = episode
)
