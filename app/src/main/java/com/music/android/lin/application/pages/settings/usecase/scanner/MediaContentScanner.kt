package com.music.android.lin.application.pages.settings.usecase.scanner

import android.content.Context
import android.net.Uri
import com.music.android.lin.player.metadata.Album
import com.music.android.lin.player.metadata.Artist
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaType
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

interface MediaContentScanner {

    suspend fun scan(): List<MediaInfo>
}

fun MediaContentScanner(context: Context): MediaContentScanner {
    return AndroidContentProviderMediaScanner(context)
}

private class AndroidContentProviderMediaScanner(
    private val context: Context
) : MediaContentScanner {

    override suspend fun scan(): List<MediaInfo> {
        return this.context.fetchMediaInformation().convertToMediaInfo(context = this.context)
    }
}

suspend fun Context.fetchMediaInfoWithUri(uri: Uri): List<MediaInfo> {
    return this.fetchWithUri(uri).convertToMediaInfo(this)
}

suspend fun List<MediaInfoCursor>.convertToMediaInfo(context: Context): List<MediaInfo> {
    return this.mapNotNull { mediaInfoCursor ->
        if (!coroutineContext.isActive) return@mapNotNull null
        val artistInformationList = context.fetchArtistInformation(mediaInfoCursor.artistId)
        val albumInformation = context.fetchAlbumInformation(mediaInfoCursor.albumId)
        MediaInfo(
            id = mediaInfoCursor.id,
            mediaTitle = mediaInfoCursor.title,
            mediaDescription = mediaInfoCursor.composer,
            artists = artistInformationList.map { mediaArtistCursor ->
                Artist(
                    id = mediaArtistCursor.id,
                    name = mediaArtistCursor.artist,
                    description = "",
                    numberOfAlbum = mediaArtistCursor.numberOfAlbums,
                    numberOfTracks = mediaArtistCursor.numberOfTracks
                )
            },
            album = Album(
                id = albumInformation.id,
                albumName = albumInformation.name,
                albumDescription = "",
                publishDate = albumInformation.date,
                mediaNumber = albumInformation.numberOfSong,
                coverUrl = albumInformation.cover
            ),
            coverUri = albumInformation.cover,
            sourceUri = mediaInfoCursor.sourceUri,
            updateTimeStamp = System.currentTimeMillis(),
            mediaType = MediaType.Audio,
            mediaExtras = mediaInfoCursor.mediaExtras
        )
    }
}