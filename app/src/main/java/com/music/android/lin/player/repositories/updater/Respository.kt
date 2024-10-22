package com.music.android.lin.player.repositories.updater

import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.harvest.musicplayer.repositories.buildAlbum
import com.harvest.musicplayer.repositories.buildArtist
import com.music.android.lin.AppKoin
import com.music.android.lin.player.interfaces.Album
import com.music.android.lin.player.interfaces.Artist
import com.music.android.lin.player.interfaces.MediaExtras
import com.music.android.lin.player.metadata.MutableMediaExtras
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * @author liuzhongao
 * @since 2023/12/10 6:13 PM
 */

private val context: Context get() = AppKoin.applicationContext

private const val MUSIC_INFO_COVER = "music_info_cover"
private const val ALBUM_COVER_BASE_CONTENT_URI = "content://media/external/audio/albumart"

private val coverCacheDir: File
    get() = context.getExternalFilesDir(MUSIC_INFO_COVER)
        ?: context.getDir(MUSIC_INFO_COVER, Context.MODE_PRIVATE)

internal suspend fun fetchAlbumInfo(albumId: String?): Album {
    return context.contentResolver.query(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
        null,
        "${MediaStore.Audio.Albums._ID} = ?",
        arrayOf(albumId),
        null
    )?.use { cursor ->
        if (cursor.moveToNext()) {
            decodeAlbumInfo(cursor)
        } else null
    } ?: Album
}

private suspend fun decodeAlbumInfo(cursor: Cursor): Album {
    val albumId = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM_ID)) ?: ""
    val albumName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM)) ?: ""
    val albumPublishDate = cursor.getLongOrNull(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.FIRST_YEAR)) ?: 0
    val songNumber = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS)) ?: 0
    val coverUrl = decodeAlbumCoverAndTransform(albumId = albumId) { bitmapBytes ->
        val file = File(coverCacheDir, "album_${albumId}.data")
        FileOutputStream(file).use {
            it.write(bitmapBytes)
            it.flush()
        }
        Uri.fromFile(file)
    }
    return buildAlbum(
        id = albumId,
        albumName = albumName,
        albumDescription = "",
        publishDate = albumPublishDate,
        mediaNumber = songNumber,
        coverUrl = coverUrl?.toString() ?: "null"
    )
}

private suspend fun decodeAlbumCoverAndTransform(
    albumId: String,
    transform: suspend (ByteArray) -> Uri
): Uri? {
    val baseUri = Uri.parse(ALBUM_COVER_BASE_CONTENT_URI).buildUpon().appendEncodedPath(albumId).build()
    return context.contentResolver.runCatching {
        openFileDescriptor(baseUri, "r").use { parcelFileDescriptor ->
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            if (fileDescriptor != null) {
                FileInputStream(fileDescriptor).use { fileInputStream ->
                    val buffer = ByteArray(1024)
                    val byteOutputStream = ByteArrayOutputStream()
                    var byteRead: Int
                    while (fileInputStream.read(buffer).also { byteRead = it } != -1) {
                        byteOutputStream.write(buffer, 0, byteRead)
                    }
                    val imageBytes = byteOutputStream.toByteArray()
                    transform(imageBytes)
                }
            } else null
        }
    }.onFailure { it.printStackTrace() }.getOrNull()
}

internal fun fetchArtistList(artistId: String?): List<Artist> {
    return context.contentResolver.query(
        MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
        null,
        "${MediaStore.Audio.Artists._ID} = ?",
        arrayOf(artistId),
        null
    )?.use { artistCursor ->
        ArrayList<Artist>().also { artistList ->
            while (artistCursor.moveToNext()) {
                artistList += decodeArtistInfo(artistCursor)
            }
        }
    } ?: emptyList()
}

private fun decodeArtistInfo(cursor: Cursor): Artist {
    val artistId = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.Artists._ID)) ?: ""
    val artistName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.ARTIST)) ?: ""
    val numberOfAlbum = cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS)) ?: 0
    val artistDescription = ""
    return buildArtist(
        id = artistId,
        name = artistName,
        description = artistDescription,
        numberOfAlbum = numberOfAlbum
    )
}

fun decodeMediaExtras(sourceUri: String?): MediaExtras {
    return kotlin.runCatching {
        MediaMetadataRetriever().use { mediaMetadataRetriever ->
            mediaMetadataRetriever.setDataSource(sourceUri)
            val mediaExtras = MutableMediaExtras()
            mediaExtras.duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            mediaExtras.writer = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER) ?: ""
            mediaExtras.mimeType = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: ""
            mediaExtras.containsAudioSource = mediaMetadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)?.toBooleanStrictOrNull() ?: false
            mediaExtras.containsVideoSource = mediaMetadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)?.toBooleanStrictOrNull() ?: false
            mediaExtras.videoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
            mediaExtras.videoHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
            mediaExtras.bitRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
            mediaExtras.captureFrameRate = mediaMetadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)?.toFloatOrNull() ?: 0f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                mediaExtras.colorRange = mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_COLOR_RANGE)?.toIntOrNull() ?: 0
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mediaExtras.sampleRate = mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_SAMPLERATE) ?: ""
                mediaExtras.bitPerSample = mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE) ?: ""
            }
            mediaExtras
        }
    }.onFailure { it.printStackTrace() }.getOrNull() ?: MediaExtras
}