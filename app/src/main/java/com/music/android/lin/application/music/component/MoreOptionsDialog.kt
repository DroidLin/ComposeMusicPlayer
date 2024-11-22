package com.music.android.lin.application.music.component

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.android.lin.R
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.usecase.MediaQuality
import com.music.android.lin.application.usecase.MusicItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

private const val TYPE_HEADER = "more_options_header"
private const val TYPE_SPACER_BETWEEN_HEADER_AND_OPERATION = "spacer_between_header_and_options"
private const val TYPE_OPERATION_LIST_ITEM = "operation_list_item"

enum class OperationType(@StringRes val nameRes: Int) {
    AddToPlayList(nameRes = R.string.string_more_options_add_to_playlist),
    Delete(nameRes = R.string.string_more_options_delete_media),
    Information(nameRes = R.string.string_more_options_information),
    Edit(nameRes = R.string.string_more_options_edit),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsDialog(
    mediaInfo: State<MusicItem?>,
    deleteOperation: suspend () -> Boolean,
    dismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaInfoItem = mediaInfo.value ?: return
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val optionItemPressed: (OperationType) -> Unit = { type ->
        coroutineScope.launch {
            when (type) {
                OperationType.Delete -> {
                    if (deleteOperation()) {
                        sheetState.hide()
                        dismissRequest()
                    }
                }

                else -> {}
            }
        }
    }
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
            ).toImmutableList()
        }
        OptionsList(
            mediaInfoItem = mediaInfoItem,
            modifier = Modifier.fillMaxHeight(0.6f),
            listState = listState,
            operationList = operationList,
            onOperationPress = optionItemPressed
        )
    }
}

@Composable
private fun OptionsList(
    operationList: ImmutableList<OperationType>,
    mediaInfoItem: MusicItem?,
    onOperationPress: (OperationType) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val topCornerShape = MaterialTheme.shapes.medium.let { shape ->
        remember(shape) {
            shape.copy(bottomStart = CornerSize(0.0.dp), bottomEnd = CornerSize(0.0.dp))
        }
    }
    val bottomCornerShape = MaterialTheme.shapes.medium.let { shape ->
        remember(shape) {
            shape.copy(topStart = CornerSize(0.0.dp), topEnd = CornerSize(0.0.dp))
        }
    }
    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (mediaInfoItem != null) {
            item(
                key = "more_options_header",
                contentType = TYPE_HEADER
            ) {
                MusicItemView(
                    musicItem = mediaInfoItem,
                    onClick = {},
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
            item(
                key = "spacer_between_header_and_options",
                contentType = TYPE_SPACER_BETWEEN_HEADER_AND_OPERATION
            ) {
                Spacer(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .height(16.dp)
                )
            }
        }
        itemsIndexed(
            items = operationList,
            key = { _, item -> "operation_item_${item.name}" },
            contentType = { _, _ -> TYPE_OPERATION_LIST_ITEM }
        ) { index, item ->
            Surface(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .height(48.dp),
                shape = when (index) {
                    0 -> topCornerShape
                    (operationList.size - 1) -> bottomCornerShape
                    else -> RectangleShape
                },
                onClick = { onOperationPress(item) },
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = stringResource(item.nameRes),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
private fun OperationListPreview() {
    AppMaterialTheme {
        val listState = rememberLazyListState()
        val operationList = remember {
            listOf(
                OperationType.AddToPlayList,
                OperationType.Edit,
                OperationType.Delete,
                OperationType.Information
            ).toImmutableList()
        }
        OptionsList(
            mediaInfoItem = MusicItem(
                mediaId = "",
                musicName = "Hello World.",
                musicDescription = "Hello World.",
                musicCover = "",
                mediaQuality = MediaQuality.HQ
            ),
            listState = listState,
            operationList = operationList,
            onOperationPress = {}
        )
    }
}