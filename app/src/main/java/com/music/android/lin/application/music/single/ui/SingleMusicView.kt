package com.music.android.lin.application.music.single.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.common.usecase.MusicItemSnapshot
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.music.component.CommonMediaItemView
import com.music.android.lin.application.music.single.ui.vm.FakeMusicItemData
import com.music.android.lin.application.music.single.ui.vm.SingleMusicViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

private const val UNIQUE_SINGLE_MUSIC_VIEW = "single_music_view"

@Composable
fun SingleMusicView(
    modifier: Modifier = Modifier,
    onDrawerIconPressed: () -> Unit = {},
) {
    val viewModel = koinViewModel<SingleMusicViewModel>()
    val playViewModel = koinViewModel<PlayViewModel>()
    val musicDataLoadState = viewModel.musicInfoList.collectAsStateWithLifecycle()

    ContentMusicView(
        state = musicDataLoadState,
        modifier = modifier,
        onDrawerIconPressed = onDrawerIconPressed,
        onMusicItemPress = { musicItemSnapshot, musicItem ->
            playViewModel.startResource(
                uniqueId = UNIQUE_SINGLE_MUSIC_VIEW,
                mediaInfoList = musicItemSnapshot.mediaInfoList,
                musicItem = musicItem
            )
        }
    )
}

@Composable
private fun ContentMusicView(
    state: State<DataLoadState>,
    modifier: Modifier = Modifier,
    onDrawerIconPressed: () -> Unit = {},
    onMusicItemPress: (MusicItemSnapshot, MusicItem) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier,
    ) {
        DataLoadingView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = state,
        ) { data: DataLoadState.Data<MusicItemSnapshot> ->
            Column(
                modifier = Modifier.matchParentSize(),
            ) {
                TopHeader(
                    modifier = Modifier,
                    onDrawerIconPressed = onDrawerIconPressed
                )
                CommonMediaItemView(
                    musicInfoList = remember(data) { data.data.musicItemList },
                    modifier = Modifier.weight(1f),
                    onMusicItemPress = { onMusicItemPress(data.data, it) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    modifier: Modifier = Modifier,
    onDrawerIconPressed: () -> Unit = {},
) {
    TopAppBarLayout(
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
                            musicItemList = FakeMusicItemData.toImmutableList(),
                            mediaInfoList = persistentListOf()
                        )
                    )
                }
            }
        )
    }
}