package com.music.android.lin.application.framework.vm

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.YoutubeSearchedFor
import androidx.compose.material.icons.outlined.ArtTrack
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.YoutubeSearchedFor
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.music.android.lin.R
import com.music.android.lin.application.music.MediaStoreScreen
import com.music.android.lin.application.music.artist.ui.ArtistScreen
import com.music.android.lin.application.music.single.ui.SimpleMusicView
import com.music.android.lin.application.settings.ui.AboutScreen
import com.music.android.lin.application.settings.ui.Settings
import kotlin.reflect.KClass

@Stable
internal class AppMusicFrameworkViewModel(private val applicationContext: Context) : ViewModel()

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
    Musician(
        titleResId = R.string.string_drawer_name_artist,
        selectedIcon = Icons.Filled.ArtTrack,
        unSelectedIcon = Icons.Outlined.ArtTrack,
        route = ArtistScreen::class
    ),
    PlayList(
        titleResId = R.string.string_drawer_name_playlist,
        selectedIcon = Icons.Filled.List,
        unSelectedIcon = Icons.Outlined.List,
        route = com.music.android.lin.application.music.playlist.ui.PlayList::class
    ),
    Scan(
        titleResId = R.string.string_drawer_name_scan_music,
        selectedIcon = Icons.Filled.YoutubeSearchedFor,
        unSelectedIcon = Icons.Outlined.YoutubeSearchedFor,
        route = MediaStoreScreen::class
    ),
    Setting(
        titleResId = R.string.string_settings_title,
        selectedIcon = Icons.Filled.Settings,
        unSelectedIcon = Icons.Outlined.Settings,
        route = Settings::class
    ),
    About(
        titleResId = R.string.string_about_title,
        selectedIcon = Icons.Filled.Notifications,
        unSelectedIcon = Icons.Outlined.Notifications,
        route = AboutScreen::class
    )
}

@Stable
enum class NavigationDrawerType(
    @StringRes val nameResId: Int,
    @DrawableRes val drawableResId: Int,
) {
    SingleMusic(nameResId = R.string.string_drawer_name_single_music, drawableResId = R.drawable.ic_music),
    Album(nameResId = R.string.string_drawer_name_album, drawableResId = R.drawable.ic_launcher_foreground),
    Artist(nameResId = R.string.string_drawer_name_artist, drawableResId = R.drawable.ic_launcher_foreground),
    PlayList(nameResId = R.string.string_drawer_name_playlist, drawableResId = R.drawable.ic_list),
    ScanMusic(nameResId = R.string.string_drawer_name_scan_music, drawableResId = R.drawable.ic_launcher_foreground),
    Settings(nameResId = R.string.string_drawer_name_settings, drawableResId = R.drawable.ic_setting),
    About(nameResId = R.string.string_drawer_name_about_app, drawableResId = R.drawable.ic_about),
}
