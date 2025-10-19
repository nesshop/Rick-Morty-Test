package com.ernesto.rickandmortycompose.feature.episodes.data.remote

import com.ernesto.rickandmortycompose.feature.episodes.data.remote.dto.response.EpisodeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EpisodeApiService {

    @GET("api/episode/{episodes}")
    suspend fun getEpisodes(@Path("episodes") episodes: String): List<EpisodeResponse>
}