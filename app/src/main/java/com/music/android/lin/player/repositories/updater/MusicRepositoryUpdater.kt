package com.music.android.lin.player.repositories.updater

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.music.android.lin.player.metadata.Album
import com.music.android.lin.player.metadata.Artist
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.interfaces.MediaRepository
import com.music.android.lin.player.metadata.MediaType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File

/**
 * @author liuzhongao
 * @since 2023/10/15 8:30 PM
 */
internal class MusicRepositoryUpdater(
    private val filterDuration: Long,
    private val context: Context,
) : RepositoryUpdater {

    override suspend fun updateRepository(mediaRepository: MediaRepository) {
        val timestamp = System.currentTimeMillis()
        val musicInfoList = this.queryMusicInfoAndTransform { cursor ->
            val musicInfoArtistsDeferred = async {
                cursor.artistId?.let { artistId -> fetchArtistList(artistId = artistId) }
                    ?: emptyList<Artist>()
            }
            val musicInfoAlbumDeferred = async {
                cursor.albumId?.let { albumId -> fetchAlbumInfo(albumId = albumId) } ?: Album
            }
            val songSourceUri = cursor.songLocalUri?.let { data ->
                kotlin.runCatching {
                    Uri.fromFile(File(data))
                }.onFailure { it.printStackTrace() }.getOrNull()
            }

            val mediaExtras = async { decodeMediaExtras(cursor.songLocalUri) }
            val musicInfoArtist = musicInfoArtistsDeferred.await()
            val musicInfoAlbum = musicInfoAlbumDeferred.await()
            val coverUri = musicInfoAlbum.coverUrl
            MediaInfo(
                id = cursor.musicInfoId,
                mediaTitle = cursor.musicInfoTitle,
                mediaDescription = cursor.musicInfoDescription,
                artists = musicInfoArtist,
                album = musicInfoAlbum,
                coverUri = coverUri,
                sourceUri = songSourceUri.toString(),
                updateTimeStamp = timestamp,
                mediaType = MediaType.Audio,
                mediaExtras = mediaExtras.await()
            )
        }
        mediaRepository.insertMediaInfo(mediaInfoList = musicInfoList)
    }

    private suspend fun queryMusicInfoAndTransform(transform: suspend CoroutineScope.(MusicCursorRecord) -> MediaInfo): List<MediaInfo> {
        return this.context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            "${MediaStore.Audio.AudioColumns.DURATION} > ?",
            arrayOf(this.filterDuration.toString()),
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        )?.use { cursor ->
            coroutineScope {
                val mutableList = mutableListOf<Deferred<MediaInfo>>()
                while (cursor.moveToNext()) {
                    val musicInfoId =
                        cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID))
                            ?: ""
                    val musicInfoTitle =
                        cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                            ?: ""
                    val musicInfoDescription =
                        cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.COMPOSER))
                            ?: ""
                    val artistId =
                        cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID))
                            ?: ""
                    val albumId =
                        cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                            ?: ""
                    val songLocalUri =
                        cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
                            ?: ""

                    val musicCursorRecord = MusicCursorRecord(
                        musicInfoId = musicInfoId,
                        musicInfoTitle = musicInfoTitle,
                        musicInfoDescription = musicInfoDescription,
                        artistId = artistId,
                        albumId = albumId,
                        songLocalUri = songLocalUri,
                    )
                    mutableList += async(Dispatchers.IO) { transform(musicCursorRecord) }
                }
                mutableList.awaitAll()
            }
        } ?: emptyList()
    }
}