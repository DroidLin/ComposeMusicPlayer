package com.music.android.lin.application.pages.music.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.music.android.lin.application.common.usecase.MusicItem
import kotlinx.collections.immutable.ImmutableList


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonMediaItemView(
    musicInfoList: ImmutableList<MusicItem>,
    modifier: Modifier = Modifier,
    editMediaInfo: (mediaInfoId: String) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    onMusicItemPress: (MusicItem) -> Unit = {},
) {
    val sheetState = rememberMediaBottomSheetState(
        moreSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        addToPlayListSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        createPlayListSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    )
    MediaBottomSheetScaffold(
        sheetState = sheetState,
        editMediaInfo = editMediaInfo,
    ) {
        MusicLazyColumn(
            musicInfoList = musicInfoList,
            modifier = modifier,
            lazyListState = lazyListState,
            onMusicItemPress = onMusicItemPress,
            onMusicItemLongPress = sheetState::showMoreOptionsBottomSheet
        )
    }
}

@Composable
private fun MusicLazyColumn(
    musicInfoList: ImmutableList<MusicItem>,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    onMusicItemPress: (MusicItem) -> Unit,
    onMusicItemLongPress: ((MusicItem) -> Unit)? = null,
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        items(
            items = musicInfoList,
            contentType = { "music_item" },
            key = { it.mediaId }
        ) { musicItem ->
            MusicItemView(
                modifier = Modifier
                    .fillParentMaxWidth(),
                musicItem = musicItem,
                onClick = {
                    onMusicItemPress(musicItem)
                },
                onLongPress = {
                    if (onMusicItemLongPress != null) {
                        onMusicItemLongPress(musicItem)
                    }
                }
            )
        }
    }
}
