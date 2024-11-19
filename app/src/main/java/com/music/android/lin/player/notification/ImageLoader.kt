package com.music.android.lin.player.notification

import android.content.Context
import android.graphics.Bitmap
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.modules.applicationContext

/**
 * @author liuzhongao
 * @since 2023/11/15 15:03
 */

suspend fun fetchImageBitmap(imageResourceUrl: String): Bitmap? {
    val context: Context = AppKoin.applicationContext
    val imageLoader = SingletonImageLoader.get(context)
    val imageRequest = ImageRequest.Builder(context)
        .data(imageResourceUrl)
        .allowHardware(false)
        .build()
    val result = imageLoader.execute(imageRequest)
    return result.image?.toBitmap()
}