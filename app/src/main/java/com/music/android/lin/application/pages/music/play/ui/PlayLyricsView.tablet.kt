package com.music.android.lin.application.pages.music.play.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.pages.music.play.model.LyricOutput
import com.music.android.lin.application.pages.music.play.ui.component.LyricView
import com.music.android.lin.application.pages.music.play.ui.component.LyricViewState
import com.music.android.lin.application.pages.music.play.ui.component.rememberLyricViewState

@Composable
fun TabletPlayerLyricView(
    currentPosition: Long,
    lyricOutput: LyricOutput?,
    lyricViewState: LyricViewState = rememberLyricViewState(),
    modifier: Modifier = Modifier,
) {
    LyricView(
        modifier = modifier
            .padding(horizontal = 24.dp),
        currentPosition = currentPosition,
        lyricOutput = lyricOutput,
        lyricViewState = lyricViewState,
    )
}