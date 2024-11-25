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
    val lightVibrant = palette.getColorForTarget(Target.LIGHT_VIBRANT, Color.TRANSPARENT)
    val vibrant = palette.getColorForTarget(Target.VIBRANT, Color.TRANSPARENT)
    val darkVibrant = palette.getColorForTarget(Target.DARK_VIBRANT, Color.TRANSPARENT)
    val lightMuted = palette.getColorForTarget(Target.LIGHT_MUTED, Color.TRANSPARENT)
    val muted = palette.getColorForTarget(Target.MUTED, Color.TRANSPARENT)
    val darkMuted = palette.getColorForTarget(Target.DARK_MUTED, Color.TRANSPARENT)

    return PlayerColorScheme(
        lightVibrant = androidx.compose.ui.graphics.Color(lightVibrant),
        vibrant = androidx.compose.ui.graphics.Color(vibrant),
        darkVibrant = androidx.compose.ui.graphics.Color(darkVibrant),
        lightMuted = androidx.compose.ui.graphics.Color(lightMuted),
        muted = androidx.compose.ui.graphics.Color(muted),
        darkMuted = androidx.compose.ui.graphics.Color(darkMuted),
    )
}