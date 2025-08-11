package com.kristianskokars.catnexus.feature.contributors.data.repository

import com.kristianskokars.catnexus.core.data.data_source.local.ContributorDao
import com.kristianskokars.catnexus.core.data.model.ContributorEntity
import com.kristianskokars.catnexus.feature.contributors.data.CONTRIBUTORS
import com.kristianskokars.catnexus.feature.contributors.data.GitHubAPI
import com.kristianskokars.catnexus.feature.contributors.domain.Contributor
import com.kristianskokars.catnexus.feature.contributors.domain.repository.ContributorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.IOException
import timber.log.Timber
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class OfflineFirstContributorRepository(
    private val gitHubAPI: GitHubAPI,
    private val contributorDao: ContributorDao,
    private val ioScope: CoroutineScope,
    private val clock: Clock,
) : ContributorRepository {
    override fun contributors(): Flow<List<Contributor>> {
        ioScope.launch { refreshGitHubInfo() }

        return contributorDao.getContributors().map { contributors ->
            contributors.map { Contributor(it.githubAccountId, it.role, it.name, it.avatarLink, it.githubUrl) }
        }
    }

    private suspend fun CoroutineScope.refreshGitHubInfo() {
        val lastSyncedTimeInMillis = contributorDao.getLastSyncedTimeInMillis().firstOrNull()
        val currentTime = clock.now().toEpochMilliseconds()
        val timeDifference = (currentTime - (lastSyncedTimeInMillis ?: 0)).milliseconds
        if (timeDifference.inWholeDays < 1L) return

        try {
            val contributorEntities = CONTRIBUTORS
                .map { contributor ->
                    async { gitHubAPI.getUser(contributor.githubAccountId) }
                }
                .awaitAll()
                .mapNotNull { contributorAPIModel ->
                    // it is not most efficient to search the original list again,
                    // but considering it will only ever a very low n number of people, this is a fine shortcut to take
                    val baseInfo = CONTRIBUTORS.find { it.githubAccountId == contributorAPIModel.id } ?: return@mapNotNull null

                    ContributorEntity(
                        githubAccountId = contributorAPIModel.id,
                        role = baseInfo.role,
                        name = contributorAPIModel.name,
                        avatarLink = contributorAPIModel.avatarUrl,
                        githubUrl = contributorAPIModel.githubUrl,
                        order = baseInfo.order,
                        lastSyncedTimeInMillis = clock.now().toEpochMilliseconds()
                    )
                }
            contributorDao.addContributors(contributorEntities)
        } catch (e: IOException) {
            Timber.e(e, "Failed to get updated contributor info from GitHub")
            addContributorsFromBaseInfoIfNeeded(lastSyncedTimeInMillis)
        }
    }

    private suspend fun addContributorsFromBaseInfoIfNeeded(lastSyncedTimeInMillis: Long?) {
        val currentContributorIds = contributorDao.getContributors().first().map { it.githubAccountId }

        for (contributor in CONTRIBUTORS) {
            if (!currentContributorIds.contains(contributor.githubAccountId)) {
                contributorDao.addContributor(
                    ContributorEntity(
                        githubAccountId = contributor.githubAccountId,
                        role = contributor.role,
                        name = contributor.originalName,
                        avatarLink = "",
                        githubUrl = contributor.originalGitHubLink,
                        order = contributor.order,
                        lastSyncedTimeInMillis = lastSyncedTimeInMillis ?: clock.now().toEpochMilliseconds()
                    )
                )
            }
        }
    }
}
