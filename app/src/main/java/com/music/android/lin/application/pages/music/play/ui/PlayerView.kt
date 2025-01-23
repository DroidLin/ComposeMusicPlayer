package com.music.android.lin.application.pages.music.play.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
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
import com.music.android.lin.application.common.ui.component.ViewModelStoreProvider
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.pages.music.play.ui.component.PlayerColorTheme
import com.music.android.lin.application.pages.music.play.ui.component.PlayerPageColumn
import com.music.android.lin.application.pages.music.play.ui.component.PlayerPageViewType
import com.music.android.lin.application.pages.music.play.ui.component.calculatePlayerPageViewType
import com.music.android.lin.application.pages.music.play.ui.state.PlayerColorScheme
import com.music.android.lin.application.pages.music.play.ui.state.isLightBgColor
import com.music.android.lin.application.pages.music.play.ui.vm.PlayerLyricViewModel
import com.music.android.lin.application.pages.music.play.ui.vm.PlayerPageViewModel
import com.music.android.lin.application.util.SystemBarStyleComponent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerView(
    show: Boolean,
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackHandler(
        enabled = show,
        onBack = backPressed
    )
    AnimatedVisibility(
        visible = show,
        enter = slideInVertically(tween(500)) { it },
        exit = slideOutVertically(tween(500)) { it },
        label = "player_view_enter_exit_animation",
        modifier = modifier,
    ) {
        ViewModelStoreProvider {
            PlayerView(
                backPressed = backPressed,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
internal fun PlayerView(
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
    playViewModel: PlayViewModel = koinViewModel(),
    playerPageViewModel: PlayerPageViewModel = koinViewModel(),
    lyricViewModel: PlayerLyricViewModel = koinViewModel(),
    playerPageViewType: PlayerPageViewType = calculatePlayerPageViewType(currentWindowAdaptiveInfo()),
) {
    val playState by playViewModel.playState.collectAsStateWithLifecycle()
    val playerState by playerPageViewModel.playerState.collectAsStateWithLifecycle()
    val lyricOutputState by lyricViewModel.lyricOutput.collectAsStateWithLifecycle()

    PlayerBackgroundScaffold(
        modifier = modifier,
        colorScheme = playerState.colorScheme,
        backgroundPainter = playerState.mediaBackgroundPainter,
    ) {
        if (playerPageViewType == PlayerPageViewType.Phone) {
            PlayerPortraitView(
                backPressed = backPressed,
                playerViewContent = {
                    PlayerCoverContentView(
                        modifier = Modifier
                            .fillMaxSize(),
                        playerState = playerState,
                        playState = playState,
                        skipToPrevButtonPressed = playViewModel::skipToPrev,
                        playOrPauseButtonPressed = playViewModel::playButtonPressed,
                        skipToNextButtonPressed = playViewModel::skipToNext,
                        seekToPosition = playViewModel::seekToPosition,
                        updateSliderProgress = playerPageViewModel::handleSliderInput,
                        switchPlayMode = playViewModel::changePlayMode,
                    )
                },
                lyricContent = {
                    PlayLyricsView(
                        modifier = Modifier
                            .fillMaxSize(),
                        currentPosition = (playerState.progress * playerState.currentDuration).toLong(),
                        lyricOutput = lyricOutputState,
                        playState = playState
                    )
                }
            )
        } else if (playerPageViewType == PlayerPageViewType.Tablet) {
            PlayerTabletView(
                playerViewContent = {
                    TabletPlayerCoverContentView(
                        modifier = Modifier
                            .fillMaxSize(),
                        playerState = playerState,
                        playState = playState,
                        skipToPrevButtonPressed = playViewModel::skipToPrev,
                        playOrPauseButtonPressed = playViewModel::playButtonPressed,
                        skipToNextButtonPressed = playViewModel::skipToNext,
                        seekToPosition = playViewModel::seekToPosition,
                        updateSliderProgress = playerPageViewModel::handleSliderInput,
                        switchPlayMode = playViewModel::changePlayMode,
                    )
                },
                lyricContent = {
                    TabletPlayerLyricView(
                        modifier = Modifier
                            .matchParentSize(),
                        currentPosition = (playerState.progress * playerState.currentDuration).toLong(),
                        lyricOutput = lyricOutputState,
                    )
                }
            )
        }
    }
}

@Composable
private fun PlayerBackgroundScaffold(
    colorScheme: PlayerColorScheme,
    backgroundPainter: Painter?,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    PlayerColorTheme(playerColorScheme = colorScheme) {
        Box(
            modifier = modifier
        ) {
            PlayerBackground(
                modifier = Modifier.matchParentSize(),
                playCover = backgroundPainter,
                backgroundColorState = colorScheme
            )
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                content = content,
            )
        }
    }
}

@Composable
fun PlayerPortraitView(
    backPressed: () -> Unit,
    playerViewContent: @Composable () -> Unit,
    lyricContent: @Composable () -> Unit,
    extraPaneContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val initPage = PlayerPageColumn.PlayerView
    val playerPageColumns = remember { PlayerPageColumn.entries.toList() }
    val pagerState = rememberPagerState(initPage.index) { playerPageColumns.size }
    val backHandlerEnable by remember {
        derivedStateOf {
            playerPageColumns[pagerState.currentPage] != initPage
        }
    }
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = backHandlerEnable && !pagerState.isScrollInProgress) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(
                page = 1,
                animationSpec = spring(stiffness = Spring.StiffnessLow)
            )
        }
    }
    Column(
        modifier = modifier
    ) {
        PlayerHeader(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HeaderHorizontalPadding),
            backPressed = backPressed,
        )
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = pagerState,
        ) { pageIndex ->
            val pageColumn = playerPageColumns[pageIndex]
            when (pageColumn) {
                PlayerPageColumn.PlayerView -> playerViewContent()
                PlayerPageColumn.LyricView -> lyricContent()
                PlayerPageColumn.ExtraView -> extraPaneContent?.invoke()
                else -> Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun PlayerTabletView(
    playerViewContent: @Composable BoxScope.() -> Unit,
    lyricContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            content = playerViewContent
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            content = lyricContent
        )
    }
}

private val backgroundFloatAnimation = spring<Float>(stiffness = Spring.StiffnessVeryLow)
private val backgroundColorAnimation = spring<Color>(stiffness = Spring.StiffnessVeryLow)

@Composable
private fun PlayerBackground(
    playCover: Painter?,
    backgroundColorState: PlayerColorScheme,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val backgroundMaskColor by animateColorAsState(
        targetValue = backgroundColorState.backgroundMaskColor ?: backgroundColor,
        label = "background_mask_animation",
        animationSpec = backgroundColorAnimation
    )
    val isLightColor by remember {
        derivedStateOf { backgroundMaskColor.isLightBgColor }
    }
    SystemBarStyleComponent(isLightMode = isLightColor)
    Box(
        modifier = modifier.drawWithContent {
            this.drawRect(color = backgroundMaskColor)
            this.drawContent()
        }
    )
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
