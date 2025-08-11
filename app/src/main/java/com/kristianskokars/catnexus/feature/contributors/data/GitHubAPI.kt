package com.kristianskokars.catnexus.feature.contributors.data

import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubAPI {
    @GET("https://api.github.com/user/{accountId}")
    suspend fun getUser(@Path("accountId") accountId: Int): ContributorAPIModel
}
