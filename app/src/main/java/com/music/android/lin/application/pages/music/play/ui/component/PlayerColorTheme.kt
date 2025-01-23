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
        value = LocalContentColor provides (playerColorScheme.textColor ?: LocalContentColor.current),
        content = content
    )
}