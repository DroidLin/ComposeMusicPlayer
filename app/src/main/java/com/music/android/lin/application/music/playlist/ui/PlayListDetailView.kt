package com.music.android.lin.application.music.playlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.common.model.MediaInfoPlayListItem
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.vm.MediaRepositoryViewModel
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.music.component.CommonMediaItemView
import com.music.android.lin.application.music.component.SelectMediaInfoBottomSheet
import com.music.android.lin.application.music.playlist.ui.vm.PlayListDetailViewModel
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
        addMoreMedia = { showSelectMediaInfoBottomSheet.value = true }
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
            Column(
                modifier = Modifier.matchParentSize(),
            ) {
                TopHeader(
                    title = data.data.name,
                    modifier = Modifier.fillMaxWidth(),
                    backPressed = backPressed,
                    addMoreMedia = addMoreMedia
                )
                CommonMediaItemView(
                    musicInfoList = remember(data) { data.data.musicInfoList },
                    modifier = Modifier.weight(1f),
                    onMusicItemPress = { startResource(it) }
                )
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
        containerColor = MaterialTheme.colorScheme.surface
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