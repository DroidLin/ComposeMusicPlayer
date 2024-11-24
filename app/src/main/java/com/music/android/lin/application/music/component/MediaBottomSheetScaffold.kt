package com.music.android.lin.application.music.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.music.android.lin.application.common.ui.vm.MediaRepositoryViewModel
import com.music.android.lin.application.common.usecase.MusicItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Stable
class MediaBottomSheetState(
    internal val musicMoreOptionsSheetState: SheetState,
    internal val addToPlayListSheetState: SheetState,
    internal val createPlayListSheetState: SheetState,
) {
    internal val moreOptionsMusicItemState = mutableStateOf<MusicItem?>(null)
    internal val addToPlayListMusicItemState = mutableStateOf<MusicItem?>(null)
    internal val showCreatePlayListBottomSheet = mutableStateOf(false)

    fun showMoreOptionsBottomSheet(musicItem: MusicItem) {
        this.moreOptionsMusicItemState.value = musicItem
    }

    fun dismissMoreOptionsBottomSheet() {
        this.moreOptionsMusicItemState.value = null
    }

    fun showAddToPlayListBottomSheet() {
        val musicItem = this.moreOptionsMusicItemState.value ?: return
        this.addToPlayListMusicItemState.value = musicItem
    }

    fun dismissAddToPlayListBottomSheet() {
        this.addToPlayListMusicItemState.value = null
    }

    fun showCreatePlayListBottomSheet() {
        this.showCreatePlayListBottomSheet.value = true
    }

    fun dismissCreatePlayListBottomSheet() {
        this.showCreatePlayListBottomSheet.value = false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMediaBottomSheetState(
    moreSheetState: SheetState = rememberModalBottomSheetState(),
    addToPlayListSheetState: SheetState = rememberModalBottomSheetState(),
    createPlayListSheetState: SheetState = rememberModalBottomSheetState(),
): MediaBottomSheetState {
    return remember(moreSheetState, addToPlayListSheetState, createPlayListSheetState) {
        MediaBottomSheetState(moreSheetState, addToPlayListSheetState, createPlayListSheetState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaBottomSheetScaffold(
    sheetState: MediaBottomSheetState,
    content: @Composable () -> Unit,
) {
    content()
    val coroutineScope = rememberCoroutineScope()
    val mediaRepositoryViewModel = koinViewModel<MediaRepositoryViewModel>()
    MoreOptionsBottomSheet(
        sheetState = sheetState.musicMoreOptionsSheetState,
        musicItemState = sheetState.moreOptionsMusicItemState,
        addToPlaylist = sheetState::showAddToPlayListBottomSheet,
        deleteOperation = { onComplete ->
            val item = sheetState.moreOptionsMusicItemState.value ?: return@MoreOptionsBottomSheet
            coroutineScope.launch {
                mediaRepositoryViewModel.deleteMediaInfo(item.mediaId)
            }.invokeOnCompletion { onComplete() }
        },
        dismissRequest = sheetState::dismissMoreOptionsBottomSheet,
        modifier = Modifier
    )
    AddToPlayListBottomSheet(
        sheetState = sheetState.addToPlayListSheetState,
        musicItemState = sheetState.addToPlayListMusicItemState,
        modifier = Modifier,
        dismissRequest = sheetState::dismissAddToPlayListBottomSheet,
        openCreatePlayListBottomSheet = sheetState::showCreatePlayListBottomSheet,
        doAddToPlayList = { playListItem, musicItem, onComplete ->
            coroutineScope.launch {
                mediaRepositoryViewModel.doAddToPlayList(playListItem, musicItem)
                onComplete()
            }
        }
    )
    CreatePlayListBottomSheet(
        sheetState = sheetState.createPlayListSheetState,
        showBottomSheet = sheetState.showCreatePlayListBottomSheet,
        dismissRequest = sheetState::dismissCreatePlayListBottomSheet,
        modifier = Modifier,
        doCreatePlayList = { createPlayListParameter, onComplete ->
            coroutineScope.launch {
                mediaRepositoryViewModel.createPlayList(createPlayListParameter)
                onComplete()
            }
        }
    )
}