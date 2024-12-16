package com.music.android.lin.application.music.single.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.common.usecase.MusicItemSnapshot
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.music.component.CommonMediaItemView
import com.music.android.lin.application.music.single.ui.vm.FakeMusicItemData
import com.music.android.lin.application.music.single.ui.vm.SingleMusicViewModel
import com.music.android.lin.application.util.fastScrollToItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val UNIQUE_SINGLE_MUSIC_VIEW = "single_music_view"

@Composable
fun SingleMusicView(
    modifier: Modifier = Modifier,
    onDrawerIconPressed: () -> Unit = {},
) {
    val viewModel = koinViewModel<SingleMusicViewModel>()
    val musicDataLoadState = viewModel.musicInfoList.collectAsStateWithLifecycle()

    val playViewModel = koinViewModel<PlayViewModel>()
    val playState = playViewModel.playState.collectAsStateWithLifecycle()


    ContentMusicView(
        state = musicDataLoadState,
        modifier = modifier,
        onDrawerIconPressed = onDrawerIconPressed,
        onMusicItemPress = { musicItemSnapshot, musicItem ->
            playViewModel.startResource(
                uniqueId = UNIQUE_SINGLE_MUSIC_VIEW,
                mediaInfoList = musicItemSnapshot.mediaInfoList,
                musicItem = musicItem
            )
        },
        playState = playState
    )
}

@Composable
private fun ContentMusicView(
    state: State<DataLoadState>,
    playState: State<PlayState>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onDrawerIconPressed: () -> Unit = {},
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
            val showAnchorButton = remember(data) {
                derivedStateOf {
                    data.data.musicItemList.find { it.mediaId == playState.value.musicItem?.mediaId } != null
                }
            }
            val anchorToTarget: () -> Unit =
                remember(coroutineScope, data, lazyListState, playState) {
                    {
                        coroutineScope.launch {
                            val indexOfCurrentMediaInfo =
                                data.data.musicItemList.indexOfFirst { musicInfo ->
                                    musicInfo.mediaId == playState.value.musicItem?.mediaId
                                }
                            if (indexOfCurrentMediaInfo in data.data.musicItemList.indices) {
                                lazyListState.fastScrollToItem(indexOfCurrentMediaInfo)
                            }
                        }
                    }
                }
            Column(
                modifier = Modifier.matchParentSize(),
            ) {
                TopHeader(
                    modifier = Modifier,
                    onDrawerIconPressed = onDrawerIconPressed
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    CommonMediaItemView(
                        lazyListState = lazyListState,
                        musicInfoList = remember(data) { data.data.musicItemList },
                        modifier = Modifier.matchParentSize(),
                        onMusicItemPress = { onMusicItemPress(data.data, it) }
                    )
                    AnchorToTargetActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        showAnchorButton = showAnchorButton,
                        anchorToTarget = anchorToTarget
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
    onDrawerIconPressed: () -> Unit = {},
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
            IconButton(onClick = onDrawerIconPressed) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(id = R.string.app_name),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Composable
fun AnchorToTargetActionButton(
    showAnchorButton: State<Boolean>,
    anchorToTarget: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (showAnchorButton.value) {
        FloatingActionButton(
            modifier = modifier,
            onClick = anchorToTarget
        ) {
            Icon(Icons.Default.MyLocation, "anchor to target media info.")
        }
    }
}


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