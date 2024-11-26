package com.music.android.lin.application.music.play.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.music.play.model.formatAudioTimestamp
import com.music.android.lin.application.music.play.ui.component.PlayerPageSeekbarTrack
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme
import com.music.android.lin.application.music.play.ui.state.PlayerState
import com.music.android.lin.application.music.play.ui.vm.PlayerPageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerView(
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playViewModel = koinViewModel<PlayViewModel>()
    val playState = playViewModel.playState.collectAsStateWithLifecycle()

    val playerPageViewModel = koinViewModel<PlayerPageViewModel>()
    val playerState = playerPageViewModel.playerState.collectAsState()

    PlayerContentView(
        modifier = modifier,
        playerState = playerState,
        playState = playState,
        skipToPrevButtonPressed = playViewModel::skipToPrev,
        playOrPauseButtonPressed = playViewModel::playButtonPressed,
        skipToNextButtonPressed = playViewModel::skipToNext,
        backPressed = backPressed,
        seekToPosition = playViewModel::seekToPosition,
        updateSliderProgress = playerPageViewModel::handleSliderInput
    )
}

@Composable
private fun PlayerContentView(
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
    val mediaCover = remember { derivedStateOf { playerState.value.mediaCoverPainter } }
    val mediaBackgroundCover =
        remember { derivedStateOf { playerState.value.mediaBackgroundPainter } }
    Box(
        modifier = modifier
    ) {
        PlayerBackground(
            playCover = mediaBackgroundCover,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .matchParentSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            PlayerHeader(
                modifier = Modifier,
                backPressed = backPressed,
            )
            PlayerCover(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                playCover = mediaCover,
            )
            PlayerProgressView(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                progress = remember { derivedStateOf { playerState.value.progress } },
                currentProgress = remember { derivedStateOf { playerState.value.currentProgress } },
                currentDuration = remember { derivedStateOf { playerState.value.currentDuration } },
                seekToPosition = seekToPosition,
                updateSliderProgress = updateSliderProgress,
            )
            PlayControlPanel(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                isPlaying = remember { derivedStateOf { playState.value.isPlaying } },
                skipToPrevButtonPressed = skipToPrevButtonPressed,
                playOrPauseButtonPressed = playOrPauseButtonPressed,
                skipToNextButtonPressed = skipToNextButtonPressed,
            )
            PlayerColorSchemeViewer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colorScheme = remember { derivedStateOf { playerState.value.colorScheme } },
            )
        }
    }
}

@Composable
private fun PlayerBackground(
    playCover: State<Painter?>,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = playCover.value,
        label = "player_background_animation",
        transitionSpec = {
            fadeIn(tween(800)) togetherWith fadeOut(tween(800))
        }
    ) { playCoverPainter ->
        if (playCoverPainter != null) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = playCoverPainter,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerHeader(
    backPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = backPressed
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        },
        windowInsets = WindowInsets(0, 0, 0, 0),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent
        )
    )
}

@Composable
private fun PlayerCover(
    playCover: State<Painter?>,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        modifier = modifier,
        targetState = playCover.value,
        label = "player_background_animation",
        transitionSpec = {
            fadeIn(tween(800)) togetherWith fadeOut(tween(800))
        }
    ) { playCoverPainter ->
        if (playCoverPainter != null) {
            Image(
                modifier = Modifier
                    .aspectRatio(1f, true)
                    .clip(shape = MaterialTheme.shapes.large),
                painter = playCoverPainter,
                contentDescription = "",
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .aspectRatio(1f, true)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProgressView(
    progress: State<Float>,
    currentProgress: State<Long>,
    currentDuration: State<Long>,
    updateSliderProgress: (Float, Boolean) -> Unit,
    seekToPosition: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        val colors = SliderDefaults.colors()
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
                    formatAudioTimestamp(currentProgress.value)
                }
            }
            val currentDurationText = remember {
                derivedStateOf {
                    formatAudioTimestamp(currentDuration.value)
                }
            }
            val alpha = animateFloatAsState(
                targetValue = if (inTouchMode.value) {
                    0.3f
                } else 1f,
                label = "normal_text_animation"
            )
            Text(
                modifier = Modifier
                    .graphicsLayer { this.alpha = alpha.value },
                text = currentProgressText.value,
                style = MaterialTheme.typography.bodySmall
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = inTouchMode.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val inputProgressText = remember {
                    derivedStateOf {
                        formatAudioTimestamp((progress.value * currentDuration.value).toLong())
                    }
                }
                Surface(
                    modifier = Modifier.padding(start = 5.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 5.dp),
                        text = inputProgressText.value,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier
                    .graphicsLayer { this.alpha = alpha.value },
                text = currentDurationText.value,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun PlayControlPanel(
    isPlaying: State<Boolean>,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun PlayerColorSchemeViewer(
    colorScheme: State<PlayerColorScheme>,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = colorScheme.value,
        transitionSpec = {
            fadeIn(tween(800)) togetherWith fadeOut(tween(800))
        },
        contentAlignment = Alignment.Center
    ) { scheme ->
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp)
                    .drawBehind { drawRect(color = scheme.lightVibrant) }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp)
                    .drawBehind { drawRect(color = scheme.vibrant) }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp)
                    .drawBehind { drawRect(color = scheme.darkVibrant) }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp)
                    .drawBehind { drawRect(color = scheme.lightMuted) }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp)
                    .drawBehind { drawRect(color = scheme.muted) }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(56.dp)
                    .drawBehind { drawRect(color = scheme.darkMuted) }
            )
        }
    }
}