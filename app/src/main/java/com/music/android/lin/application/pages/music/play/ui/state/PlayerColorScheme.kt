package com.music.android.lin.application.pages.music.play.ui.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

@Stable
data class PlayerColorScheme(
    val textColor: Color = Color.White,
    val slideBarActiveColor: Color = Color.White,
    val slideBarInactiveColor: Color = Color.White.copy(alpha = 0.5f),
    val backgroundMaskColor: Color = Color.Transparent.copy(alpha = 0.5f),
)