package com.music.android.lin.application.pages.music.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.common.model.PlayListItem
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.vm.MediaRepositoryViewModel
import com.music.android.lin.application.common.usecase.MusicItem
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToPlayListBottomSheet(
    sheetState: SheetState,
    musicItemState: State<MusicItem?>,
    dismissRequest: () -> Unit,
    doAddToPlayList: (PlayListItem, MusicItem, onComplete: () -> Unit) -> Unit,
    openCreatePlayListBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val musicItem = musicItemState.value ?: return
    val mediaRepositoryViewModel = koinViewModel<MediaRepositoryViewModel>()
    val playListState = mediaRepositoryViewModel.playList.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = dismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        dragHandle = null,
        properties = ModalBottomSheetProperties(),
    ) {
        DataLoadingView(
            state = playListState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
        ) { data ->
            Column(
                modifier = Modifier.matchParentSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.string_more_options_add_to_playlist),
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        modifier = Modifier,
                        onClick = openCreatePlayListBottomSheet
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    }
                }
                CommonPlayListItemView(
                    modifier = Modifier.weight(1f),
                    playList = data.data,
                    onPlayListPressed = { playListItem ->
                        doAddToPlayList(playListItem, musicItem) {
                            coroutineScope.launch {
                                sheetState.hide()
                                dismissRequest()
                            }
                        }
                    },
                    goToCreatePlayList = openCreatePlayListBottomSheet
                )
            }
        }
    }
}
