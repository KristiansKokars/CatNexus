package com.kristianskokars.catnexus.feature.contributors.data

data class ContributorBaseInfo(
    val githubAccountId: Int,
    val originalName: String,
    val originalGitHubLink: String,
    val role: String,
    val order: Int,
)

val CONTRIBUTORS get() = listOf(
    ContributorBaseInfo(
        githubAccountId = 56245893,
        originalName = "Kristiāns Kokars",
        originalGitHubLink = "https://github.com/KristiansKokars",
        role = "Author",
        order = 1
    ),
    ContributorBaseInfo(
        githubAccountId = 42638122,
        originalName = "Kurts Folkmanis",
        originalGitHubLink = "https://github.com/KurtsFolkmanis",
        role = "Contributor",
        order = 2
    )
)
