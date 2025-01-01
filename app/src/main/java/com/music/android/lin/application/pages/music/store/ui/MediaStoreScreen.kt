package com.music.android.lin.application.pages.music.store.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object MediaStoreScreen

fun NavController.navigateToMediaStore() {
    navigate(route = MediaStoreScreen) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.mediaStoreScreen(showBackButton: Boolean = false, backPressed: () -> Unit) {
    composable<MediaStoreScreen> {
        MediaStoreScreen(
            backPressed = backPressed,
            showBackButton = showBackButton,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
internal fun MediaStoreScreen(
    showBackButton: Boolean,
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {

}