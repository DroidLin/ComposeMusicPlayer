package com.music.android.lin.application.music.single.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.common.usecase.MusicItemSnapshot
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.minibar.ui.minibarHeightPadding
import com.music.android.lin.application.music.component.AnchorToTargetActionButton
import com.music.android.lin.application.music.component.CommonMediaItemView
import com.music.android.lin.application.music.single.ui.vm.FakeMusicItemData
import com.music.android.lin.application.music.single.ui.vm.SingleMusicViewModel
import com.music.android.lin.application.util.fastScrollToItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

private const val UNIQUE_SINGLE_MUSIC_VIEW = "single_music_view"

@Stable
@Serializable
data object SimpleMusicView

fun NavController.navigateToSimpleMusicView(navOptions: NavOptions? = null) {
    this.navigate(
        route = SimpleMusicView,
        navOptions = navOptions ?: navOptions {
            restoreState = true
            launchSingleTop = true
        }
    )
}

fun NavGraphBuilder.simpleMusicScreen(
    backPressed: () -> Unit,
    showBackButton: Boolean = false
) {
    composable<SimpleMusicView> {
        SingleMusicView(
            modifier = Modifier
                .fillMaxSize()
                .minibarHeightPadding()
                .navigationBarsPadding(),
            showBackButton = showBackButton,
            backPressed = backPressed,
        )
    }
}

@Composable
fun SingleMusicView(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    backPressed: () -> Unit = {},
) {
    val viewModel = koinViewModel<SingleMusicViewModel>()
    val musicDataLoadState = viewModel.musicInfoList.collectAsStateWithLifecycle()

    val playViewModel = koinViewModel<PlayViewModel>()
    val playState = playViewModel.playState.collectAsStateWithLifecycle()

    ContentMusicView(
        state = musicDataLoadState,
        modifier = modifier,
        onMusicItemPress = { musicItemSnapshot, musicItem ->
            playViewModel.startResource(
                uniqueId = UNIQUE_SINGLE_MUSIC_VIEW,
                mediaInfoList = musicItemSnapshot.mediaInfoList,
                musicItem = musicItem
            )
        },
        playState = playState,
        showBackButton = showBackButton,
        backPressed = backPressed,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentMusicView(
    state: State<DataLoadState>,
    playState: State<PlayState>,
    showBackButton: Boolean = false,
    modifier: Modifier = Modifier,
    backPressed: () -> Unit = {},
    lazyListState: LazyListState = rememberLazyListState(),
    onMusicItemPress: (MusicItemSnapshot, MusicItem) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier,
    ) {
        DataLoadingView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = state,
        ) { data: DataLoadState.Data<MusicItemSnapshot> ->
            val coroutineScope = rememberCoroutineScope()
            Column(
                modifier = Modifier.matchParentSize(),
            ) {
                val behavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                TopHeader(
                    modifier = Modifier,
                    showBackButton = showBackButton,
                    backPressed = backPressed,
                    scrollBehavior = behavior
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    CommonMediaItemView(
                        modifier = Modifier
                            .matchParentSize()
                            .nestedScroll(behavior.nestedScrollConnection),
                        lazyListState = lazyListState,
                        musicInfoList = remember(data) { data.data.musicItemList },
                        onMusicItemPress = { onMusicItemPress(data.data, it) }
                    )
                    AnchorToTargetActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        showAnchorButton = remember(data) {
                            derivedStateOf {
                                data.data.musicItemList.find { it.mediaId == playState.value.musicItem?.mediaId } != null
                            }
                        },
                        anchorToTarget = {
                            coroutineScope.launch {
                                val indexOfCurrentMediaInfo =
                                    data.data.musicItemList.indexOfFirst { musicInfo ->
                                        musicInfo.mediaId == playState.value.musicItem?.mediaId
                                    }
                                if (indexOfCurrentMediaInfo in data.data.musicItemList.indices) {
                                    lazyListState.fastScrollToItem(indexOfCurrentMediaInfo)
                                }
                            }
                        },
                        shouldHideContent = remember { derivedStateOf { lazyListState.isScrollInProgress } }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = false,
    backPressed: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBarLayout(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.string_single_music_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = backPressed
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = null
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun SingleMusicTopHeaderPreview() {
    AppMaterialTheme {
        TopHeader()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun ContentViewPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        ContentMusicView(
            modifier = Modifier.fillMaxSize(),
            state = remember {
                derivedStateOf {
                    DataLoadState.Data(
                        MusicItemSnapshot(
                            musicItemList = FakeMusicItemData.toImmutableList(),
                            mediaInfoList = persistentListOf()
                        )
                    )
                }
            },
            playState = remember {
                derivedStateOf {
                    PlayState()
                }
            }
        )
    }
}