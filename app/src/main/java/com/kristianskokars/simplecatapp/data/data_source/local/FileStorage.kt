package com.kristianskokars.simplecatapp.data.data_source.local

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FileStorage {
    suspend fun downloadImage(url: String, fileName: String): Uri?
}

class FileStorageImpl(private val context: Context) : FileStorage {
    override suspend fun downloadImage(url: String, fileName: String): Uri? {
        val resolver = context.contentResolver

        val imageCollection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        // TODO: needs an error here
        val newImageUri = resolver.insert(imageCollection, newImageDetails) ?: return null
        writeImageToFileFromUrl(resolver, newImageUri, url)
        newImageDetails.clear()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
        }
        resolver.update(newImageUri, newImageDetails, null, null)
        return newImageUri
    }

    private suspend fun writeImageToFileFromUrl(resolver: ContentResolver, uri: Uri, url: String) =
        // TODO: needs an error here?
        suspendCoroutine<Unit> { continuation ->
            // We are doing something pretty hacky here, so I shall explain it:
            // Basically, to avoid downloading the image twice, we use Coil to get the URL and it retrieves it from it's own cache.
            val loader = ImageLoader.Builder(context).build() // TODO: once per application, so not injecting it
            val request = ImageRequest.Builder(context)
                .data(url)
                .target(
                    onSuccess = { image ->
                        // TODO: try/catch here just in case
                        resolver.openFileDescriptor(uri, "w", null)
                            .use { pfd ->
                                pfd?.let {
                                    val fos = FileOutputStream(it.fileDescriptor)
                                    image.toBitmap()
                                        .compress(Bitmap.CompressFormat.JPEG, 100, fos)
                                    fos.close()
                                }
                            }
                        continuation.resume(Unit)
                    }
                )
                .build()
            loader.enqueue(request)
        }
}
