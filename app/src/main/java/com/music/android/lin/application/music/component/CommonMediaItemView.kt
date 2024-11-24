package com.music.android.lin.application.music.component

import androidx.compose.foundation.lazy.LazyColumn
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
    onMusicItemPress: (MusicItem) -> Unit = {},
) {
    val sheetState = rememberMediaBottomSheetState(
        moreSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        addToPlayListSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        createPlayListSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    )
    MediaBottomSheetScaffold(sheetState = sheetState) {
        MusicLazyColumn(
            musicInfoList = musicInfoList,
            modifier = modifier,
            onMusicItemPress = onMusicItemPress,
            onMusicItemLongPress = sheetState::showMoreOptionsBottomSheet
        )
    }
}

@Composable
private fun MusicLazyColumn(
    musicInfoList: ImmutableList<MusicItem>,
    modifier: Modifier = Modifier,
    onMusicItemPress: (MusicItem) -> Unit,
    onMusicItemLongPress: ((MusicItem) -> Unit)? = null,
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        items(
            items = musicInfoList,
            contentType = { it::class.qualifiedName },
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
