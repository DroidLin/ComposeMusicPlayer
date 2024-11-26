package com.music.android.lin.application.music.play.ui.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun pickColorAndTransformToScheme(bitmap: Bitmap?): PlayerColorScheme {
    bitmap ?: return PlayerColorScheme()
    val palette = withContext(Dispatchers.Default) {
        Palette.from(bitmap).generate()
    }
    val dominantColor = palette.dominantSwatch?.rgb ?: Color.TRANSPARENT
    return PlayerColorScheme(
        dominantColor = androidx.compose.ui.graphics.Color(dominantColor),
        swatchesColorList = palette.swatches.mapNotNull { it?.rgb }.map { androidx.compose.ui.graphics.Color(it) }
    )
}