package com.music.android.lin.application.music.play.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.ui.component.PlayerColorTheme
import com.music.android.lin.application.music.play.ui.state.PlayerColorScheme
import com.music.android.lin.application.music.play.ui.state.PlayerState
import com.music.android.lin.application.music.play.ui.vm.PlayerLyricViewModel
import com.music.android.lin.application.music.play.ui.vm.PlayerPageViewModel
import com.music.android.lin.application.util.SystemBarStyleComponent
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

    val lyricViewModel = koinViewModel<PlayerLyricViewModel>()
    val lyricOutputState = lyricViewModel.lyricOutput.collectAsState()

    SystemBarStyleComponent(isLightMode = false)
    PlayerHorizontalPagerView(
        modifier = modifier,
        playerState = playerState,
        playState = playState,
        skipToPrevButtonPressed = playViewModel::skipToPrev,
        playOrPauseButtonPressed = playViewModel::playButtonPressed,
        skipToNextButtonPressed = playViewModel::skipToNext,
        backPressed = backPressed,
        seekToPosition = playViewModel::seekToPosition,
        updateSliderProgress = playerPageViewModel::handleSliderInput,
        lyricOutput = lyricOutputState
    )
}

@Composable
private fun PlayerHorizontalPagerView(
    playState: State<PlayState>,
    playerState: State<PlayerState>,
    lyricOutput: State<LyricOutput?>,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    backPressed: () -> Unit,
    seekToPosition: (Long) -> Unit,
    updateSliderProgress: (Float, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val playerColorScheme = remember { derivedStateOf { playerState.value.colorScheme } }
    val pagerState = rememberPagerState(1) { 3 }
    PlayerColorTheme(playerColorScheme = playerColorScheme) {
        Box(
            modifier = modifier
        ) {
            PlayerBackground(
                modifier = Modifier.matchParentSize(),
                playCover = remember {
                    derivedStateOf {
                        playerState.value.mediaBackgroundPainter
                    }
                },
                backgroundColorState = playerColorScheme
            )
            HorizontalPager(
                modifier = Modifier.matchParentSize(),
                state = pagerState,
            ) { pageIndex ->
                when (pageIndex) {
                    1 -> PlayerCoverContentView(
                        modifier = Modifier.fillMaxSize(),
                        playerState = playerState,
                        playState = playState,
                        skipToPrevButtonPressed = skipToPrevButtonPressed,
                        playOrPauseButtonPressed = playOrPauseButtonPressed,
                        skipToNextButtonPressed = skipToNextButtonPressed,
                        backPressed = backPressed,
                        seekToPosition = seekToPosition,
                        updateSliderProgress = updateSliderProgress
                    )

                    2 -> PlayLyricsView(
                        modifier = Modifier.fillMaxSize(),
                        progress = playerState.value.progress,
                        lyricOutput = lyricOutput.value
                    )

                    else -> Box(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

private const val ContentSwitchDuration = 1500
private const val backgroundAnimationDelayMillis = 0

private val backgroundFloatAnimation = tween<Float>(
    durationMillis = ContentSwitchDuration,
    delayMillis = backgroundAnimationDelayMillis
)
private val backgroundColorAnimation = tween<Color>(
    durationMillis = ContentSwitchDuration,
    delayMillis = backgroundAnimationDelayMillis
)

@Composable
private fun PlayerBackground(
    playCover: State<Painter?>,
    backgroundColorState: State<PlayerColorScheme>,
    modifier: Modifier = Modifier,
) {
    val backgroundMaskColor = animateColorAsState(
        targetValue = backgroundColorState.value.backgroundMaskColor,
        label = "background_mask_animation",
        animationSpec = backgroundColorAnimation
    )
    Box(
        modifier = modifier.drawWithContent {
            this.drawRect(color = Color.Black)
            this.drawContent()
            this.drawRect(color = backgroundMaskColor.value)
        }
    ) {
        AnimatedContent(
            modifier = Modifier.matchParentSize(),
            targetState = playCover.value,
            label = "player_background_animation",
            transitionSpec = {
                fadeIn(backgroundFloatAnimation) togetherWith
                        fadeOut(backgroundFloatAnimation)
            }
        ) { playCoverPainter ->
            if (playCoverPainter != null) {
                Image(
                    modifier = Modifier
                        .fillMaxSize(),
                    painter = playCoverPainter,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                )
            }
        }
    }
}
