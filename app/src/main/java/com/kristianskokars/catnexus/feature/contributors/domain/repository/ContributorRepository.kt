package com.kristianskokars.catnexus.feature.contributors.domain.repository

import com.kristianskokars.catnexus.feature.contributors.domain.Contributor
import kotlinx.coroutines.flow.Flow

interface ContributorRepository {
    fun contributors(): Flow<List<Contributor>>
}
