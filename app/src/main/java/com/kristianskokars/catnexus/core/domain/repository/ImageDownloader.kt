package com.kristianskokars.catnexus.core.domain.repository

import android.net.Uri
import com.kristianskokars.catnexus.lib.Result

interface ImageDownloader {
    suspend fun downloadImage(url: String, fileName: String): Result<Unit, Uri>
}
