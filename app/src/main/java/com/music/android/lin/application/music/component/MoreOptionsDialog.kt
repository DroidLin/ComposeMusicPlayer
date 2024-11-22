package com.music.android.lin.application.music.component

import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.music.android.lin.R
import com.music.android.lin.application.usecase.MusicItem

enum class OperationType(@StringRes val nameRes: Int) {
    AddToPlayList(nameRes = R.string.string_more_options_add_to_playlist),
    Delete(nameRes = R.string.string_more_options_delete_media),
    Information(nameRes = R.string.string_more_options_information),
    Edit(nameRes = R.string.string_more_options_edit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsDialog(
    show: Boolean,
    mediaInfo: State<MusicItem?>,
    deleteOperation: () -> Unit,
    dismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaInfoItem = mediaInfo.value
    if (!show && mediaInfoItem == null) return
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = dismissRequest,
        sheetState = sheetState,
    ) {
        val listState = rememberLazyListState()
        val operationList = remember {
            listOf(
                OperationType.AddToPlayList,
                OperationType.Edit,
                OperationType.Delete,
                OperationType.Information
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
        ) {
            val musicItem = mediaInfoItem
            if (musicItem != null) {
                item(
                    key = "more_options_header",
                    contentType = "more_options_header"
                ) {
                    MusicItemView(
                        musicItem = musicItem,
                        onClick = {}
                    )
                }
            }
            itemsIndexed(
                items = operationList,
                key = { index, item  -> "operation_item_${it.name}" },
                contentType = { index, item -> "operation_list_item" }
            ) { index, item ->

            }
        }
    }
}