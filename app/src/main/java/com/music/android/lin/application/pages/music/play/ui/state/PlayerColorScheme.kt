package com.music.android.lin.application.pages.music.play.ui.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

@Stable
data class PlayerColorScheme(
    val textColor: Color? = null,
    val slideBarActiveColor: Color? = null,
    val slideBarInactiveColor: Color? = null,
    val backgroundMaskColor: Color? = null,
)

val Color.isLightBgColor: Boolean
    get() = FloatArray(3).let {
        ColorUtils.colorToHSL(this.toArgb(), it)
        it[2] >= 0.7f
    }