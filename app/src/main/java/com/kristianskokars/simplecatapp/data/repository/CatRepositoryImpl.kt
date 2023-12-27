package com.kristianskokars.simplecatapp.data.repository

import androidx.room.withTransaction
import androidx.work.*
import com.kristianskokars.simplecatapp.data.data_source.local.CatDatabase
import com.kristianskokars.simplecatapp.data.data_source.remote.CatAPI
import com.kristianskokars.simplecatapp.data.worker.DownloadImageWorker
import com.kristianskokars.simplecatapp.domain.model.Cat
import com.kristianskokars.simplecatapp.domain.repository.CatRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class CatRepositoryImpl(
    private val local: CatDatabase,
    private val remote: CatAPI,
    private val workManager: WorkManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CatRepository {
    private val scope = CoroutineScope(ioDispatcher)
    private val catDao = local.catDao()
    override val cats: Flow<List<Cat>> = catDao.getCats().flowOn(ioDispatcher)

    override suspend fun refreshCats() = withContext(ioDispatcher) {
        val requestCats = { async { remote.getCats() } }
        // API has a limit of 10 cats, so we instead make a few concurrent requests
        val newCats = (0..7)
            .map { requestCats() }
            .awaitAll()
            .flatten()
            .map { it.toCat() }
        local.withTransaction {
            catDao.addCats(newCats)
            catDao.clearCatsNotIn(newCats.map { it.id })
        }
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
