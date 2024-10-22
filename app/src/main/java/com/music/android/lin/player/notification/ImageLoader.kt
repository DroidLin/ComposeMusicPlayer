package com.music.android.lin.player.notification

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author liuzhongao
 * @since 2023/11/15 15:03
 */

suspend fun fetchImageBitmap(imageResourceUrl: String): Bitmap? {
    val futureTask = Glide.with(ApplicationWrapper)
        .asBitmap()
        .load(imageResourceUrl)
        .submit()
    return withContext(Dispatchers.IO) { futureTask.get() }
}