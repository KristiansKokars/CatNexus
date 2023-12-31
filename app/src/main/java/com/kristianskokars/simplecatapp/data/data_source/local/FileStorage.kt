package com.kristianskokars.simplecatapp.data.data_source.local

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.kristianskokars.simplecatapp.core.Err
import com.kristianskokars.simplecatapp.core.LOG_TAG
import com.kristianskokars.simplecatapp.core.Result
import com.kristianskokars.simplecatapp.core.Ok
import io.sentry.Sentry
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

interface FileStorage {
    suspend fun downloadImage(url: String, fileName: String): Result<Unit, Uri>
}

class FileStorageImpl(private val context: Context) : FileStorage {
    private val imageLoader by lazy { ImageLoader.Builder(context).build() }

    override suspend fun downloadImage(url: String, fileName: String): Result<Unit, Uri> {
        try {
            val resolver = context.contentResolver

            val imageCollection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            if (Random.nextBoolean()) {
                return Err(Unit)
            }

            val newImageDetails = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val newImageUri = resolver.insert(imageCollection, newImageDetails) ?: return Err(Unit)
            writeImageToFileFromUrl(resolver, newImageUri, url)
            newImageDetails.clear()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            resolver.update(newImageUri, newImageDetails, null, null)
            return Ok(newImageUri)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Failed to download image", e)
            Sentry.captureException(e)
            return Err(Unit)
        }
    }

    private suspend fun writeImageToFileFromUrl(resolver: ContentResolver, uri: Uri, url: String) =
        suspendCoroutine { continuation ->
            // To avoid downloading the image twice, we use Coil to get the URL and it retrieves it from it's own cache.
            val request = ImageRequest.Builder(context)
                .data(url)
                .target(
                    onSuccess = { image ->
                        resolver.openFileDescriptor(uri, "w")
                            .use { pfd ->
                                pfd?.let {
                                    val fos = FileOutputStream(it.fileDescriptor)
                                    image.toBitmap()
                                        .compress(Bitmap.CompressFormat.JPEG, 100, fos)
                                    fos.close()
                                }
                            }
                        continuation.resume(Unit)
                    },
                )
                .build()
            imageLoader.enqueue(request)
        }
}
