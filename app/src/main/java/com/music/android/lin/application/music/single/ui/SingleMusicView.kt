package com.music.android.lin.application.music.single.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.common.state.DataLoadState
import com.music.android.lin.application.common.ui.LoadingProgress
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.common.ui.component.DataLoadView
import com.music.android.lin.application.music.component.MoreOptionsDialog
import com.music.android.lin.application.music.component.MusicItemView
import com.music.android.lin.application.music.single.ui.vm.FakeMusicItemData
import com.music.android.lin.application.music.single.ui.vm.SingleMusicViewModel
import com.music.android.lin.application.usecase.MusicItem
import com.music.android.lin.application.usecase.MusicItemSnapshot
import org.koin.androidx.compose.koinViewModel

/**
 * @author: liuzhongao
 * @since: 2024/10/26 12:28
 */
@Composable
fun SingleMusicView(
    modifier: Modifier = Modifier,
    onDrawerIconPressed: () -> Unit = {},
) {
    val viewModel = koinViewModel<SingleMusicViewModel>()
    val musicDataLoadState = viewModel.musicInfoList.collectAsStateWithLifecycle()

    ContentMusicView(
        state = musicDataLoadState,
        modifier = modifier,
        onDrawerIconPressed = onDrawerIconPressed,
        onMusicItemPress = viewModel::onMusicItemPressed
    )
}

@Composable
private fun ContentMusicView(
    state: State<DataLoadState>,
    modifier: Modifier = Modifier,
    onDrawerIconPressed: () -> Unit = {},
    onMusicItemPress: (MusicItemSnapshot, MusicItem) -> Unit = { _, _ -> },
) {
    val deleteMusicItemState = remember {
        mutableStateOf<MusicItem?>(null)
    }
    Column(
        modifier = modifier,
    ) {
        TopHeader(
            modifier = Modifier,
            onDrawerIconPressed = onDrawerIconPressed
        )
        DataLoadView(
            state = state,
            loading = {
                LoadingProgress(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            },
            data = { data ->
                MusicLazyColumn(
                    data = data,
                    modifier = Modifier.weight(1f),
                    onMusicItemPress = onMusicItemPress,
                    onMusicItemLongPress = { snapshot, musicItem ->
                        deleteMusicItemState.value = musicItem
                    }
                )
            }
        )
    }
    MoreOptionsDialog(
        mediaInfo = deleteMusicItemState,
        deleteOperation = {
            val item = deleteMusicItemState.value ?: return@MoreOptionsDialog true
            true
        },
        dismissRequest = {
            deleteMusicItemState.value = null
        },
        modifier = Modifier,
    )
}

@Composable
private fun MusicLazyColumn(
    data: DataLoadState.Data<MusicItemSnapshot>,
    modifier: Modifier = Modifier,
    onMusicItemPress: (MusicItemSnapshot, MusicItem) -> Unit,
    onMusicItemLongPress: ((MusicItemSnapshot, MusicItem) -> Unit)? = null
) {
    val musicInfoList = data.data.musicItemList
    val lazyListState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(vertical = 16.dp)
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
                    onMusicItemPress(data.data, musicItem)
                },
                onLongPress = {
                    if (onMusicItemLongPress != null) {
                        onMusicItemLongPress(data.data, musicItem)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    modifier: Modifier = Modifier,
    onDrawerIconPressed: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.string_single_music_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        navigationIcon = {
            IconButton(onClick = onDrawerIconPressed) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = stringResource(id = R.string.app_name),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun SingleMusicTopHeaderPreview() {
    AppMaterialTheme {
        TopHeader()
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun ContentViewPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        ContentMusicView(
            modifier = Modifier.fillMaxSize(),
            state = remember {
                derivedStateOf {
                    DataLoadState.Data(
                        MusicItemSnapshot(
                            musicItemList = FakeMusicItemData,
                            mediaInfoList = emptyList()
                        )
                    )
                }
            }
        )
    }
}