package com.music.android.lin.application.ui.composables.settings

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.model.DataLoadState
import com.music.android.lin.application.ui.composables.component.LoadingProgress
import com.music.android.lin.application.ui.composables.framework.AppMaterialTheme
import com.music.android.lin.application.ui.composables.music.component.DataLoadView
import com.music.android.lin.application.ui.composables.settings.component.ResetConfirmDialog
import com.music.android.lin.application.ui.composables.settings.component.SettingItemViewHolder
import com.music.android.lin.application.ui.composables.settings.model.SettingSection
import com.music.android.lin.application.ui.composables.settings.model.SettingSectionItem
import com.music.android.lin.application.ui.composables.settings.model.SettingSectionType
import com.music.android.lin.application.ui.composables.settings.vm.AppSettingsViewModel
import com.music.android.lin.application.ui.composables.settings.vm.fakeData
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppSettingsHomeView(
    backPress: () -> Unit = {},
    goToAboutView: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<AppSettingsViewModel>()
    val dataLoadState = viewModel.settingsList.collectAsStateWithLifecycle()
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    ContentSettingsView(
        modifier = modifier
            .navigationBarsPadding(),
        backPress = backPress,
        state = dataLoadState,
        onSectionClick = { settingSectionItem ->
            when (settingSectionItem.itemType) {
                SettingSectionType.About -> goToAboutView()
                SettingSectionType.ResetAll -> showResetConfirmDialog = true
                else -> {}
            }
        }
    )
    ResetConfirmDialog(showDialog = showResetConfirmDialog)
}

@Composable
private fun ContentSettingsView(
    state: State<DataLoadState>,
    backPress: () -> Unit = {},
    onSectionClick: (SettingSectionItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        TopHeader(backPress = backPress)
        DataLoadView(
            state = state,
            loading = {
                LoadingProgress(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            },
            data = { data ->
                SettingsLazyColumn(
                    data = data,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onSectionClick = onSectionClick
                )
            }
        )
    }
}

@Composable
fun SettingsLazyColumn(
    data: DataLoadState.Data<List<SettingSection>>,
    onSectionClick: (SettingSectionItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
    ) {
        val settingSectionList = data.data
        settingSectionList.forEachIndexed { sectionIndex, settingSection ->
            itemsIndexed(
                items = settingSection.sectionItems,
                key = { _, item -> item.itemType },
                contentType = { _, item -> item.itemType }
            ) { index, item ->
                var topCorner = Dp.Hairline
                var bottomCorner = Dp.Hairline
                if (index == 0) {
                    topCorner = 10.dp
                }
                if (index == (settingSection.sectionItems.size - 1)) {
                    bottomCorner = 10.dp
                }
                var topMargin = Dp.Hairline
                if (index == 0 && sectionIndex != 0) {
                    topMargin = 10.dp
                }
                Surface(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(top = topMargin, start = 10.dp, end = 10.dp),
                    onClick = {
                        onSectionClick(item)
                    },
                    shape = RoundedCornerShape(
                        topStart = topCorner,
                        topEnd = topCorner,
                        bottomStart = bottomCorner,
                        bottomEnd = bottomCorner
                    ),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    SettingItemViewHolder(
                        modifier = Modifier,
                        sectionItem = item
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(backPress: () -> Unit, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.string_settings_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        navigationIcon = {
            IconButton(
                onClick = backPress
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

@Preview
@Composable
private fun HeaderPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        TopHeader(backPress = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun AppSettingsPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        ContentSettingsView(
            backPress = {},
            modifier = Modifier.fillMaxSize(),
            state = remember {
                derivedStateOf { DataLoadState.Data(data = fakeData) }
            }
        )
    }
}
