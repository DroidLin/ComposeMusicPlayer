package com.music.android.lin.application.pages.music.play.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import kotlinx.coroutines.launch

internal enum class PlayerPageViewType {
    Phone,
    Tablet;
}

internal enum class PlayerPageColumn(val index: Int) {
    ExtraView(0),
    PlayerView(1),
    LyricView(2),
}

internal fun calculatePlayerPageViewType(windowAdaptiveInfo: WindowAdaptiveInfo): PlayerPageViewType {
    with(windowAdaptiveInfo) {
        if (windowPosture.isTabletop) {
            return PlayerPageViewType.Phone
        }
        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
            return PlayerPageViewType.Phone
        }
        return PlayerPageViewType.Tablet
    }
}

@Composable
internal fun PlayerViewScaffold(
    playerPageViewType: PlayerPageViewType,
    modifier: Modifier = Modifier,
    playerViewContent: @Composable () -> Unit,
    lyricContent: @Composable () -> Unit,
    extraPaneContent: (@Composable () -> Unit)? = null
) {
    if (playerPageViewType == PlayerPageViewType.Phone) {
        val initPage = PlayerPageColumn.PlayerView
        val playerPageColumns = remember { PlayerPageColumn.entries.toList() }
        val pagerState = rememberPagerState(initPage.index) { playerPageColumns.size }
        val backHandlerEnable by remember {
            derivedStateOf {
                playerPageColumns[pagerState.currentPage] == initPage
            }
        }
        val coroutineScope = rememberCoroutineScope()
        BackHandler(enabled = backHandlerEnable && !pagerState.isScrollInProgress) {
            coroutineScope.launch {
                pagerState.animateScrollToPage(1)
            }
        }
        HorizontalPager(
            modifier = modifier,
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
    } else if (playerPageViewType == PlayerPageViewType.Tablet) {

    }
}