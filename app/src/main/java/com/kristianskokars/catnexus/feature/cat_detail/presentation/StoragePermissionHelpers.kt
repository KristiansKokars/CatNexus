package com.kristianskokars.catnexus.feature.cat_detail.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat

fun isPermissionToSavePicturesGranted(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return true

    return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
}

fun askForStoragePermissionIfOnOlderAndroid(
    context: Context,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    block: () -> Unit
) {
    // We are allowed to use Media Storage above and including Android 10 without needing a permission
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        block()
        return
    }

    if (isPermissionToSavePicturesGranted(context)) {
        block()
    } else {
        launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
