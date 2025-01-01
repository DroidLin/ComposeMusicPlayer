package com.music.android.lin.application.pages.music.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.BackButton
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.ViewModelStoreProvider
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.pages.music.component.vm.SelectMediaInfoViewModel
import com.music.android.lin.application.pages.music.component.vm.SelectableMusicItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectMediaInfoBottomSheet(
    show: State<Boolean>,
    completePressed: (mediaInfoIdList: List<String>, onComplete: () -> Unit) -> Unit,
    dismissRequest: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    modifier: Modifier = Modifier,
) {
    if (!show.value) return

    ViewModelStoreProvider {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = dismissRequest,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = null,
            contentWindowInsets = { BottomSheetDefaults.windowInsets.add(WindowInsets.statusBars) }
        ) {
            val viewModel = koinViewModel<SelectMediaInfoViewModel>()
            val uiState = viewModel.selectableMusicItems.collectAsStateWithLifecycle()
            val searchInputState = viewModel.searchInput.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier.fillMaxHeight(),
            ) {
                val inSearchMode = remember { mutableStateOf(false) }
                val launchLoading = remember { mutableStateOf(false) }
                val completeButtonEnabled = remember {
                    derivedStateOf {
                        val data =
                            (uiState.value as? DataLoadState.Data<*>)?.data as? ImmutableList<SelectableMusicItem>
                        data != null && data.any { it.isSelected }
                    }
                }
                SelectHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    inSearchMode = inSearchMode,
                    completePressed = {
                        launchLoading.value = true
                        coroutineScope.launch {
                            val idList = viewModel.peekSelectedIdList()
                            completePressed(idList) {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    dismissRequest()
                                    launchLoading.value = false
                                }
                            }
                        }
                    },
                    searchInputState = searchInputState,
                    onSearchValueChanged = viewModel::onSearchInputValueChange,
                    dismissSearchMode = { inSearchMode.value = false },
                    enterSearchModePressed = { inSearchMode.value = true },
                    launchLoading = launchLoading,
                    completeButtonEnabled = completeButtonEnabled,
                    closeBottomSheet = {
                        coroutineScope.launch {
                            sheetState.hide()
                            dismissRequest()
                        }
                    }
                )
                DataLoadingView(
                    modifier = Modifier.weight(1f),
                    state = uiState
                ) { data ->
                    SelectList(
                        modifier = Modifier
                            .fillMaxSize(),
                        selectableItemList = data.data,
                        onMusicItemClick = viewModel::onMusicItemPressed,
                    )
                }
            }
        }
    }
}

@Composable
fun SelectHeader(
    inSearchMode: State<Boolean>,
    searchInputState: State<String>,
    launchLoading: State<Boolean>,
    completeButtonEnabled: State<Boolean>,
    closeBottomSheet: () -> Unit,
    onSearchValueChanged: (String) -> Unit,
    dismissSearchMode: () -> Unit,
    completePressed: () -> Unit,
    enterSearchModePressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = inSearchMode.value && !launchLoading.value,
        label = "select_header_animation",
        modifier = modifier,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
    ) { isInSearchMode ->
        if (isInSearchMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(focusRequester) {
                    focusRequester.requestFocus()
                }
                BackButton(onClick = dismissSearchMode)
                BasicTextField(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .focusRequester(focusRequester),
                    value = searchInputState.value,
                    onValueChange = onSearchValueChanged,
                    maxLines = 1,
                    decorationBox = { textField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            textField()
                        }
                    }
                )
            }
        } else {
            Row(
                modifier = Modifier
            ) {
                IconButton(
                    onClick = closeBottomSheet,
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = enterSearchModePressed,
                    enabled = !launchLoading.value
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
                Button(
                    onClick = completePressed,
                    enabled = !launchLoading.value && completeButtonEnabled.value
                ) {
                    AnimatedContent(
                        targetState = launchLoading.value,
                        modifier = Modifier,
                        label = "loading_processing_animation",
                        contentAlignment = Alignment.Center
                    ) { loadingProcessing ->
                        if (loadingProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = LocalContentColor.current
                            )
                        } else {
                            Text(text = stringResource(R.string.string_select_media_info_complete))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectList(
    selectableItemList: ImmutableList<SelectableMusicItem>,
    onMusicItemClick: (SelectableMusicItem) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        items(
            items = selectableItemList,
            key = { it.musicItem.mediaId },
            contentType = { it.musicItem.javaClass.name }
        ) { selectableMusicItem ->
            MusicItemView(
                modifier = Modifier.fillParentMaxWidth(),
                musicItem = selectableMusicItem.musicItem,
                onClick = { onMusicItemClick(selectableMusicItem) },
                isSelected = selectableMusicItem.isSelected
            )
        }
    }
}