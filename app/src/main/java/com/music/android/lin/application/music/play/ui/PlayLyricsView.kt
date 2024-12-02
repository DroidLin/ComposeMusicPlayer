package com.music.android.lin.application.music.play.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.music.android.lin.application.music.play.model.LyricLine
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.model.SimpleLineLyricOutput

@Composable
fun PlayLyricsView(
    progress: Float,
    lyricOutput: LyricOutput,
    modifier: Modifier = Modifier,
) {
    val progressState = rememberUpdatedState(progress)
    val lyricOutputState = rememberUpdatedState(lyricOutput)

    when (val output = lyricOutputState.value) {
        is SimpleLineLyricOutput -> SimpleLineLyricView(
            modifier = modifier,
            progress = progressState,
            lyricOutput = remember { derivedStateOf { output } }
        )

        else -> Box(modifier = modifier)
    }
}

@Composable
private fun SimpleLineLyricView(
    progress: State<Float>,
    lyricOutput: State<SimpleLineLyricOutput>,
    modifier: Modifier = Modifier,
) {
}
