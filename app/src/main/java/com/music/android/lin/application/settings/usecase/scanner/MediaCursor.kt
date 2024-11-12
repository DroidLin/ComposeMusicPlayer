package com.music.android.lin.application.settings.usecase.scanner

import com.music.android.lin.player.metadata.MediaExtras

data class MediaInfoCursor(
    val id: String,
    val title: String,
    val composer: String,
    val artistId: String,
    val albumId: String,
    val sourceUri: String,
    val duration: Int,
    val mediaExtras: MediaExtras
)

data class MediaArtistCursor(
    val id: String,
    val artist: String,
    val numberOfAlbums: Int,
    val numberOfTracks: Int
)

data class MediaAlbumCursor(
    val id: String,
    val name: String,
    val date: Long,
    val numberOfSong: Int,
    val cover: String,
)