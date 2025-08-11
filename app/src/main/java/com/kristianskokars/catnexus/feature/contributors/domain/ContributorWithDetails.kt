package com.kristianskokars.catnexus.feature.contributors.domain

data class Contributor(
    val githubAccountId: Int,
    val role: String,
    val name: String,
    val avatarLink: String,
    val githubUrl: String,
)
