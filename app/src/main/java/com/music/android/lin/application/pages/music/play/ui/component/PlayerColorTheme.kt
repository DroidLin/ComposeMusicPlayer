package com.music.android.lin.application.pages.music.play.ui.component

import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.music.android.lin.application.pages.music.play.ui.state.PlayerColorScheme

@Composable
fun PlayerColorTheme(
    playerColorScheme: PlayerColorScheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        values = arrayOf(
            LocalContentColor provides playerColorScheme.textColor,
        ),
        content = content
    )
}