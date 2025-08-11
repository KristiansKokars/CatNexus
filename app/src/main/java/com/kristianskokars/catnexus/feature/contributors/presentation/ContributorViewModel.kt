package com.kristianskokars.catnexus.feature.contributors.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kristianskokars.catnexus.feature.contributors.domain.repository.ContributorRepository
import com.kristianskokars.catnexus.lib.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class ContributorViewModel @Inject constructor(
    repository: ContributorRepository
): ViewModel() {
    private val contributors = repository.contributors()

    val state = contributors
        .map { contributors -> ContributorsState.Loaded(contributors = contributors) }
        .asStateFlow(viewModelScope, ContributorsState.Loading)
}
