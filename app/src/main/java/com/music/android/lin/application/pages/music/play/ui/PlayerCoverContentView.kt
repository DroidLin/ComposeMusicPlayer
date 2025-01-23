package com.music.android.lin.application.pages.music.play.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.pages.music.play.model.formatAudioTimestamp
import com.music.android.lin.application.pages.music.play.ui.component.PlayButton
import com.music.android.lin.application.pages.music.play.ui.component.PlayerPageSeekbarTrack
import com.music.android.lin.application.pages.music.play.ui.state.PlayerColorScheme
import com.music.android.lin.application.pages.music.play.ui.state.PlayerState
import com.music.android.lin.player.metadata.PlayMode

internal val ContentHorizontalPadding = 32.dp

@Composable
fun PlayerCoverContentView(
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
                .fillMaxWidth()
                .padding(horizontal = ContentHorizontalPadding),
            playCover = playerState.mediaCoverPainter,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        PlayerInformationView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentHorizontalPadding),
            title = playState.musicItem?.musicName ?: "",
            subTitle = playState.musicItem?.musicDescription ?: "",
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        PlayControlPanel(
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
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3f)
        )
    }
}

@Composable
fun PlayerCover(
    playCover: Painter?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f, true)
    ) {
        if (playCover != null) {
            Image(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = MaterialTheme.shapes.large),
                painter = playCover,
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )
        } else {
            Spacer(modifier = Modifier.matchParentSize())
        }
    }
}

@Composable
private fun PlayerInformationView(
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProgressView(
    progress: Float,
    currentDuration: Long,
    playerColorScheme: PlayerColorScheme,
    updateSliderProgress: (Float, Boolean) -> Unit,
    seekToPosition: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        val slideBarActiveColor = playerColorScheme.slideBarActiveColor ?: LocalContentColor.current
        val slideBarInactiveColor =
            playerColorScheme.slideBarInactiveColor ?: LocalContentColor.current.copy(alpha = 0.5f)
        val colors = SliderDefaults.colors(
            thumbColor = slideBarActiveColor,
            activeTrackColor = slideBarActiveColor,
            activeTickColor = slideBarActiveColor,
            inactiveTrackColor = slideBarInactiveColor,
            inactiveTickColor = slideBarInactiveColor,
            disabledThumbColor = slideBarInactiveColor,
            disabledActiveTrackColor = slideBarInactiveColor,
            disabledActiveTickColor = slideBarInactiveColor,
            disabledInactiveTrackColor = slideBarInactiveColor,
            disabledInactiveTickColor = slideBarInactiveColor,
        )
        val interactionSource = remember { MutableInteractionSource() }
        val inTouchMode = remember { mutableStateOf(false) }
        val valueProgressRecord = remember { mutableStateOf(0f) }
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            value = progress,
            onValueChange = {
                valueProgressRecord.value = it
                inTouchMode.value = true
                updateSliderProgress(it, true)
            },
            onValueChangeFinished = {
                seekToPosition((valueProgressRecord.value * currentDuration).toLong())
                updateSliderProgress(-1f, false)
                inTouchMode.value = false
            },
            thumb = { Box(modifier = Modifier) },
            colors = colors,
            interactionSource = interactionSource,
            track = { sliderState ->
                PlayerPageSeekbarTrack(
                    modifier = Modifier.padding(horizontal = 1.dp),
                    enabled = true,
                    sliderState = sliderState,
                    inTouchMode = inTouchMode,
                    colors = colors
                )
            }
        )
        Row {
            Text(
                modifier = Modifier,
                text = formatAudioTimestamp((progress * currentDuration).toLong()),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier,
                text = formatAudioTimestamp(currentDuration),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PlayControlPanel(
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
            val resourceId = when (playMode) {
                PlayMode.Single, PlayMode.SingleLoop -> R.drawable.ic_single_cycle
                PlayMode.PlayListLoop -> R.drawable.ic_list
                PlayMode.Shuffle -> R.drawable.ic_random
                else -> R.drawable.ic_list
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