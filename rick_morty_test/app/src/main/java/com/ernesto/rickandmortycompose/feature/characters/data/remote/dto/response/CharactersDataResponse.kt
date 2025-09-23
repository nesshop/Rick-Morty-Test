package com.ernesto.rickandmortycompose.feature.characters.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class CharactersDataResponse(
    @SerializedName("info")
    val info: InfoResponse,
    @SerializedName("results")
    val results: List<CharacterResponse>
)
