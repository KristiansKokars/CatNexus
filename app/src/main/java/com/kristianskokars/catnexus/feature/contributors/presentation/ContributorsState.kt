package com.kristianskokars.catnexus.feature.contributors.presentation

import com.kristianskokars.catnexus.feature.contributors.domain.Contributor

sealed class ContributorsState {
    data object Loading : ContributorsState()
    data class Loaded(val contributors: List<Contributor>) : ContributorsState()
}
