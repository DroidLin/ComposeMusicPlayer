package com.music.android.lin.application.music.play.ui.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun pickColorAndTransformToScheme(bitmap: Bitmap?): PlayerColorScheme {
    bitmap ?: return PlayerColorScheme()
    val palette = withContext(Dispatchers.Default) {
        Palette.from(bitmap).generate()
    }

    val dominantColor = palette.dominantSwatch?.rgb ?: Color.TRANSPARENT

    val pickColor = if (dominantColor != Color.TRANSPARENT) {
        dominantColor
    } else requireNotNull(palette.swatches.find { it != null }).rgb
    val hslColor = FloatArray(3).also { array -> ColorUtils.colorToHSL(pickColor, array) }

    val textColor = ColorUtils.HSLToColor(hslColor.copyOf().also { it[2] = 0.85f })
    val slideBarActiveColor = ColorUtils.HSLToColor(hslColor.copyOf().also { it[2] = 0.75f })
    val slideBarInactiveColor = ColorUtils.setAlphaComponent(
        ColorUtils.HSLToColor(
            hslColor.copyOf().also { it[2] = 0.75f }), 0x7f
    )
    val backgroundMaskColor = ColorUtils.setAlphaComponent(
        ColorUtils.HSLToColor(
            hslColor.copyOf().also { it[2] = 0.25f }), 0x7f
    )

    return PlayerColorScheme(
        textColor = androidx.compose.ui.graphics.Color(textColor),
        slideBarActiveColor = androidx.compose.ui.graphics.Color(slideBarActiveColor),
        slideBarInactiveColor = androidx.compose.ui.graphics.Color(slideBarInactiveColor),
        backgroundMaskColor = androidx.compose.ui.graphics.Color(backgroundMaskColor),
    )
}