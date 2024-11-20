package com.music.android.lin.application.settings.usecase.scanner

import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.music.android.lin.player.metadata.MediaExtras
import com.music.android.lin.player.metadata.MutableMediaExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File
import java.util.LinkedList
import kotlin.time.Duration.Companion.minutes

private const val MEDIA_COVER_DIR = "mediaCoverCache"
private const val ALBUM_COVER_BASE_CONTENT_URI = "content://media/external/audio/albumart"

private val Context.albumCacheDir: File
    get() = this.getExternalFilesDir(MEDIA_COVER_DIR)
        ?: this.getDir(MEDIA_COVER_DIR, Context.MODE_PRIVATE)

suspend fun Context.fetchMediaInformation(filterDuration: Long = 2L.minutes.inWholeMilliseconds): List<MediaInfoCursor> {
    val externalMediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
    val selection = "${MediaStore.Audio.AudioColumns.DURATION} > $filterDuration"
    return withContext(Dispatchers.IO) {
        contentResolver.query(externalMediaContentUri, null, selection, null, sortOrder)
            ?.use { rawCursor ->
                val mediaInfoCursorList = LinkedList<MediaInfoCursor>()
                MediaMetadataRetriever().use { metadataRetriever ->
                    while (isActive && rawCursor.moveToNext()) {
                        val id = rawCursor.getStringByColumnName(MediaStore.Audio.AudioColumns._ID) ?: ""
                        val title = rawCursor.getStringByColumnName(MediaStore.Audio.AudioColumns.TITLE) ?: ""
                        val composer = rawCursor.getStringByColumnName(MediaStore.Audio.AudioColumns.COMPOSER) ?: ""
                        val artistId = rawCursor.getStringByColumnName(MediaStore.Audio.AudioColumns.ARTIST_ID) ?: ""
                        val albumId = rawCursor.getStringByColumnName(MediaStore.Audio.AudioColumns.ALBUM_ID) ?: ""
                        val sourceUri = rawCursor.getStringByColumnName(MediaStore.Audio.AudioColumns.DATA) ?: ""
                        val duration = rawCursor.getIntByColumnName(MediaStore.Audio.AudioColumns.DURATION) ?: 0
                        val mediaExtras = metadataRetriever.decodeMediaExtras(sourceUri)

                        val fileUri = Uri.fromFile(File(sourceUri))

                        mediaInfoCursorList += MediaInfoCursor(
                            id = id,
                            title = title,
                            composer = composer,
                            artistId = artistId,
                            albumId = albumId,
                            sourceUri = fileUri.toString(),
                            duration = duration,
                            mediaExtras = mediaExtras
                        )
                    }
                }
                mediaInfoCursorList
            } ?: emptyList()
    }
}

suspend fun Context.fetchAlbumInformation(albumId: String): MediaAlbumCursor {
    val externalMediaContentUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
    val sortOrder = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
    return withContext(Dispatchers.IO) {
        contentResolver.query(externalMediaContentUri, null, "${MediaStore.Audio.Albums.ALBUM_ID} = ${albumId}", null, sortOrder)
            ?.use { rawCursor ->
                if (!rawCursor.moveToNext()) return@use null
                val id = rawCursor.getStringByColumnName(MediaStore.Audio.Albums.ALBUM_ID) ?: ""
                val name = rawCursor.getStringByColumnName(MediaStore.Audio.Albums.ALBUM) ?: ""
                val date = rawCursor.getLongByColumnName(MediaStore.Audio.Albums.FIRST_YEAR) ?: 0L
                val numberOfSong = rawCursor.getIntByColumnName(MediaStore.Audio.Albums.NUMBER_OF_SONGS) ?: 0
                val coverUri = decodeAlbumCover(albumId)
                MediaAlbumCursor(
                    id = id,
                    name = name,
                    date = date,
                    numberOfSong = numberOfSong,
                    cover = coverUri
                )
            } ?: MediaAlbumCursor(id = albumId, name = "", date = 0L, numberOfSong = 0, cover = "")
    }
}

private fun Context.decodeAlbumCover(albumId: String): String {
    val coverUri = Uri.parse(ALBUM_COVER_BASE_CONTENT_URI).buildUpon().appendPath(albumId).build()
    return coverUri.toString()
}

suspend fun Context.fetchArtistInformation(artistId: String): List<MediaArtistCursor> {
    val externalMediaContentUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
    val sortOrder = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
    return withContext(Dispatchers.IO) {
        contentResolver.query(externalMediaContentUri, null, "${MediaStore.Audio.Artists._ID} = $artistId", null, sortOrder)
            ?.use { rawCursor ->
                val mediaArtistCursorList = LinkedList<MediaArtistCursor>()
                while (isActive && rawCursor.moveToNext()) {
                    val id = rawCursor.getStringByColumnName(MediaStore.Audio.Artists._ID) ?: ""
                    val name = rawCursor.getStringByColumnName(MediaStore.Audio.Artists.ARTIST) ?: ""
                    val numberOfAlbums = rawCursor.getIntByColumnName(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS) ?: 0
                    val numberOfTracks = rawCursor.getIntByColumnName(MediaStore.Audio.Artists.NUMBER_OF_TRACKS) ?: 0
                    mediaArtistCursorList += MediaArtistCursor(
                        id = id,
                        artist = name,
                        numberOfAlbums = numberOfAlbums,
                        numberOfTracks = numberOfTracks
                    )
                }
                mediaArtistCursorList
            } ?: emptyList()
    }
}

private fun MediaMetadataRetriever.decodeMediaExtras(sourceUri: String?): MediaExtras {
    return kotlin.runCatching {
        val mediaMetadataRetriever = this
        mediaMetadataRetriever.setDataSource(sourceUri)
        val mediaExtras = MutableMediaExtras()
        mediaExtras.duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        mediaExtras.writer = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_WRITER) ?: ""
        mediaExtras.mimeType = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE) ?: ""
        mediaExtras.containsAudioSource = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)?.toBooleanStrictOrNull() ?: false
        mediaExtras.containsVideoSource = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)?.toBooleanStrictOrNull() ?: false
        mediaExtras.videoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull() ?: 0
        mediaExtras.videoHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull() ?: 0
        mediaExtras.bitRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull() ?: 0
        mediaExtras.captureFrameRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CAPTURE_FRAMERATE)?.toFloatOrNull() ?: 0f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mediaExtras.colorRange = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COLOR_RANGE)?.toIntOrNull() ?: 0
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            mediaExtras.sampleRate = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE) ?: ""
            mediaExtras.bitPerSample = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITS_PER_SAMPLE) ?: ""
        }
        mediaExtras
    }.onFailure { it.printStackTrace() }.getOrNull() ?: MediaExtras
}

private fun Cursor.getStringByColumnName(columnName: String): String? {
    return this.getStringOrNull(this.getColumnIndex(columnName))
}

private fun Cursor.getIntByColumnName(columnName: String): Int? {
    return this.getIntOrNull(this.getColumnIndex(columnName))
}

private fun Cursor.getLongByColumnName(columnName: String): Long? {
    return this.getLongOrNull(this.getColumnIndex(columnName))
}
