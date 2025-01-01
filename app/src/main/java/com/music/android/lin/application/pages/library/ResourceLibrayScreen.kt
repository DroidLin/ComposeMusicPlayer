package com.music.android.lin.application.pages.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object ResourceLibrary

fun NavController.navigateToResourceLibrary(navOptions: NavOptions? = null) {
    this.navigate(route = ResourceLibrary, navOptions = navOptions)
}

fun NavGraphBuilder.resourceLibrary() {
    composable<ResourceLibrary> {
        ResourceLibraryScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
internal fun ResourceLibraryScreen(modifier: Modifier = Modifier) {

}