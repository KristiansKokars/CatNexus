package com.kristianskokars.simplecatapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.kristianskokars.simplecatapp.core.CHANNEL_DESCRIPTION
import com.kristianskokars.simplecatapp.core.CHANNEL_ID
import com.kristianskokars.simplecatapp.core.CHANNEL_NAME
import dagger.hilt.android.HiltAndroidApp
import io.sentry.Sentry
import javax.inject.Inject

@HiltAndroidApp
class CatApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    private val exceptionHandler =
        Thread.UncaughtExceptionHandler { _: Thread, e: Throwable ->
            handleUncaughtException(e)
        }

    override fun onCreate() {
        super.onCreate()
        attachUnhandledExceptionHandler()
        createNotificationChannel()
    }

    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder().setWorkerFactory(workerFactory).build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun attachUnhandledExceptionHandler() {
        if (BuildConfig.DEBUG.not()) {
            Thread.setDefaultUncaughtExceptionHandler(exceptionHandler)
        }
    }

    private fun handleUncaughtException(e: Throwable) {
        Sentry.captureException(e)
        Toast.makeText(this, getString(R.string.error_unexpected_error, e.localizedMessage), Toast.LENGTH_SHORT).show()
    }
}
