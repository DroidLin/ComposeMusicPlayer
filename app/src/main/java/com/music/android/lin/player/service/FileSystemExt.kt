package com.music.android.lin.player.service

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/11 14:27
 */

internal fun <T : Serializable> readObject(file: File): T? {
    return runCatching {
        ObjectInputStream(FileInputStream(file)).use { inputStream ->
            inputStream.readObject() as? T
        }
    }.onFailure { it.printStackTrace() }.getOrNull()
}

internal fun <T : Serializable> writeObject(file: File, value: T?) {
    runCatching {
        ObjectOutputStream(FileOutputStream(file)).use { outputStream ->
            outputStream.writeObject(value)
            outputStream.flush()
        }
    }.onFailure { it.printStackTrace() }
}
