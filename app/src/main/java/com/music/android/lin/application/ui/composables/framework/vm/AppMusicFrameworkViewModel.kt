package com.music.android.lin.application.ui.composables.framework.vm

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.music.android.lin.R

val NavigationDrawerTypes = listOf(
    NavigationDrawerType.SingleMusic,
    NavigationDrawerType.Album,
    NavigationDrawerType.Artist,
    NavigationDrawerType.PlayList,
    NavigationDrawerType.ScanMusic,
    NavigationDrawerType.Settings,
    NavigationDrawerType.About,
)

internal class AppMusicFrameworkViewModel(private val applicationContext: Context) : ViewModel() {
}

enum class NavigationDrawerType(
    @StringRes val nameResId: Int,
    @DrawableRes val drawableResId: Int,
) {
    SingleMusic(nameResId = R.string.string_drawer_name_single_music, drawableResId = R.drawable.ic_launcher_foreground),
    Album(nameResId = R.string.string_drawer_name_album, drawableResId = R.drawable.ic_launcher_foreground),
    Artist(nameResId = R.string.string_drawer_name_artist, drawableResId = R.drawable.ic_launcher_foreground),
    PlayList(nameResId = R.string.string_drawer_name_playlist, drawableResId = R.drawable.ic_launcher_foreground),
    ScanMusic(nameResId = R.string.string_drawer_name_scan_music, drawableResId = R.drawable.ic_launcher_foreground),
    Settings(nameResId = R.string.string_drawer_name_settings, drawableResId = R.drawable.ic_launcher_foreground),
    About(nameResId = R.string.string_drawer_name_about_app, drawableResId = R.drawable.ic_launcher_foreground),
}