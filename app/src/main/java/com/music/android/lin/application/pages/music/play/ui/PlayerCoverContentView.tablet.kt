package com.music.android.lin.application.pages.music.play.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.pages.music.play.ui.component.PlayButton
import com.music.android.lin.application.pages.music.play.ui.state.PlayerState
import com.music.android.lin.player.metadata.PlayMode

@Composable
fun TabletPlayerCoverContentView(
    playState: PlayState,
    playerState: PlayerState,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    switchPlayMode: () -> Unit,
    seekToPosition: (Long) -> Unit,
    updateSliderProgress: (Float, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        PlayerCover(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = ContentHorizontalPadding)
                .align(Alignment.CenterHorizontally),
            playCover = playerState.mediaCoverPainter,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
        PlayerProgressView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentHorizontalPadding),
            progress = playerState.progress,
            currentDuration = playerState.currentDuration,
            seekToPosition = seekToPosition,
            updateSliderProgress = updateSliderProgress,
            playerColorScheme = playerState.colorScheme
        )
        TabletPlayControlPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentHorizontalPadding),
            isPlaying = playState.isPlaying,
            playMode = playState.playMode,
            skipToPrevButtonPressed = skipToPrevButtonPressed,
            playOrPauseButtonPressed = playOrPauseButtonPressed,
            skipToNextButtonPressed = skipToNextButtonPressed,
            switchPlayMode = switchPlayMode
        )
    }
}

@Composable
private fun TabletPlayControlPanel(
    isPlaying: Boolean,
    playMode: PlayMode,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    switchPlayMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayButton(
            onClick = switchPlayMode,
            modifier = Modifier.size(42.dp)
        ) {
            val resourceId = remember(playMode) {
                when (playMode) {
                    PlayMode.Single, PlayMode.SingleLoop -> R.drawable.ic_single_cycle
                    PlayMode.PlayListLoop -> R.drawable.ic_list
                    PlayMode.Shuffle -> R.drawable.ic_random
                    else -> R.drawable.ic_list
                }
            }
            Icon(
                painter = painterResource(resourceId),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        PlayButton(
            onClick = skipToPrevButtonPressed,
            modifier = Modifier
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_skip_previous),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
        }
        PlayButton(
            onClick = playOrPauseButtonPressed,
            modifier = Modifier
        ) {
            if (isPlaying) {
                Icon(
                    painter = painterResource(R.drawable.ic_pause),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            }
        }
        PlayButton(
            onClick = skipToNextButtonPressed,
            modifier = Modifier
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_skip_next),
                contentDescription = null,
                modifier = Modifier.size(42.dp)
            )
        }
        PlayButton(
            onClick = {},
            modifier = Modifier.size(42.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_list),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}