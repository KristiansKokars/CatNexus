package com.kristianskokars.catnexus.core.data.repository

import androidx.room.withTransaction
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kristianskokars.catnexus.lib.Err
import com.kristianskokars.catnexus.lib.Ok
import com.kristianskokars.catnexus.core.data.data_source.local.CatDatabase
import com.kristianskokars.catnexus.core.data.data_source.remote.CatAPI
import com.kristianskokars.catnexus.core.data.worker.DownloadImageWorker
import com.kristianskokars.catnexus.core.domain.model.Cat
import com.kristianskokars.catnexus.core.domain.model.ServerError
import com.kristianskokars.catnexus.core.domain.repository.CatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstCatRepository(
    private val local: CatDatabase,
    private val remote: CatAPI,
    private val workManager: WorkManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CatRepository {
    private val scope = CoroutineScope(ioDispatcher)
    private val catDao = local.catDao()
    override val cats: Flow<List<Cat>> = catDao.getCats().flowOn(ioDispatcher)

    override suspend fun refreshCats(clearPrevious: Boolean) =
        try {
            withContext(ioDispatcher) {
                val requestCats = { async { remote.getCats() } }
                // API has a limit of 10 cats, so we instead make a few concurrent requests
                val newCats = (0..7)
                    .map { requestCats() }
                    .awaitAll()
                    .flatten()
                    .map { it.toCat() }
                local.withTransaction {
                    catDao.addCats(newCats)
                    if (clearPrevious) {
                        catDao.clearCatsNotIn(newCats.map { it.id })
                    }
                }
                Ok()
            }
        } catch (exception: Exception) {
            Err(ServerError)
        }

    override fun getCat(id: String): Flow<Cat> = catDao.getCat(id)

    override fun saveCatImage(cat: Cat) {
        scope.launch {
            val fileName = "cat${cat.id}"
            val downloadRequest = OneTimeWorkRequestBuilder<DownloadImageWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                )
                .setInputData(
                    workDataOf(
                        DownloadImageWorker.DOWNLOAD_IMAGE_URL to cat.url,
                        DownloadImageWorker.OUTPUT_FILE_NAME to fileName,
                    ),
                )
                .build()

            workManager.enqueueUniqueWork(
                fileName,
                ExistingWorkPolicy.KEEP,
                downloadRequest,
            )
        }
    }
}
