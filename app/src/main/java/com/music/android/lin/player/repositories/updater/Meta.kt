package com.harvest.musicplayer.repositories.updater

import com.harvest.musicplayer.MediaExtras

/**
 * @author liuzhongao
 * @since 2023/12/10 6:26 PM
 */


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