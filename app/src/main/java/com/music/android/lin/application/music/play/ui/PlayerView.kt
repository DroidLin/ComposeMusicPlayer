package com.music.android.lin.application.music.play.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch
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
    val backHandlerEnable by remember {
        derivedStateOf {
            pagerState.currentPage != 1
        }
    }
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = backHandlerEnable) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(1)
        }
    }
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
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                PlayerHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = HeaderHorizontalPadding),
                    backPressed = backPressed,
                )
                HorizontalPager(
                    modifier = Modifier.weight(1f),
                    state = pagerState,
                ) { pageIndex ->
                    when (pageIndex) {
                        1 -> PlayerCoverContentView(
                            modifier = Modifier
                                .fillMaxSize(),
                            playerState = playerState,
                            playState = playState,
                            skipToPrevButtonPressed = skipToPrevButtonPressed,
                            playOrPauseButtonPressed = playOrPauseButtonPressed,
                            skipToNextButtonPressed = skipToNextButtonPressed,
                            seekToPosition = seekToPosition,
                            updateSliderProgress = updateSliderProgress
                        )

                        2 -> PlayLyricsView(
                            modifier = Modifier
                                .fillMaxSize(),
                            currentPosition = remember {
                                derivedStateOf {
                                    (playerState.value.progress * playerState.value.currentDuration).toLong()
                                }
                            },
                            lyricOutput = lyricOutput,
                            playState = playState
                        )

                        else -> Box(modifier = Modifier.fillMaxSize())
                    }
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

private val HeaderHorizontalPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerHeader(
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
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
