package com.music.android.lin.application.pages.music.search.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.music.android.lin.application.PageDefinition
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object SearchRoute

fun NavController.navigateToSearchRoute(navOptions: NavOptions? = null) {
    this.navigate(route = SearchRoute, navOptions = navOptions)
}

fun NavGraphBuilder.searchScreen() {
    composable<SearchRoute> {
        SearchView(modifier = Modifier.fillMaxSize())
    }
}

@Composable
internal fun SearchView(modifier: Modifier = Modifier) {

}