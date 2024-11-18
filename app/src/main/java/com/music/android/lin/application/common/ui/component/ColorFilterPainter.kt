package com.music.android.lin.application.common.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun Painter.tintPainter(color: Color): Painter {
    return remember(this, color) {
        ColorFilterPainter(this, ColorFilter.tint(color = color))
    }
}

class ColorFilterPainter(
    private val painter: Painter,
    private val overrideColorFilter: ColorFilter?
) : Painter() {
    override val intrinsicSize: Size get() = this.painter.intrinsicSize

    override fun DrawScope.onDraw() {
        painter.run {
            draw(size = size, colorFilter = overrideColorFilter)
        }
    }
}