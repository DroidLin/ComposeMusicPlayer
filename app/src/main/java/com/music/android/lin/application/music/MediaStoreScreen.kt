package com.music.android.lin.application.music

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object MediaStoreScreen

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