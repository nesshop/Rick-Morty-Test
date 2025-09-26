package com.ernesto.rickandmortycompose.core.navigation

import kotlinx.serialization.Serializable

sealed class Route {

    @Serializable
    data object CharacterList: Route()

    @Serializable
    data class CharacterDetail(val id : Int): Route()
}
