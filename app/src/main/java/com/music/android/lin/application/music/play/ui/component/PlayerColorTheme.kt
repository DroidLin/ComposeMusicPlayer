package com.music.android.lin.application.music.play.ui.component

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme
import com.music.android.lin.application.music.play.ui.state.availableColor

internal data class PlayerCustomUiColorScheme(
    val textColor: Color = Color.White,
    val slideBarActiveColor: Color = Color.White,
    val slideBarInactiveColor: Color = Color.White.copy(alpha = 0.5f),
    val backgroundMaskColor: Color = Color.Black,
    val isLightStyle: Boolean = false,
)

internal val LocalPlayerCustomUiColorScheme =
    compositionLocalOf<PlayerCustomUiColorScheme> { throw RuntimeException("Not implemented yet.") }

@Composable
fun PlayerColorTheme(
    playerColorScheme: State<PlayerColorScheme>,
    content: @Composable () -> Unit,
) {
    val color = remember {
        derivedStateOf {
            val availableColor = playerColorScheme.value.availableColor
                ?: return@derivedStateOf PlayerCustomUiColorScheme()
            val hslColor = FloatArray(3).also { array ->
                ColorUtils.colorToHSL(availableColor.toArgb(), array)
            }

            val textColor = ColorUtils.HSLToColor(hslColor.copyOf().also { it[2] = 0.85f })
            val slideBarActiveColor =
                ColorUtils.HSLToColor(hslColor.copyOf().also { it[2] = 0.75f })
            val slideBarInactiveColor = ColorUtils.setAlphaComponent(
                ColorUtils.HSLToColor(hslColor.copyOf().also { it[2] = 0.75f }),
                0x7f
            )
            val backgroundMaskColor = ColorUtils.setAlphaComponent(
                ColorUtils.HSLToColor(hslColor.copyOf().also { it[2] = 0.25f }),
                0x60
            )

            PlayerCustomUiColorScheme(
                textColor = Color(textColor),
                slideBarActiveColor = Color(slideBarActiveColor),
                slideBarInactiveColor = Color(slideBarInactiveColor),
                backgroundMaskColor = Color(backgroundMaskColor)
            )
        }
    }
    CompositionLocalProvider(
        values = arrayOf(
            LocalContentColor provides color.value.textColor,
            LocalPlayerCustomUiColorScheme provides color.value
        ),
        content = content
    )
}