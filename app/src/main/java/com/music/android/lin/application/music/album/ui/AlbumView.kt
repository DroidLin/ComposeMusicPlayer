package com.music.android.lin.application.music.album.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.music.android.lin.application.PageDeepLinks
import com.music.android.lin.application.PageDefinition
import com.music.android.lin.application.minibar.ui.minibarHeightPadding
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object AlbumView

fun NavGraphBuilder.albumView() {
    composable<AlbumView> {
        AlbumView(
            modifier = Modifier
                .fillMaxSize()
                .minibarHeightPadding()
                .navigationBarsPadding()
        )
    }
}

@Composable
fun AlbumView(modifier: Modifier = Modifier) {

}