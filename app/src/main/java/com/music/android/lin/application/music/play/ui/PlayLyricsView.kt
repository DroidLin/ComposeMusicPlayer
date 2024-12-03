package com.music.android.lin.application.music.play.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.model.SimpleLineLyricOutput

@Composable
fun PlayLyricsView(
    progress: Float,
    lyricOutput: LyricOutput?,
    modifier: Modifier = Modifier,
) {
    val progressState = rememberUpdatedState(progress)
    val lyricOutputState = rememberUpdatedState(lyricOutput)

    when (val output = lyricOutputState.value) {
        is SimpleLineLyricOutput -> SimpleLineLyricView(
            modifier = modifier,
            progress = progressState,
            lyricOutput = output
        )

        else -> Box(modifier = modifier)
    }
}

@Composable
private fun SimpleLineLyricView(
    progress: State<Float>,
    lyricOutput: SimpleLineLyricOutput,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.bodyMedium.copy(
        color = LocalContentColor.current
    )

    val animate = remember { Animatable(0f) }

    Canvas(modifier = modifier) {
        translate(left = 0f, top = 0f) {
            lyricOutput.lyricEntities.forEachIndexed { index, lyricLine ->
                drawText(
                    textMeasurer = textMeasurer,
                    text = lyricLine.lyricString,
                    style = textStyle,
                    topLeft = Offset(0f, textStyle.fontSize.toDp().toPx() * index)
                )
            }
        }
    }
}
