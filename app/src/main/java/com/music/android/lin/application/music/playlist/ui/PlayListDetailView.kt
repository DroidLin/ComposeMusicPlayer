package com.music.android.lin.application.music.playlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.common.model.MediaInfoPlayListItem
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.ui.vm.MediaRepositoryViewModel
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.music.component.AnchorToTargetActionButton
import com.music.android.lin.application.music.component.CommonMediaItemView
import com.music.android.lin.application.music.component.SelectMediaInfoBottomSheet
import com.music.android.lin.application.music.playlist.ui.vm.PlayListDetailViewModel
import com.music.android.lin.application.util.fastScrollToItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListDetailView(
    playListId: String,
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playViewModel = koinViewModel<PlayViewModel>()
    val playState = playViewModel.playState.collectAsStateWithLifecycle()

    val playListDetailViewModel = koinViewModel<PlayListDetailViewModel>() {
        parametersOf(playListId)
    }
    val musicInfoState = playListDetailViewModel.playList.collectAsStateWithLifecycle()
    val showSelectMediaInfoBottomSheet = remember { mutableStateOf(false) }
    val mediaRepositoryViewModel = koinViewModel<MediaRepositoryViewModel>()
    val coroutineScope = rememberCoroutineScope()

    PlayListDetailContentView(
        backPressed = backPressed,
        musicInfoState = musicInfoState,
        startResource = { playViewModel.startResource(playListId, it) },
        modifier = modifier,
        addMoreMedia = { showSelectMediaInfoBottomSheet.value = true },
        playState = playState,
    )
    SelectMediaInfoBottomSheet(
        show = showSelectMediaInfoBottomSheet,
        completePressed = { idList, onComplete ->
            coroutineScope.launch {
                mediaRepositoryViewModel.doAddToPlayList(playListId = playListId, mediaInfoIdList = idList)
                onComplete()
            }
        },
        dismissRequest = { showSelectMediaInfoBottomSheet.value = false }
    )
}

@Composable
fun PlayListDetailContentView(
    backPressed: () -> Unit,
    addMoreMedia: () -> Unit,
    musicInfoState: State<DataLoadState>,
    playState: State<PlayState>,
    startResource: (MusicItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        DataLoadingView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = musicInfoState,
        ) { data: DataLoadState.Data<MediaInfoPlayListItem> ->
            val coroutineScope = rememberCoroutineScope()
            val lazyListState = rememberLazyListState()
            Column(
                modifier = Modifier.matchParentSize(),
            ) {
                TopHeader(
                    title = data.data.name,
                    modifier = Modifier.fillMaxWidth(),
                    backPressed = backPressed,
                    addMoreMedia = addMoreMedia
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    CommonMediaItemView(
                        musicInfoList = remember(data) { data.data.musicInfoList },
                        modifier = Modifier.matchParentSize(),
                        onMusicItemPress = { startResource(it) },
                        lazyListState = lazyListState,
                    )
                    AnchorToTargetActionButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        showAnchorButton = remember(data) {
                            derivedStateOf {
                                data.data.musicInfoList.find { it.mediaId == playState.value.musicItem?.mediaId } != null
                            }
                        },
                        anchorToTarget = {
                            coroutineScope.launch {
                                val indexOfCurrentMediaInfo =
                                    data.data.musicInfoList.indexOfFirst { musicInfo ->
                                        musicInfo.mediaId == playState.value.musicItem?.mediaId
                                    }
                                if (indexOfCurrentMediaInfo in data.data.musicInfoList.indices) {
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
    title: String,
    backPressed: () -> Unit,
    addMoreMedia: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBarLayout(
        modifier = modifier,
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        navigationIcon = {
            IconButton(
                onClick = backPressed
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        actions = {
            val showPopup = rememberSaveable(
                stateSaver = Saver(
                    save = { it },
                    restore = { it }
                ),
            ) { mutableStateOf(false) }
            IconButton(
                onClick = { showPopup.value = true }
            ) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            PlayListDetailMoreOptionPopup(
                showPopup = showPopup,
                onDismissRequest = { showPopup.value = false },
                addMoreMedia = addMoreMedia
            )
        }
    )
}

@Composable
fun PlayListDetailMoreOptionPopup(
    showPopup: State<Boolean>,
    onDismissRequest: () -> Unit,
    addMoreMedia: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        modifier = modifier,
        expanded = showPopup.value,
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium,
    ) {
        DropdownMenuItem(
            text = {
                Text(text = stringResource(R.string.string_add_more_media))
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
            },
            onClick = { onDismissRequest(); addMoreMedia() },
            modifier = Modifier,
        )
    }
}