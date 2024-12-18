package com.music.android.lin.application.framework

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

val LocalWindowInfo = compositionLocalOf< WindowAdaptiveInfo> { error("not provided") }

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AppAdaptiveLayoutFramework(
    modifier: Modifier = Modifier
) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfo()
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>(
        scaffoldDirective = calculatePaneScaffoldDirective(windowAdaptiveInfo)
    )
    WindowInfoProvider(
        windowAdaptiveInfo = windowAdaptiveInfo
    ) {
        NavigableListDetailPaneScaffold(
            navigator = navigator,
            listPane = {
                val destination = navigator.currentDestination?.pane
            },
            detailPane = {

            },
            extraPane = {

            }
        )
    }
}

@Composable
private fun WindowInfoProvider(
    windowAdaptiveInfo: WindowAdaptiveInfo,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(value = LocalWindowInfo provides windowAdaptiveInfo, content = content)
}