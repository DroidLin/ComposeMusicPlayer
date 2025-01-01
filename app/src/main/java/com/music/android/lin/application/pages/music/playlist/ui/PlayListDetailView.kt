package com.music.android.lin.application.pages.music.playlist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.music.android.lin.R
import com.music.android.lin.application.common.model.MediaInfoPlayListItem
import com.music.android.lin.application.common.ui.BackButton
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.ui.vm.MediaRepositoryViewModel
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.pages.minibar.ui.minibarHeightPadding
import com.music.android.lin.application.pages.music.component.AnchorToTargetActionButton
import com.music.android.lin.application.pages.music.component.CommonMediaItemView
import com.music.android.lin.application.pages.music.component.SelectMediaInfoBottomSheet
import com.music.android.lin.application.pages.music.playlist.ui.vm.PlayListDetailViewModel
import com.music.android.lin.application.util.fastScrollToItem
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Stable
@Serializable
data class PlayListDetail(
    val id: String,
)

fun NavController.navigateToPlayListDetail(playListId: String, navOptions: NavOptions? = null) {
    this.navigate(route = PlayListDetail(playListId), navOptions = navOptions)
}

fun NavGraphBuilder.playListDetail(
    backPressed: () -> Unit,
    editMediaInfo: (mediaInfoId: String) -> Unit,
) {
    composable<PlayListDetail> {
        PlayListDetailView(
            modifier = Modifier
                .fillMaxSize()
                .minibarHeightPadding()
                .navigationBarsPadding(),
            backPressed = backPressed,
            showBackButton = true,
            editMediaInfo = editMediaInfo
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PlayListDetailView(
    showBackButton: Boolean,
    backPressed: () -> Unit,
    editMediaInfo: (mediaInfoId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val playViewModel = koinViewModel<PlayViewModel>()
    val playState = playViewModel.playState.collectAsStateWithLifecycle()

    val playListDetailViewModel = koinViewModel<PlayListDetailViewModel>()
    val playListId = playListDetailViewModel.playListId
    val musicInfoState = playListDetailViewModel.playList.collectAsStateWithLifecycle()
    val showSelectMediaInfoBottomSheet = remember { mutableStateOf(false) }
    val mediaRepositoryViewModel = koinViewModel<MediaRepositoryViewModel>()
    val coroutineScope = rememberCoroutineScope()

    PlayListDetailContentView(
        backPressed = backPressed,
        musicInfoState = musicInfoState,
        modifier = modifier,
        startResource = { playViewModel.startResource(playListId, it) },
        addMoreMedia = { showSelectMediaInfoBottomSheet.value = true },
        playState = playState,
        showBackButton = showBackButton,
        editMediaInfo = editMediaInfo
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListDetailContentView(
    backPressed: () -> Unit,
    addMoreMedia: () -> Unit,
    showBackButton: Boolean,
    musicInfoState: State<DataLoadState>,
    playState: State<PlayState>,
    startResource: (MusicItem) -> Unit,
    editMediaInfo: (mediaInfoId: String) -> Unit,
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
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                TopHeader(
                    title = data.data.name,
                    modifier = Modifier.fillMaxWidth(),
                    backPressed = backPressed,
                    addMoreMedia = addMoreMedia,
                    showBackButton = showBackButton,
                    scrollBehavior = scrollBehavior,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                ) {
                    CommonMediaItemView(
                        musicInfoList = remember(data) { data.data.musicInfoList },
                        modifier = Modifier.matchParentSize(),
                        onMusicItemPress = { startResource(it) },
                        lazyListState = lazyListState,
                        editMediaInfo = editMediaInfo
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
    showBackButton: Boolean,
    backPressed: () -> Unit,
    addMoreMedia: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
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
            if (showBackButton) {
                BackButton(onClick = backPressed)
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
        },
        scrollBehavior = scrollBehavior,
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