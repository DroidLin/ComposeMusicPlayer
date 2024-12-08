package com.music.android.lin.application.music.play.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.music.play.model.formatAudioTimestamp
import com.music.android.lin.application.music.play.ui.component.PlayButton
import com.music.android.lin.application.music.play.ui.component.PlayerPageSeekbarTrack
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme
import com.music.android.lin.application.music.play.ui.state.PlayerInformationState
import com.music.android.lin.application.music.play.ui.state.PlayerState

private val ContentHorizontalPadding = 32.dp

@Composable
fun PlayerCoverContentView(
    playState: State<PlayState>,
    playerState: State<PlayerState>,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    backPressed: () -> Unit,
    seekToPosition: (Long) -> Unit,
    updateSliderProgress: (Float, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val playerColorScheme = remember { derivedStateOf { playerState.value.colorScheme } }
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
            playCover = remember {
                derivedStateOf { playerState.value.mediaCoverPainter }
            },
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        )
        PlayerInformationView(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentHorizontalPadding),
            informationState = remember {
                derivedStateOf {
                    PlayerInformationState(
                        title = playState.value.musicItem?.musicName ?: "",
                        subTitle = playState.value.musicItem?.musicDescription ?: ""
                    )
                }
            },
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
            progress = remember { derivedStateOf { playerState.value.progress } },
            currentDuration = remember { derivedStateOf { playerState.value.currentDuration } },
            seekToPosition = seekToPosition,
            updateSliderProgress = updateSliderProgress,
            playerColorScheme = playerColorScheme
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
        PlayControlPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ContentHorizontalPadding),
            isPlayingState = remember { derivedStateOf { playState.value.isPlaying } },
            skipToPrevButtonPressed = skipToPrevButtonPressed,
            playOrPauseButtonPressed = playOrPauseButtonPressed,
            skipToNextButtonPressed = skipToNextButtonPressed,
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
        )
    }
}

@Composable
private fun PlayerCover(
    playCover: State<Painter?>,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f, true)
    ) {
        val playCoverPainter = playCover.value
        if (playCoverPainter != null) {
            Image(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape = MaterialTheme.shapes.large),
                painter = playCoverPainter,
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
    informationState: State<PlayerInformationState>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = informationState.value.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
        Text(
            text = informationState.value.subTitle,
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
    progress: State<Float>,
    currentDuration: State<Long>,
    playerColorScheme: State<PlayerColorScheme>,
    updateSliderProgress: (Float, Boolean) -> Unit,
    seekToPosition: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        val colors = SliderDefaults.colors(
            thumbColor = playerColorScheme.value.slideBarActiveColor,
            activeTrackColor = playerColorScheme.value.slideBarActiveColor,
            activeTickColor = playerColorScheme.value.slideBarActiveColor,
            inactiveTrackColor = playerColorScheme.value.slideBarInactiveColor,
            inactiveTickColor = playerColorScheme.value.slideBarInactiveColor,
            disabledThumbColor = playerColorScheme.value.slideBarInactiveColor,
            disabledActiveTrackColor = playerColorScheme.value.slideBarInactiveColor,
            disabledActiveTickColor = playerColorScheme.value.slideBarInactiveColor,
            disabledInactiveTrackColor = playerColorScheme.value.slideBarInactiveColor,
            disabledInactiveTickColor = playerColorScheme.value.slideBarInactiveColor,
        )
        val interactionSource = remember { MutableInteractionSource() }
        val inTouchMode = remember { mutableStateOf(false) }
        val valueProgressRecord = remember { mutableStateOf(0f) }
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp),
            value = progress.value,
            onValueChange = {
                valueProgressRecord.value = it
                inTouchMode.value = true
                updateSliderProgress(it, true)
            },
            onValueChangeFinished = {
                seekToPosition((valueProgressRecord.value * currentDuration.value).toLong())
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
            val currentProgressText = remember {
                derivedStateOf {
                    formatAudioTimestamp((progress.value * currentDuration.value).toLong())
                }
            }
            val currentDurationText = remember {
                derivedStateOf {
                    formatAudioTimestamp(currentDuration.value)
                }
            }
            Text(
                modifier = Modifier,
                text = currentProgressText.value,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier,
                text = currentDurationText.value,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
private fun PlayControlPanel(
    isPlayingState: State<Boolean>,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            AnimatedContent(
                targetState = isPlayingState.value,
                label = "is_playing_or_pause_button_animation",
                transitionSpec = {
                    (scaleIn(initialScale = 0.5f) + fadeIn()) togetherWith (scaleOut(targetScale = 0.5f) + fadeOut())
                }
            ) { isPlaying ->
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
    }
}