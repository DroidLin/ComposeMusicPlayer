package com.music.android.lin.application.music.playlist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.music.android.lin.R
import com.music.android.lin.application.common.model.PlayListItem
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.vm.MediaRepositoryViewModel
import com.music.android.lin.application.minibar.ui.minibarHeightPadding
import com.music.android.lin.application.music.component.CommonPlayListItemView
import com.music.android.lin.application.music.component.CreatePlayListBottomSheet
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Stable
@Serializable
data object PlayList

fun NavController.navigateToPlayList(navOptions: NavOptions? = null) {
    navigate(route = PlayList, navOptions = navOptions)
}

fun NavGraphBuilder.playListView(
    backPressed: () -> Unit,
    goToPlayListDetail: (playListId: String) -> Unit,
    showBackButton: Boolean = false,
) {
    composable<PlayList> {
        PlayListView(
            showBackButton = showBackButton,
            modifier = Modifier
                .fillMaxSize()
                .minibarHeightPadding()
                .navigationBarsPadding(),
            backPressed = backPressed,
            goToPlayListDetail = goToPlayListDetail,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListView(
    showBackButton: Boolean,
    backPressed: () -> Unit,
    goToPlayListDetail: (playListId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val mediaRepositoryViewModel = koinViewModel<MediaRepositoryViewModel>()
    val playList = mediaRepositoryViewModel.playList.collectAsStateWithLifecycle()
    val showCreatePlayListBottomSheet = remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier,
    ) {
        DataLoadingView(
            state = playList,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { data ->
            Column(
                modifier = Modifier.matchParentSize(),
            ) {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                TopHeader(
                    modifier = Modifier.fillMaxWidth(),
                    backPressed = backPressed,
                    showBackButton = showBackButton,
                    goToCreatePlayList = { showCreatePlayListBottomSheet.value = true },
                    scrollBehavior = scrollBehavior,
                )
                CommonPlayListItemView(
                    playList = data.data,
                    modifier = Modifier
                        .weight(1f)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    goToCreatePlayList = { showCreatePlayListBottomSheet.value = true },
                    onPlayListPressed = { goToPlayListDetail(it.id) }
                )
            }
        }
    }
    CreatePlayListBottomSheet(
        modifier = Modifier,
        sheetState = sheetState,
        showBottomSheet = showCreatePlayListBottomSheet,
        dismissRequest = { showCreatePlayListBottomSheet.value = false },
        doCreatePlayList = { createPlayListParameter, onComplete ->
            coroutineScope.launch {
                mediaRepositoryViewModel.createPlayList(createPlayListParameter)
                onComplete()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    backPressed: () -> Unit,
    showBackButton: Boolean,
    goToCreatePlayList: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    TopAppBarLayout(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.string_playlist_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        navigationIcon = {
            if (showBackButton) {
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
        },
        actions = {
            IconButton(
                onClick = goToCreatePlayList
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}