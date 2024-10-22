package com.harvest.musicplayer.repositories.updater

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import com.harvest.common.services.KServiceFacade
import com.harvest.musicplayer.MediaExtras
import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.MediaRepository
import com.harvest.musicplayer.MediaType
import com.harvest.musicplayer.repositories.buildAlbum
import com.harvest.musicplayer.repositories.buildArtist
import com.harvest.musicplayer.repositories.buildMediaInfo
import com.harvest.statistic.interfaces.IStatistic
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
    private val iStatistic: IStatistic = KServiceFacade[IStatistic::class.java]
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
                    buildMediaInfo(
                        id = videoCursorRecord.id,
                        mediaTitle = videoCursorRecord.title,
                        mediaDescription = videoCursorRecord.description,
                        artists = listOf(
                            buildArtist(
                                id = "video_artist_${videoCursorRecord.id}",
                                name = videoCursorRecord.artistName ?: "",
                                description = "",
                                numberOfAlbum = 1
                            )
                        ),
                        album = buildAlbum(
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