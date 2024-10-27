package com.music.android.lin.application.ui.composables.music

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.model.DataLoadState
import com.music.android.lin.application.ui.composables.framework.AppMaterialTheme
import com.music.android.lin.application.ui.composables.music.component.DataLoadView
import com.music.android.lin.application.ui.composables.music.component.MusicItemView
import com.music.android.lin.application.ui.composables.music.vm.SingleMusicViewModel
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

    Column(
        modifier = modifier,
    ) {
        TopHeader(
            modifier = Modifier,
            onDrawerIconPressed = onDrawerIconPressed
        )
        DataLoadView(
            state = musicDataLoadState,
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
                    onMusicItemPress = viewModel::onMusicItemPressed
                )
            }
        )
    }
}

@Composable
fun MusicLazyColumn(
    data: DataLoadState.Data<MusicItemSnapshot>,
    modifier: Modifier = Modifier,
    onMusicItemPress: (MusicItemSnapshot, MusicItem) -> Unit,
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
                }
            )
        }
    }
}

@Composable
private fun LoadingProgress(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(0.5f),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(id = R.string.string_loading_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                style = MaterialTheme.typography.titleMedium,
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
private fun LoadingProgressPreview() {
    AppMaterialTheme {
        LoadingProgress()
    }
}
