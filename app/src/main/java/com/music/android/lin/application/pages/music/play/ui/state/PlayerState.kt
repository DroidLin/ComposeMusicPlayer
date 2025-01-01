package com.music.android.lin.application.pages.music.play.ui.state

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.painter.Painter

@Stable
data class PlayerState(
    val progress: Float = 0f,
    val currentDuration: Long = 0L,
    val mediaCoverBitmap: Bitmap? = null,
    val mediaCoverPainter: Painter? = null,
    val mediaBackgroundPainter: Painter? = null,
    val colorScheme: PlayerColorScheme = PlayerColorScheme()
)