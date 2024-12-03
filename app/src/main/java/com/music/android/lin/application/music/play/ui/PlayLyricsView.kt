package com.music.android.lin.application.music.play.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.ui.component.LyricView

@Composable
fun PlayLyricsView(
    currentPosition: State<Long>,
    lyricOutput: State<LyricOutput?>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        LyricView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            currentPosition = currentPosition,
            lyricOutput = lyricOutput
        )
    }
}
