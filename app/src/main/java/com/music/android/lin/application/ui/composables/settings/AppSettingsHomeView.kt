package com.music.android.lin.application.ui.composables.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.model.DataLoadState
import com.music.android.lin.application.ui.composables.component.LoadingProgress
import com.music.android.lin.application.ui.composables.framework.AppMaterialTheme
import com.music.android.lin.application.ui.composables.music.component.DataLoadView
import com.music.android.lin.application.ui.composables.settings.model.SettingSection
import com.music.android.lin.application.ui.composables.settings.vm.AppSettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppSettingsHomeView(backPress: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<AppSettingsViewModel>()
    val dataLoadState = viewModel.settingsList.collectAsStateWithLifecycle()
    ContentSettingsView(
        modifier = modifier
            .navigationBarsPadding(),
        backPress = backPress,
        state = dataLoadState
    )
}

@Composable
private fun ContentSettingsView(
    state: State<DataLoadState>,
    backPress: () -> Unit,
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
                        .weight(1f)
                )
            }
        )
    }
}

@Composable
fun SettingsLazyColumn(
    data: DataLoadState.Data<List<SettingSection>>,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    LazyColumn(
        state = lazyListState,
        modifier = modifier,
    ) {
        val settingSectionList = data.data
        settingSectionList.forEach { settingSection ->
            itemsIndexed(
                items = settingSection.sectionItems,
                key = { _, item -> item.itemType },
                contentType = { _, item -> item.itemType }
            ) { index, item ->

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

@Preview
@Composable
private fun AppSettingsPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        ContentSettingsView(
            backPress = {},
            modifier = Modifier.fillMaxSize(),
            state = remember {
                derivedStateOf { DataLoadState.Loading }
            }
        )
    }
}
