package com.harvest.musicplayer.repositories.updater

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.music.android.lin.player.metadata.MediaExtras
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.interfaces.MediaRepository
import com.music.android.lin.player.metadata.Album
import com.music.android.lin.player.metadata.Artist
import com.music.android.lin.player.metadata.MediaType
import com.music.android.lin.player.repositories.updater.RepositoryUpdater
import com.music.android.lin.player.repositories.updater.VideoCursorRecord
import com.music.android.lin.player.repositories.updater.decodeMediaExtras
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.io.FileOutputStream

/**
 * @author liuzhongao
 * @since 2023/12/12 12:09 AM
 */
internal class VideoRepositoryUpdater(
    private val context: Context,
    private val filterDuration: Long,
) : RepositoryUpdater {

    private val coverCacheDir: File
        get() = context.getExternalFilesDir(VIDEO_INFO_COVER)
            ?: context.getDir(VIDEO_INFO_COVER, Context.MODE_PRIVATE)

    override suspend fun updateRepository(mediaRepository: MediaRepository) {
        val videoInfoList = this.fetchVideoResource()
        mediaRepository.insertMediaInfo(mediaInfoList = videoInfoList)
    }

    private suspend fun fetchVideoResource(): List<MediaInfo> {
        val videoCursorRecordList = this.context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER)?.use { cursor ->
            val arrayList = arrayListOf<VideoCursorRecord>()
            while (cursor.moveToNext()) {
                arrayList += VideoCursorRecord(
                    id = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID)) ?: "",
                    title = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE)) ?: "",
                    description = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DESCRIPTION)) ?: "",
                    artistName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST)) ?: "",
                    albumName = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns.ALBUM)) ?: "",
                    sourceUri = cursor.getStringOrNull(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)) ?: "",
                )
            }
            arrayList
        } ?: emptyList<VideoCursorRecord>()
        val timestamp = System.currentTimeMillis()
        return videoCursorRecordList.map { videoCursorRecord ->
            coroutineScope {
                async {
                    val videoCoverUri = async {
                        val videoCoverCacheFile = File(this@VideoRepositoryUpdater.coverCacheDir, "video_cover_${videoCursorRecord.id}.dat")
                        val sourceUri = videoCursorRecord.sourceUri
                        val videoCoverBitmap = if (!sourceUri.isNullOrEmpty()) {
                            ThumbnailUtils.createVideoThumbnail(sourceUri, MediaStore.Images.Thumbnails.MINI_KIND)
                        } else null
                        kotlin.runCatching {
                            if (videoCoverBitmap != null) {
                                videoCoverBitmap.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(videoCoverCacheFile))
                                Uri.fromFile(videoCoverCacheFile)
                            } else null
                        }.onFailure { it.printStackTrace() }.getOrNull()
                    }
                    val mediaExtras = async { decodeMediaExtras(videoCursorRecord.sourceUri) }
                    MediaInfo(
                        id = videoCursorRecord.id,
                        mediaTitle = videoCursorRecord.title,
                        mediaDescription = videoCursorRecord.description,
                        artists = listOf(
                            Artist(
                                id = "video_artist_${videoCursorRecord.id}",
                                name = videoCursorRecord.artistName ?: "",
                                description = "",
                                numberOfAlbum = 1
                            )
                        ),
                        album = Album(
                            id = "video_album_${videoCursorRecord.id}",
                            albumName = videoCursorRecord.albumName ?: "",
                            albumDescription = "",
                            publishDate = 0,
                            mediaNumber = 1,
                            coverUrl = videoCoverUri.await()?.toString() ?: "null"
                        ),
                        coverUri = videoCoverUri.await()?.toString(),
                        sourceUri = videoCursorRecord.sourceUri,
                        updateTimeStamp = timestamp,
                        mediaType = MediaType.Video,
                        mediaExtras = mediaExtras.await() ?: MediaExtras
                    )
                }
            }
        }.awaitAll()
    }

    companion object {
        private const val VIDEO_INFO_COVER = "music_info_cover"
    }
}