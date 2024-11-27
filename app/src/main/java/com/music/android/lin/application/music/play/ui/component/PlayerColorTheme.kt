package com.music.android.lin.application.music.play.ui.component

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme


internal val LocalPlayerCustomUiColorScheme =
    compositionLocalOf<PlayerColorScheme> { throw RuntimeException("Not implemented yet.") }

@Composable
fun PlayerColorTheme(
    playerColorScheme: State<PlayerColorScheme>,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        values = arrayOf(
            LocalContentColor provides playerColorScheme.value.textColor,
            LocalPlayerCustomUiColorScheme provides playerColorScheme.value
        ),
        content = content
    )
}