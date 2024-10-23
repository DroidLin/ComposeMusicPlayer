package com.music.android.lin.player.repositories

import androidx.room.withTransaction
import com.harvest.musicplayer.dao.MusicInfoDao
import com.harvest.musicplayer.dao.PlayRecordDao
import com.music.android.lin.player.MediaDatabase
import com.music.android.lin.player.dao.AlbumDao
import com.music.android.lin.player.dao.ArtistDao
import com.music.android.lin.player.dao.PlayListDao
import com.music.android.lin.player.interfaces.MediaRepository
import com.music.android.lin.player.metadata.Album
import com.music.android.lin.player.metadata.Artist
import com.music.android.lin.player.metadata.MediaExtras
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaInfoRecentPlayItem
import com.music.android.lin.player.metadata.MediaType
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayListType
import com.music.android.lin.player.metadata.PlayRecord
import com.music.android.lin.player.metadata.PlayRecord.PlayRecordResourceType.Companion.playRecordResourceType
import com.music.android.lin.player.metadata.RecentPlayItem
import com.music.android.lin.player.metadata.local.LocalAlbum
import com.music.android.lin.player.metadata.local.LocalArtist
import com.music.android.lin.player.metadata.local.LocalMusicInfo
import com.music.android.lin.player.metadata.local.LocalPlayList
import com.music.android.lin.player.metadata.local.LocalPlayRecord
import com.music.android.lin.player.metadata.toImmutable
import com.music.android.lin.player.metadata.toJson
import com.music.android.lin.player.metadata.toMutable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author liuzhongao
 * @since 2023/10/6 10:53
 */
internal class MediaRepositoryImpl constructor(
    private val mediaDatabase: MediaDatabase,
) : MediaRepository {

    private val playListDao: PlayListDao get() = this.mediaDatabase.playlistDao
    private val musicInfoDao: MusicInfoDao get() = this.mediaDatabase.musicInfoDao
    private val artistDao: ArtistDao get() = this.mediaDatabase.artistDao
    private val albumDao: AlbumDao get() = this.mediaDatabase.albumDao
    private val playRecordDao: PlayRecordDao get() = this.mediaDatabase.playRecordDao

    override fun observablePlayList(limit: Int): Flow<List<PlayList>> {
        return this.playListDao.fetchAllPlayList(limit).map { itemList ->
            itemList.map { item -> item.toPlayList() }
        }
    }

    override fun observableMusicInfoList(): Flow<List<MediaInfo>> {
        return this.musicInfoDao.fetchMusicInfoFlow()
            .map { itemList -> itemList.map { item -> item.toMediaInfo() } }
    }

    override fun observableVideoInfoList(): Flow<List<MediaInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchUserPlaylist(limit: Int): List<PlayList> {
        return this.playListDao.getAllPlaylist(limit)
            .map { localPlayList -> localPlayList.toPlayList() }
    }

    override suspend fun fetchMusicInfoList(): List<MediaInfo> {
        return this.musicInfoDao.getAllMusicInfo()
            .map { localMusicInfo -> localMusicInfo.toMediaInfo() }
    }

    override suspend fun fetchMediaInfo(mediaInfoId: String): MediaInfo? {
        return this.musicInfoDao.getMediaInfoList(listOf(mediaInfoId))
            .map { localMusicInfo -> localMusicInfo.toMediaInfo() }.firstOrNull()
    }

    override fun observableMediaInfo(mediaInfoId: String): Flow<MediaInfo?> {
        return this.musicInfoDao.fetchMediaInfoListFlow(listOf(mediaInfoId))
            .map { itemList -> itemList.firstOrNull() }
            .map { item -> item?.toMediaInfo() }
    }

    override suspend fun fetchLatestUpdatedMediaInfoList(limit: Int): List<MediaInfo> {
        return this.musicInfoDao.fetchLatestUpdatedMediaInfoList(limit = limit)
            .map { localMusicInfo -> localMusicInfo.toMediaInfo() }
    }

    override fun observableLatestUpdatedMediaInfoList(limit: Int): Flow<List<MediaInfo>> {
        return this.musicInfoDao.fetchLatestUpdatedMediaInfoListFlow(limit)
            .map { itemList -> itemList.map { item -> item.toMediaInfo() } }
    }

    override suspend fun fetchVideoInfoList(): List<MediaInfo> {
        return this.musicInfoDao.getAllVideoInfo()
            .map { localMusicInfo -> localMusicInfo.toMediaInfo() }
    }

    override suspend fun updateMediaInfo(mediaInfo: MediaInfo): Boolean {
        this.mediaDatabase.withTransaction {
            this.musicInfoDao.update(musicInfo = mediaInfo.toLocalMusicInfo())
            mediaInfo.artists.forEach { artist ->
                this.artistDao.update(artist = artist.toLocalArtist())
            }
            this.albumDao.update(album = mediaInfo.album.toLocalAlbum())
        }
        return true
    }

    override suspend fun fetchAlbumList(): List<Album> {
        return this.albumDao.getAlbum(
            albumIdList = this.musicInfoDao.getAllMusicInfo().map { it.albumId }.distinct()
        ).map { localAlbum -> localAlbum.toAlbum() }
    }

    override suspend fun getAlbum(albumId: String): Album? {
        return this.albumDao.getAlbum(albumId = albumId)?.toAlbum()
    }

    override suspend fun getAlbumMusicInfoList(albumId: String): List<MediaInfo> {
        return this.musicInfoDao.getAlbumMusicInfo(albumId = albumId)
            .map { it.toMediaInfo() }
    }

    private suspend fun LocalPlayList.toPlayList(): PlayList {
        val localPlayList = this
        return coroutineScope {
            PlayList(
                id = localPlayList.id,
                type = PlayListType.fromCode(localPlayList.typeCode),
                name = localPlayList.name,
                description = localPlayList.description,
                mediaInfoList = localPlayList.musicInfoIdList.map { musicInfoId ->
                    async {
                        this@MediaRepositoryImpl.musicInfoDao.getMusicInfo(musicInfoId)
                            ?.toMediaInfo()
                    }
                }.awaitAll().filterNotNull().toMutableList(),
                extensions = localPlayList.extensionsMap,
                updateTimeStamp = localPlayList.updateTimeStamp
            )
        }
    }

    private suspend fun LocalMusicInfo.toMediaInfo(): MediaInfo {
        return MediaInfo(
            id = this.id,
            mediaTitle = this.songTitle,
            mediaDescription = this.songDescription,
            artists = this.artistsIdList.mapNotNull artistMapNotNulL@{ artistId ->
                this@MediaRepositoryImpl.artistDao.getArtist(id = artistId)?.toArtist()
            },
            album = this@MediaRepositoryImpl.albumDao.getAlbum(albumId = this.albumId)?.toAlbum()
                ?: Album,
            coverUri = this.cover?.takeIf { it.isNotEmpty() && it != "null" },
            sourceUri = this.songSource?.takeIf { it.isNotEmpty() && it != "null" },
            updateTimeStamp = this.updateTimeStamp,
            mediaType = MediaType.fromCode(code = this.mediaType),
            mediaExtras = MediaExtras(this.mediaExtras)
        )
    }

    private fun LocalArtist.toArtist(): Artist {
        return Artist(
            id = this.id,
            name = this.name,
            description = this.description,
            numberOfAlbum = this.numberOfAlbum
        )
    }

    private fun LocalAlbum.toAlbum(): Album {
        return Album(
            id = this.id,
            albumName = this.albumName,
            albumDescription = this.albumDescription,
            publishDate = this.publishDate,
            mediaNumber = this.mediaNumber,
            coverUrl = this.coverUrl
        )
    }

    override suspend fun insertPlayList(playList: PlayList) {
        this.playListDao.insertAll(playList.toLocalPlayList())
    }

    override suspend fun updatePlayList(playList: PlayList) {
        this.playListDao.update(playList.toLocalPlayList())
    }

    override suspend fun deletePlayList(playList: PlayList) {
        this.playListDao.delete(playList.toLocalPlayList())
    }

    override suspend fun queryPlayList(playListId: String): PlayList? {
        return this.playListDao.getPlayList(playListId)?.toPlayList()
    }

    override fun observablePlayList(playListId: String): Flow<PlayList?> {
        return this.playListDao.getPlayListFlow(playListId)
            .map { localPlayList -> localPlayList?.toPlayList() }
    }

    private fun PlayList.toLocalPlayList(): LocalPlayList {
        return LocalPlayList(
            id = this.id,
            typeCode = this.type.code,
            name = this.name,
            description = this.description,
            musicInfoIdStr = this.mediaInfoList.distinctBy { it.id }.joinToString(",") { it.id },
            extensionsStr = this.extensions?.toJson() ?: "",
            updateTimeStamp = System.currentTimeMillis()
        )
    }

    override suspend fun addMusicInfoToPlayList(
        playList: PlayList,
        musicInfoIdList: List<String>
    ) {
        val mediaInfo = this.musicInfoDao.getMusicInfoList(ids = musicInfoIdList)
            .map { it.toMediaInfo() }.distinctBy { it.id }
        val mutablePlayList = playList.toMutable()
        mutablePlayList.mediaInfoList.clear()
        mutablePlayList.mediaInfoList.addAll(mediaInfo)
        this.updatePlayList(mutablePlayList.toImmutable())
    }

    override suspend fun addMusicInfoToPlayList(playListId: String, musicInfoIdList: List<String>) {
        val playList = this.queryPlayList(playListId)
        if (playList != null) {
            this.addMusicInfoToPlayList(playList, musicInfoIdList)
        }
    }

    override suspend fun insertMediaInfo(mediaInfoList: List<MediaInfo>) {
        this.mediaDatabase.withTransaction {
            mediaInfoList.forEach { musicInfo ->
                val artists = musicInfo.artists
                artists.forEach { artist ->
                    this.artistDao.insert(artist = artist.toLocalArtist())
                }
                this.albumDao.insert(album = musicInfo.album.toLocalAlbum())
                this.musicInfoDao.insert(musicInfo = musicInfo.toLocalMusicInfo())
            }
        }
    }

    private fun Artist.toLocalArtist(): LocalArtist {
        return LocalArtist(
            id = this.id,
            name = this.name,
            description = this.description,
            numberOfAlbum = numberOfAlbum,
        )
    }

    private fun Album.toLocalAlbum(): LocalAlbum {
        return LocalAlbum(
            id = this.id,
            albumName = this.albumName,
            albumDescription = this.albumDescription,
            publishDate = this.publishDate,
            mediaNumber = this.mediaNumber,
            coverUrl = this.coverUrl
        )
    }

    override suspend fun deleteMediaInfoFromPlayList(
        playList: PlayList,
        musicInfoIdList: List<String>
    ) {
        val mutablePlayList = playList.toMutable()
        mutablePlayList.mediaInfoList.removeAll { it.id in musicInfoIdList }
        this.insertPlayList(mutablePlayList.toImmutable())
    }

    override suspend fun deleteMediaInfo(mediaInfo: MediaInfo) {
        this.musicInfoDao.delete(musicInfo = mediaInfo.toLocalMusicInfo())
    }

    private fun MediaInfo.toLocalMusicInfo(): LocalMusicInfo {
        return LocalMusicInfo(
            id = this.id,
            songTitle = this.mediaTitle,
            songDescription = this.mediaDescription,
            cover = this.coverUri,
            songSource = this.sourceUri,
            artistIds = this.artists.joinToString(",") { it.id },
            albumId = this.album.id,
            updateTimeStamp = System.currentTimeMillis(),
            mediaType = this.mediaType.code,
            mediaExtras = this.mediaExtras.toJson()
        )
    }

    override suspend fun deleteMediaInfo(playList: PlayList, mediaInfo: MediaInfo) {
        return this.deleteMediaInfo(playList = playList, id = mediaInfo.id)
    }

    override suspend fun deleteMediaInfo(playList: PlayList, id: String) {
        this.insertPlayList(
            playList.toMutable()
                .also { mutablePlayList ->
                    mutablePlayList.mediaInfoList.removeAll { it.id == id }
                }
                .toImmutable()
        )
    }

    override suspend fun recordPlayRecord(mediaInfoId: String, resourceType: MediaType) {
        val localPlayRecord = LocalPlayRecord(
            id = 0,
            resourceId = mediaInfoId,
            resourceType = resourceType.playRecordResourceType.code,
            timeStamp = System.currentTimeMillis()
        )
        this.playRecordDao.recordPlayRecord(localPlayRecord)
    }

    override suspend fun recordPlayRecord(mediaInfo: MediaInfo) {
        this.recordPlayRecord(mediaInfoId = mediaInfo.id, resourceType = mediaInfo.mediaType)
    }

    override suspend fun fetchRecentPlayItems(playRecordList: List<PlayRecord>): List<RecentPlayItem> {
        return playRecordList.mapNotNull { playRecord ->
            when (playRecord.mediaResourceType) {
                PlayRecord.PlayRecordResourceType.Audio -> {
                    val mediaInfo =
                        this.musicInfoDao.getMusicInfo(playRecord.mediaResourceId)?.toMediaInfo()
                    if (mediaInfo != null) {
                        MediaInfoRecentPlayItem(playRecord, mediaInfo)
                    } else null
                }

                PlayRecord.PlayRecordResourceType.Video -> {
                    val mediaInfo =
                        this.musicInfoDao.getVideoInfo(playRecord.mediaResourceId)
                            ?.toMediaInfo()
                    if (mediaInfo != null) {
                        MediaInfoRecentPlayItem(playRecord, mediaInfo)
                    } else null
                }

                else -> null
            }
        }
    }

    override suspend fun fetchMediaInfo(idList: List<String>): List<MediaInfo> {
        return this.musicInfoDao.getMediaInfoList(idList)
            .map { mediaInfo -> mediaInfo.toMediaInfo() }
    }

    override suspend fun fetchPlayRecords(limit: Int): List<PlayRecord> {
        return this.playRecordDao.getAllPlayRecord(limit)
            .map { it.toPlayRecord() }
    }

    override fun observableRecentPlayItems(limit: Int): Flow<List<RecentPlayItem>> {
        return this.playRecordDao.fetchAllPlayRecordFlow(limit = 10)
            .map { itemList -> itemList.map { item -> item.toPlayRecord() } }
            .map { itemList -> this.fetchRecentPlayItems(itemList) }
    }

    private fun LocalPlayRecord.toPlayRecord(): PlayRecord {
        return PlayRecord(
            id = this.id,
            mediaResourceId = this.resourceId,
            mediaResourceType = PlayRecord.PlayRecordResourceType.fromCode(this.resourceType),
            recordTimeStamp = this.timeStamp
        )
    }

    private fun PlayRecord.toLocalPlayRecord(): LocalPlayRecord {
        return LocalPlayRecord(
            id = this.id,
            resourceId = this.mediaResourceId,
            resourceType = this.mediaResourceType.code,
            timeStamp = this.recordTimeStamp
        )
    }
}