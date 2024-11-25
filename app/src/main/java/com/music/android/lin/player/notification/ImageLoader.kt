package com.music.android.lin.player.notification

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.painter.Painter
import coil3.SingletonImageLoader
import coil3.compose.asPainter
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.modules.applicationContext


suspend fun fetchImageAsPainter(imageResourceUrl: String?): Painter? {
    if (imageResourceUrl == null) return null
    val context: Context = AppKoin.applicationContext
    val imageLoader = SingletonImageLoader.get(context)
    val imageRequest = ImageRequest.Builder(context)
        .data(imageResourceUrl)
        .allowHardware(false)
        .build()
    val result = imageLoader.execute(imageRequest)
    return result.image?.asPainter(context, FilterQuality.None)
}

suspend fun fetchImageAsBitmap(imageResourceUrl: String?): Bitmap? {
    imageResourceUrl ?: return null
    val context: Context = AppKoin.applicationContext
    val imageLoader = SingletonImageLoader.get(context)
    val imageRequest = ImageRequest.Builder(context)
        .data(imageResourceUrl)
        .allowHardware(false)
        .build()
    val result = imageLoader.execute(imageRequest)
    return result.image?.toBitmap()
}