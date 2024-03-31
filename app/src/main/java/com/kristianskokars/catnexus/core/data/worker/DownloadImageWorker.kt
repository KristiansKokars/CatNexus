package com.kristianskokars.catnexus.core.data.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.kristianskokars.catnexus.R
import com.kristianskokars.catnexus.core.CHANNEL_ID
import com.kristianskokars.catnexus.core.domain.model.UserSettings
import com.kristianskokars.catnexus.core.domain.repository.ImageDownloader
import com.kristianskokars.catnexus.lib.ToastMessage
import com.kristianskokars.catnexus.lib.Toaster
import com.kristianskokars.catnexus.lib.UIText
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DownloadImageWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted parameters: WorkerParameters,
    private val toaster: Toaster,
    private val imageDownloader: ImageDownloader,
    private val userSettingsStore: DataStore<UserSettings>,
) : CoroutineWorker(context, parameters) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        val downloadUrl = inputData.getString(DOWNLOAD_IMAGE_URL) ?: return Result.failure()
        val fileName = inputData.getString(OUTPUT_FILE_NAME) ?: return Result.failure()

        if (userSettingsStore.data.first().showDownloadNotifications) {
            setForeground(createForegroundInfo(downloadUrl))
        }

        return imageDownloader.downloadImage(downloadUrl, fileName).handle(
            onSuccess = { fileUri ->
                showFinishedNotification(downloadUrl, fileUri)
                Result.success()
            },
            onError = {
                showFailedNotification(downloadUrl)
                Result.failure()
            },
        )
    }

    private fun createForegroundInfo(downloadUrl: String): ForegroundInfo {
        val cancelText = context.getString(R.string.cancel_download)
        val contentTitle = context.getString(R.string.downloading_picture)
        val contentText = context.getString(R.string.downloading, downloadUrl)

        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(contentTitle)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_cat)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, cancelText, intent)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ForegroundInfo(DOWNLOAD_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE)
        } else {
            ForegroundInfo(DOWNLOAD_NOTIFICATION_ID, notification)
        }
    }

    private suspend fun showFinishedNotification(downloadUrl: String, fileUri: Uri) {
        toaster.show(ToastMessage(UIText.StringResource(R.string.picture_downloaded)))

        if (userSettingsStore.data.first().showDownloadNotifications) {
            val contentTitle = context.getString(R.string.downloaded_picture)
            val contentText = context.getString(R.string.downloaded_from, downloadUrl)

            val openImageInGallery =
                PendingIntent.getActivity(
                    context,
                    OPEN_IMAGE_REQUEST_CODE,
                    Intent(Intent.ACTION_VIEW, fileUri),
                    PendingIntent.FLAG_IMMUTABLE,
                )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_cat)
                .setContentIntent(openImageInGallery)
                .build()

            with(notificationManager) {
                notify(COMPLETED_DOWNLOAD_NOTIFICATION_ID, notification)
            }
        }
    }

    private suspend fun showFailedNotification(downloadUrl: String) {
        toaster.show(ToastMessage(UIText.StringResource(R.string.failed_download)))

        if (userSettingsStore.data.first().showDownloadNotifications) {
            val contentTitle = context.getString(R.string.error_downloading_picture_title)
            val contentText = context.getString(R.string.error_downloading_picture_text, downloadUrl)

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_cat)
                .build()

            with(notificationManager) {
                notify(FAILED_DOWNLOAD_NOTIFICATION_ID, notification)
            }
        }
    }

    companion object {
        const val DOWNLOAD_IMAGE_URL = "DownloadImageURL"
        const val OUTPUT_FILE_NAME = "OutputFileName"
        const val OPEN_IMAGE_REQUEST_CODE = 1
        const val DOWNLOAD_NOTIFICATION_ID = 1
        const val COMPLETED_DOWNLOAD_NOTIFICATION_ID = 2
        const val FAILED_DOWNLOAD_NOTIFICATION_ID = 3
    }
}
