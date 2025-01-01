package com.music.android.lin.application.framework.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.music.android.lin.R
import com.music.android.lin.application.pages.music.single.ui.SimpleMusicView
import com.music.android.lin.application.pages.settings.ui.Settings
import kotlin.reflect.KClass

@Stable
enum class TopLevelDestination(
    @StringRes val titleResId: Int,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    Music(
        titleResId = R.string.string_drawer_name_single_music,
        selectedIcon = Icons.Filled.MusicNote,
        unSelectedIcon = Icons.Outlined.MusicNote,
        route = SimpleMusicView::class
    ),

    //    Musician(
//        titleResId = R.string.string_drawer_name_artist,
//        selectedIcon = Icons.Filled.ArtTrack,
//        unSelectedIcon = Icons.Outlined.ArtTrack,
//        route = ArtistScreen::class
//    ),
    PlayList(
        titleResId = R.string.string_drawer_name_playlist,
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unSelectedIcon = Icons.AutoMirrored.Outlined.List,
        route = com.music.android.lin.application.pages.music.playlist.ui.PlayList::class
    ),

    //    Scan(
//        titleResId = R.string.string_drawer_name_scan_music,
//        selectedIcon = Icons.Filled.YoutubeSearchedFor,
//        unSelectedIcon = Icons.Outlined.YoutubeSearchedFor,
//        route = ScanMediaContent::class
//    ),
    Setting(
        titleResId = R.string.string_settings_title,
        selectedIcon = Icons.Filled.Settings,
        unSelectedIcon = Icons.Outlined.Settings,
        route = Settings::class
    ),
//    About(
//        titleResId = R.string.string_about_title,
//        selectedIcon = Icons.Filled.Notifications,
//        unSelectedIcon = Icons.Outlined.Notifications,
//        route = AboutScreen::class
//    )
}
