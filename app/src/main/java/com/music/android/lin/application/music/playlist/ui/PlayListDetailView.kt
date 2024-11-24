package com.music.android.lin.application.music.playlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.application.common.model.MediaInfoPlayListItem
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.music.component.CommonMediaItemView
import com.music.android.lin.application.music.playlist.ui.vm.PlayListDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

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
                    backPressed = backPressed
                )
                CommonMediaItemView(
                    musicInfoList = remember(data) { data.data.musicInfoList },
                    modifier = Modifier.weight(1f),
                    onMusicItemPress = { playViewModel.startResource(playListId, it) }
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
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
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
        }
    )
}