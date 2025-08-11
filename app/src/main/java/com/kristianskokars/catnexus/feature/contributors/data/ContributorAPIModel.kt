package com.kristianskokars.catnexus.feature.contributors.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContributorAPIModel(
    val id: Int,
    @SerialName("html_url")
    val githubUrl: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    val name: String,
)
