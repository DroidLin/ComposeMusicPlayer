package com.music.android.lin.application.pages.settings.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.BackButton
import com.music.android.lin.application.common.ui.component.DataLoadingView
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.pages.minibar.ui.minibarHeightPadding
import com.music.android.lin.application.pages.settings.model.SettingSection
import com.music.android.lin.application.pages.settings.model.SettingSectionItem
import com.music.android.lin.application.pages.settings.model.SettingSectionType
import com.music.android.lin.application.pages.settings.ui.component.ResetConfirmDialog
import com.music.android.lin.application.pages.settings.ui.component.SettingItemViewHolder
import com.music.android.lin.application.pages.settings.ui.vm.AppSettingsViewModel
import com.music.android.lin.application.pages.settings.ui.vm.fakeData
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Stable
@Serializable
data object Settings

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(route = Settings, navOptions = navOptions)
}

fun NavGraphBuilder.settingsView(
    navigateToAbout: () -> Unit,
    navigateToMediaScanner: () -> Unit,
    backPressed: () -> Unit,
    showBackButton: Boolean = false,
) {
    composable<Settings> {
        AppSettingsHomeView(
            modifier = Modifier
                .fillMaxSize()
                .minibarHeightPadding(),
            backPressed = backPressed,
            goToAboutView = navigateToAbout,
            showBackButton = showBackButton,
            navigateToMediaScanner = navigateToMediaScanner
        )
    }
}

@Composable
fun AppSettingsHomeView(
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
    backPressed: () -> Unit = {},
    goToAboutView: () -> Unit = {},
    navigateToMediaScanner: () -> Unit = {},
) {
    val viewModel = koinViewModel<AppSettingsViewModel>()
    val dataLoadState = viewModel.settingsList.collectAsStateWithLifecycle()
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    ContentSettingsView(
        showBackButton = showBackButton,
        modifier = modifier,
        backPress = backPressed,
        state = dataLoadState,
        onSectionClick = { settingSectionItem ->
            when (settingSectionItem.itemType) {
                SettingSectionType.About -> goToAboutView()
                SettingSectionType.ResetAll -> showResetConfirmDialog = true
                SettingSectionType.MediaStoreScanner -> navigateToMediaScanner()
                else -> {}
            }
        }
    )
    ResetConfirmDialog(
        showDialog = showResetConfirmDialog,
        dismissRequest = { showResetConfirmDialog = false },
        confirmResetAll = { showResetConfirmDialog = false }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentSettingsView(
    showBackButton: Boolean,
    state: State<DataLoadState>,
    backPress: () -> Unit = {},
    onSectionClick: (SettingSectionItem) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        DataLoadingView(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { data ->
            Column(
                modifier = Modifier.matchParentSize(),
            ) {
                val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
                TopHeader(
                    showBackButton = showBackButton,
                    backPress = backPress,
                    scrollBehavior = scrollBehavior,
                )
                SettingsLazyColumn(
                    data = data,
                    modifier = Modifier
                        .weight(1f)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    onSectionClick = onSectionClick
                )
            }
        }
    }
}

@Composable
private fun SettingsLazyColumn(
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
                val viewHolderModifier = if (index == 0 && sectionIndex != 0) {
                    Modifier.padding(top = 16.dp)
                } else Modifier
                SettingItemViewHolder(
                    modifier = viewHolderModifier,
                    sectionItem = item,
                    onClick = { onSectionClick(item) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    showBackButton: Boolean,
    backPress: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier,
) {
    TopAppBarLayout(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.string_settings_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        navigationIcon = {
            if (showBackButton) {
                BackButton(onClick = backPress)
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun HeaderPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        TopHeader(showBackButton = false, backPress = {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
private fun AppSettingsPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        ContentSettingsView(
            showBackButton = false,
            backPress = {},
            modifier = Modifier.fillMaxSize(),
            state = remember {
                derivedStateOf { DataLoadState.Data(data = fakeData) }
            }
        )
    }
}
