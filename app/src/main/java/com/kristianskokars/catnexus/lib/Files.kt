package com.kristianskokars.catnexus.lib

// we are assuming the given file will always be an image type
fun mimeTypeFromImageUrl(url: String): String {
    val extension = url.split(".").last().lowercase()
    return when (extension) {
        "gif" -> "image/gif"
        else -> "image/jpeg"
    }
}

fun String.extensionFromImageUrl(): String = split(".").last().lowercase()
