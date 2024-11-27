package com.music.android.lin.application.music.play.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.music.play.model.formatAudioTimestamp
import com.music.android.lin.application.music.play.ui.component.LocalPlayerCustomUiColorScheme
import com.music.android.lin.application.music.play.ui.component.PlayerColorTheme
import com.music.android.lin.application.music.play.ui.component.PlayerPageSeekbarTrack
import com.music.android.lin.application.music.play.ui.state.PlayerState
import com.music.android.lin.application.music.play.ui.vm.PlayerPageViewModel
import org.koin.androidx.compose.koinViewModel

private const val ContentSwitchDuration = 1000

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

private val ContentHorizontalPadding = 32.dp
private val HeaderHorizontalPadding = 16.dp

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
    val mediaCover = remember {
        derivedStateOf { playerState.value.mediaCoverPainter }
    }
    val mediaBackgroundCover = remember {
        derivedStateOf { playerState.value.mediaBackgroundPainter }
    }
    val mediaTitle = remember {
        derivedStateOf { playState.value.musicItem?.musicName ?: "" }
    }
    val mediaSubTitle = remember {
        derivedStateOf { playState.value.musicItem?.musicDescription ?: "" }
    }
    val playerColorScheme = remember { derivedStateOf { playerState.value.colorScheme } }
    PlayerColorTheme(playerColorScheme = playerColorScheme) {
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
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                PlayerHeader(
                    modifier = Modifier
                        .padding(horizontal = HeaderHorizontalPadding),
                    backPressed = backPressed,
                )
                PlayerCover(
                    modifier = Modifier
                        .padding(horizontal = ContentHorizontalPadding),
                    playCover = mediaCover,
                )
                PlayerInformationView(
                    modifier = Modifier
                        .padding(horizontal = ContentHorizontalPadding),
                    mediaTitle = mediaTitle,
                    mediaSubTitle = mediaSubTitle
                )
                PlayerProgressView(
                    modifier = Modifier
                        .padding(horizontal = ContentHorizontalPadding),
                    progress = remember { derivedStateOf { playerState.value.progress } },
                    currentProgress = remember { derivedStateOf { playerState.value.currentProgress } },
                    currentDuration = remember { derivedStateOf { playerState.value.currentDuration } },
                    seekToPosition = seekToPosition,
                    updateSliderProgress = updateSliderProgress,
                )
                PlayControlPanel(
                    modifier = Modifier
                        .padding(horizontal = ContentHorizontalPadding),
                    isPlaying = remember { derivedStateOf { playState.value.isPlaying } },
                    skipToPrevButtonPressed = skipToPrevButtonPressed,
                    playOrPauseButtonPressed = playOrPauseButtonPressed,
                    skipToNextButtonPressed = skipToNextButtonPressed,
                )
            }
        }
    }
}

@Composable
private fun PlayerBackground(
    playCover: State<Painter?>,
    modifier: Modifier = Modifier,
) {
    val updateTransient = updateTransition(LocalPlayerCustomUiColorScheme.current, null)
    val backgroundMaskColor = updateTransient.animateColor(label = "background_mask_animation") { it.backgroundMaskColor }
    AnimatedContent(
        modifier = modifier,
        targetState = playCover.value,
        label = "player_background_animation",
        transitionSpec = {
            fadeIn(tween(ContentSwitchDuration)) togetherWith fadeOut(tween(ContentSwitchDuration))
        }
    ) { playCoverPainter ->
        if (playCoverPainter != null) {
            Image(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        this.drawContent()
                        this.drawRect(color = backgroundMaskColor.value)
                    },
                painter = playCoverPainter,
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier.drawWithContent {
                    this.drawContent()
                    this.drawRect(color = backgroundMaskColor.value)
                }
            )
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
        modifier = modifier,
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
            scrolledContainerColor = Color.Transparent,
            navigationIconContentColor = LocalContentColor.current,
            titleContentColor = LocalContentColor.current,
            actionIconContentColor = LocalContentColor.current,
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
            fadeIn(tween(ContentSwitchDuration)) togetherWith fadeOut(tween(ContentSwitchDuration))
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

@Composable
private fun PlayerInformationView(
    mediaTitle: State<String>,
    mediaSubTitle: State<String>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = mediaTitle.value,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = mediaSubTitle.value,
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
    currentProgress: State<Long>,
    currentDuration: State<Long>,
    updateSliderProgress: (Float, Boolean) -> Unit,
    seekToPosition: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val customUiColorScheme = LocalPlayerCustomUiColorScheme.current
    Column(
        modifier = modifier
    ) {
        val colors = SliderDefaults.colors(
            thumbColor = customUiColorScheme.slideBarActiveColor,
            activeTrackColor = customUiColorScheme.slideBarActiveColor,
            activeTickColor = customUiColorScheme.slideBarActiveColor,
            inactiveTrackColor = customUiColorScheme.slideBarInactiveColor,
            inactiveTickColor = customUiColorScheme.slideBarInactiveColor,
            disabledThumbColor = customUiColorScheme.slideBarInactiveColor,
            disabledActiveTrackColor = customUiColorScheme.slideBarInactiveColor,
            disabledActiveTickColor = customUiColorScheme.slideBarInactiveColor,
            disabledInactiveTrackColor = customUiColorScheme.slideBarInactiveColor,
            disabledInactiveTickColor = customUiColorScheme.slideBarInactiveColor,
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
                enter = fadeIn(tween(ContentSwitchDuration)),
                exit = fadeOut(tween(ContentSwitchDuration)),
                label = "duration_slider_indication_animation"
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