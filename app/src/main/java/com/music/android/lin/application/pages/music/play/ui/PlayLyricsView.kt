package com.music.android.lin.application.pages.music.play.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.pages.music.play.model.LyricOutput
import com.music.android.lin.application.pages.music.play.ui.component.LyricView
import com.music.android.lin.application.pages.music.play.ui.component.LyricViewState
import com.music.android.lin.application.pages.music.play.ui.component.rememberLyricViewState

@Composable
fun PlayLyricsView(
    currentPosition: Long,
    playState: PlayState,
    lyricOutput: LyricOutput?,
    lyricViewState: LyricViewState = rememberLyricViewState(),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        PlayLyricInformationView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            title = playState.musicItem?.musicName ?: "",
            subTitle = playState.musicItem?.musicDescription ?: "",
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
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

@Composable
private fun PlayLyricInformationView(
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Text(
            text = subTitle,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = LocalContentColor.current.copy(alpha = 0.75f)
        )
    }
}