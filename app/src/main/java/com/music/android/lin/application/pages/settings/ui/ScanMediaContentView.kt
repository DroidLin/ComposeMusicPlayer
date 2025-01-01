package com.music.android.lin.application.pages.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.BackButton
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.pages.settings.ui.component.SettingsNormalInformationItem
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object ScanMediaContent

fun NavController.navigateToScanMediaContent(navOptions: NavOptions? = null) {
    navigate(
        route = ScanMediaContent,
        navOptions = navOptions ?: navOptions {
            launchSingleTop = true
        }
    )
}

fun NavGraphBuilder.scanMediaContentView(
    backPressed: () -> Unit,
) {
    composable<ScanMediaContent> {
        ScanMediaContentView(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            backPressed = backPressed
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanMediaContentView(
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showScanningDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier,
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        TopAppBarLayout(
            title = {
                Text(text = stringResource(R.string.string_scan_local_media_content))
            },
            navigationIcon = {
                BackButton(onClick = backPressed)
            },
            scrollBehavior = scrollBehavior,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            repeat(100) { index ->
                item(
                    key = "scan_local_media_${index}"
                ) {
                    SettingsNormalInformationItem(
                        title = stringResource(R.string.string_scan_local_media_title),
                        subTitle = stringResource(R.string.string_scan_local_media_description),
                        icons = Icons.Default.Search,
                        onClick = {}
                    )
                }
            }
        }
    }
    ScanLoadingDialog(
        modifier = Modifier,
        show = showScanningDialog,
        onDismissRequest = { showScanningDialog = false }
    )
}

@Composable
fun ScanLoadingDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!show) return
}