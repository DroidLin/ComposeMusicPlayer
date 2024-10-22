package com.music.android.lin.player.repositories.updater

data class MusicCursorRecord(
    val musicInfoId: String,
    val musicInfoTitle: String,
    val musicInfoDescription: String,
    val artistId: String?,
    val albumId: String?,
    val songLocalUri: String?,
)

data class VideoCursorRecord(
    val id: String,
    val title: String,
    val description: String,
    val artistName: String?,
    val albumName: String?,
    val sourceUri: String?,
)