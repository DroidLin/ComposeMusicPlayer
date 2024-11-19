package com.music.android.lin.application.settings.usecase.scanner

import android.content.Context
import com.music.android.lin.modules.AppIdentifier
import com.music.android.lin.player.metadata.Album
import com.music.android.lin.player.metadata.Artist
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaType
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Named

interface MediaContentScanner {

    suspend fun scan(): List<MediaInfo>
}

fun MediaContentScanner(@Named(AppIdentifier.applicationContext) context: Context): MediaContentScanner {
    return AndroidContentProviderMediaScanner(context)
}

private class AndroidContentProviderMediaScanner(private val context: Context) :
    MediaContentScanner {

    override suspend fun scan(): List<MediaInfo> {
        return this.context.fetchMediaInformation().map { mediaInfoCursor ->
            val artistInformationList = this.context.fetchArtistInformation(mediaInfoCursor.artistId)
            val albumInformation = this.context.fetchAlbumInformation(mediaInfoCursor.albumId)
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
}

