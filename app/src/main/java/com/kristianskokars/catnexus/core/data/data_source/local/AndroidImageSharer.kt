package com.kristianskokars.catnexus.core.data.data_source.local

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.content.FileProvider
import com.kristianskokars.catnexus.core.domain.repository.ImageSharer
import com.kristianskokars.catnexus.lib.extensionFromImageUrl
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.random.Random

class AndroidImageSharer(
    @ApplicationContext private val context: Context,
    private val ioScope: CoroutineScope
) : ImageSharer {
    override fun shareImage(url: String) {
        ioScope.launch {
            try {
                // TODO: catch exceptions
                val shareCat = Intent(Intent.ACTION_SEND)
                shareCat.setType("image/*")

                val path = File(context.filesDir, "sharedimages")
                path.mkdirs()
                val newFile = File(path, "${Random.nextInt(from = 0, until = Int.MAX_VALUE)}.${url.extensionFromImageUrl()}")
                newFile.delete()
                newFile.createNewFile()
                val stream = FileOutputStream("$newFile")

                URL(url).openStream().use { it.copyTo(stream) }

                stream.close()
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "com.kristianskokars.catnexus.fileprovider",
                    newFile
                )

                shareCat.putExtra(Intent.EXTRA_STREAM, contentUri)

                val shareCatChooser = Intent.createChooser(shareCat, null)
                shareCatChooser.addFlags(FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(shareCatChooser)
            } catch (e: Exception) {
                Timber.e(e, "Failed to share url: $url")
            }
        }
    }
}
