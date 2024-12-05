package com.music.android.lin.application.music.play.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.ui.component.LyricView
import com.music.android.lin.application.music.play.ui.component.LyricViewState
import com.music.android.lin.application.music.play.ui.component.rememberLyricViewState

@Composable
fun PlayLyricsView(
    currentPosition: State<Long>,
    lyricOutput: State<LyricOutput?>,
    lyricViewState: LyricViewState = rememberLyricViewState(),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        LyricView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            currentPosition = currentPosition,
            lyricOutput = lyricOutput,
            lyricViewState = lyricViewState,
        )
    }
}
