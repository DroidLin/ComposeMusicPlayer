package com.music.android.lin.application.minibar.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.music.android.lin.application.common.ui.component.padding

private val LocalMinibarConfig =
    compositionLocalOf<Minibar> { throw NotImplementedError("not implemented") }

@Composable
fun MinibarSizeContainer(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalMinibarConfig provides remember { Minibar() },
        content = content
    )
}

@Composable
internal fun Modifier.anchorMinibarContainer(): Modifier = composed {
    val minibar = LocalMinibarConfig.current
    val density = LocalDensity.current
    onSizeChanged { intSize ->
        density.run {
            minibar.minibarSize = MinibarSize(
                width = intSize.width.toDp(),
                height = intSize.height.toDp()
            )
        }
    }
}

@Composable
internal fun Modifier.minibarHeightPadding(): Modifier = composed {
    val minibar = LocalMinibarConfig.current
    padding { bottom = minibar.minibarSize.height }
}

@Stable
internal class Minibar {

    var minibarSize by mutableStateOf(MinibarSize(Dp.Unspecified, Dp.Unspecified))
}

@Immutable
internal data class MinibarSize(
    val width: Dp,
    val height: Dp,
)