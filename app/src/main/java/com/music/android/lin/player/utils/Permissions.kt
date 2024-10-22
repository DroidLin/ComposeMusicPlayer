package com.harvest.musicplayer.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * @author liuzhongao
 * @since 2024/1/5 15:37
 */

val storagePermissionList: List<String>
    get() {
        val localStoragePermissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            localStoragePermissions += Manifest.permission.READ_MEDIA_AUDIO
            localStoragePermissions += Manifest.permission.READ_MEDIA_VIDEO
        } else {
            localStoragePermissions += Manifest.permission.READ_EXTERNAL_STORAGE
            localStoragePermissions += Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
        return localStoragePermissions
    }

fun isStoragePermissionGranted(context: Context): Boolean {
    return storagePermissionList.all { singlePermission ->
        ContextCompat.checkSelfPermission(context, singlePermission) == PackageManager.PERMISSION_GRANTED
    }
}