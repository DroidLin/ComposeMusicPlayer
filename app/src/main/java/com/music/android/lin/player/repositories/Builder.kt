package com.harvest.musicplayer.repositories

import com.harvest.musicplayer.Album
import com.harvest.musicplayer.Artist
import com.harvest.musicplayer.ExtensionMap
import com.music.android.lin.player.ExtensionMapImpl
import com.harvest.musicplayer.MediaExtras
import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.MediaType
import com.harvest.musicplayer.PlayList
import com.harvest.musicplayer.PlayListType
import com.harvest.musicplayer.PlayRecord
import com.harvest.musicplayer.metadata.AlbumImpl
import com.harvest.musicplayer.metadata.ArtistImpl
import com.harvest.musicplayer.metadata.MediaExtrasImpl
import com.harvest.musicplayer.metadata.MediaInfoImpl
import com.harvest.musicplayer.metadata.PlayListImpl
import com.harvest.musicplayer.metadata.PlayRecordImpl
import org.json.JSONObject

/**
 * @author liuzhongao
 * @since 2023/10/6 15:00
 */
fun buildPlayList(
    id: String,
    type: PlayListType,
    name: String,
    description: String,
    mediaInfoList: MutableList<MediaInfo> = mutableListOf(),
    extensions: ExtensionMap? = ExtensionMapImpl(),
    updateTimeStamp: Long = System.currentTimeMillis(),
): PlayList {
    return PlayListImpl(
        id = id,
        type = type,
        name = name,
        description = description,
        mediaInfoList = mediaInfoList,
        extensions = extensions,
        updateTimeStamp = updateTimeStamp,
    )
}

fun buildPlayList(
    playList: PlayList
): PlayList {
    return PlayListImpl(
        id = playList.id,
        type = playList.type,
        name = playList.name,
        description = playList.description,
        mediaInfoList = playList.mediaInfoList,
        extensions = playList.extensions,
        updateTimeStamp = playList.updateTimeStamp,
    )
}

fun buildMediaInfo(mediaInfo: MediaInfo): MediaInfo {
    return buildMediaInfo(
        id = mediaInfo.id,
        mediaTitle = mediaInfo.mediaTitle,
        mediaDescription = mediaInfo.mediaDescription,
        artists = mediaInfo.artists.map { artist ->
            buildArtist(artist)
        },
        album = buildAlbum(mediaInfo.album),
        coverUri = mediaInfo.coverUri,
        sourceUri = mediaInfo.sourceUri,
        updateTimeStamp = mediaInfo.updateTimeStamp,
        mediaType = mediaInfo.mediaType,
        mediaExtras = mediaInfo.mediaExtras
    )
}

fun buildMediaInfo(
    id: String,
    mediaTitle: String,
    mediaDescription: String,
    artists: List<Artist>,
    album: Album,
    coverUri: String?,
    sourceUri: String?,
    updateTimeStamp: Long,
    mediaType: MediaType,
    mediaExtras: MediaExtras
): MediaInfo {
    return MediaInfoImpl(
        id = id,
        mediaTitle = mediaTitle,
        mediaDescription = mediaDescription,
        artists = artists,
        album = album,
        coverUri = coverUri,
        sourceUri = sourceUri,
        updateTimeStamp = updateTimeStamp,
        mediaType = mediaType,
        mediaExtras = mediaExtras
    )
}

fun buildArtist(artist: Artist): Artist {
    return buildArtist(
        id = artist.id,
        name = artist.name,
        description = artist.description,
        numberOfAlbum = artist.numberOfAlbum,
    )
}

fun buildArtist(
    id: String,
    name: String,
    description: String,
    numberOfAlbum: Int
): Artist {
    return ArtistImpl(
        id = id,
        name = name,
        description = description,
        numberOfAlbum = numberOfAlbum
    )
}

fun buildAlbum(album: Album): Album {
    return buildAlbum(
        id = album.id,
        albumName = album.albumName,
        albumDescription = album.albumDescription,
        publishDate = album.publishDate,
        mediaNumber = album.mediaNumber,
        coverUrl = album.coverUrl,
    )
}

fun buildAlbum(
    id: String,
    albumName: String,
    albumDescription: String,
    publishDate: Long,
    mediaNumber: Int,
    coverUrl: String
): Album {
    return AlbumImpl(
        id = id,
        albumName = albumName,
        albumDescription = albumDescription,
        publishDate = publishDate,
        mediaNumber = mediaNumber,
        coverUrl = coverUrl
    )
}

fun buildPlayRecord(
    id: Long,
    mediaResourceId: String,
    mediaResourceType: PlayRecord.PlayRecordResourceType,
    recordTimeStamp: Long,
): PlayRecord {
    return PlayRecordImpl(
        id = id,
        mediaResourceId = mediaResourceId,
        mediaResourceType = mediaResourceType,
        recordTimeStamp = recordTimeStamp
    )
}

fun buildMediaExtras(jsonString: String): MediaExtras {
    val jsonObject = kotlin.runCatching {
        JSONObject(jsonString)
    }.onFailure { it.printStackTrace() }.getOrNull() ?: return MediaExtras

    val duration: Long = jsonObject.optLong("duration", 0)
    val writer: String = jsonObject.optString("writer", "")
    val mimeType: String = jsonObject.optString("mimeType", "")
    val containsAudioSource: Boolean = jsonObject.optBoolean("containsAudioSource", false)
    val containsVideoSource: Boolean = jsonObject.optBoolean("containsVideoSource", false)
    val videoWidth: Int = jsonObject.optInt("videoWidth", 0)
    val videoHeight: Int = jsonObject.optInt("videoHeight", 0)
    val bitRate: Int = jsonObject.optInt("bitRate", 0)
    val captureFrameRate: Float = jsonObject.optDouble("captureFrameRate", 0.0).toFloat()
    val colorRange: Int = jsonObject.optInt("colorRange", 0)
    val sampleRate: String = jsonObject.optString("sampleRate", "")
    val bitPerSample: String = jsonObject.optString("bitPerSample", "")

    return MediaExtrasImpl(
        duration = duration,
        writer = writer,
        mimeType = mimeType,
        containsAudioSource = containsAudioSource,
        containsVideoSource = containsVideoSource,
        videoWidth = videoWidth,
        videoHeight = videoHeight,
        bitRate = bitRate,
        captureFrameRate = captureFrameRate,
        colorRange = colorRange,
        sampleRate = sampleRate,
        bitPerSample = bitPerSample,
    )
}
