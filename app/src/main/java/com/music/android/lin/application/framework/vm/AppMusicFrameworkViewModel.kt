package com.music.android.lin.application.framework.vm

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.music.android.lin.R
import com.music.android.lin.application.PageDefinition

val NavigationDrawerTypes = listOf(
    NavigationDrawerType.SingleMusic,
    NavigationDrawerType.Album,
    NavigationDrawerType.Artist,
    NavigationDrawerType.PlayList,
    NavigationDrawerType.ScanMusic,
    NavigationDrawerType.Settings,
    NavigationDrawerType.About,
)

@Stable
internal class AppMusicFrameworkViewModel(private val applicationContext: Context) : ViewModel() {
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

 val NavigationDrawerType.pageDefinitionName: String?
     get() = when (this) {
         NavigationDrawerType.SingleMusic -> PageDefinition.SingleMusicView::class.qualifiedName
         NavigationDrawerType.Album -> PageDefinition.AlbumView::class.qualifiedName
         NavigationDrawerType.Settings -> PageDefinition.SettingsView::class.qualifiedName
         NavigationDrawerType.About -> PageDefinition.AboutView::class.qualifiedName
         NavigationDrawerType.PlayList -> PageDefinition.PlayList::class.qualifiedName
         else -> null
     }