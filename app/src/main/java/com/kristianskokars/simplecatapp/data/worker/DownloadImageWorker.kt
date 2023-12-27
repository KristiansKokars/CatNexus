package com.kristianskokars.simplecatapp.data.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kristianskokars.simplecatapp.R
import com.kristianskokars.simplecatapp.core.CHANNEL_ID
import com.kristianskokars.simplecatapp.data.data_source.local.FileStorage
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DownloadImageWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted parameters: WorkerParameters,
    private val fileStorage: FileStorage,
) : CoroutineWorker(context, parameters) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        val inputUrl = inputData.getString(DOWNLOAD_IMAGE_URL) ?: return Result.failure()
        val fileName = inputData.getString(OUTPUT_FILE_NAME) ?: return Result.failure()

        setForeground(createForegroundInfo(fileName))
        val fileUri = fileStorage.downloadImage(inputUrl, fileName) ?: return Result.failure()
        showFinishedNotification(inputUrl, fileName, fileUri)

        return Result.success()
    }

    private fun createForegroundInfo(fileName: String): ForegroundInfo {
        val cancel = context.getString(R.string.cancel_download)
        val contentTitle = context.getString(R.string.downloading_picture, fileName)
        val contentText = context.getString(R.string.downloading)

        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_cat)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(1, notification)
    }

    private fun showFinishedNotification(inputUrl: String, fileName: String, fileUri: Uri) {
        val contentTitle = context.getString(R.string.downloaded_picture, fileName)
        val contentText = context.getString(R.string.downloaded_from, inputUrl)

        val openImageInGallery =
            PendingIntent.getActivity(
                context,
                1, // TODO: Deal with IDs
                Intent(Intent.ACTION_VIEW, fileUri),
                PendingIntent.FLAG_IMMUTABLE,
            )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID) // TODO: always remember of Compat libraries... ree
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_cat)
            .setContentIntent(openImageInGallery)
            .build()

        with(notificationManager) {
            notify(2, notification)
        }
    }

    companion object {
        const val DOWNLOAD_IMAGE_URL = "DownloadImageURL"
        const val OUTPUT_FILE_NAME = "OutputFileName"
    }
}
