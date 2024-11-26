package com.music.android.lin.application.music.play.ui.state

import androidx.compose.ui.graphics.Color

data class PlayerColorScheme(
    val dominantColor: Color = Color.Transparent,
    val swatchesColorList: List<Color> = emptyList()
)

val PlayerColorScheme.availableColor: Color?
    get() {
        if (dominantColor != Color.Transparent) {
            return dominantColor
        }
        if (swatchesColorList.isNotEmpty()) {
            return swatchesColorList.first()
        }
        return null
    }