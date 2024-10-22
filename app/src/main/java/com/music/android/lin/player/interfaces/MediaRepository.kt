package com.music.android.lin.player.interfaces

import com.music.android.lin.player.interfaces.Album
import kotlinx.coroutines.flow.Flow

/**
 * @author liuzhongao
 * @since 2023/10/5 18:15
 */
interface MediaRepository {

    fun observablePlayList(limit: Int = Int.MAX_VALUE): Flow<List<PlayList>>

    fun observableMusicInfoList(): Flow<List<MediaInfo>>

    fun observableVideoInfoList(): Flow<List<MediaInfo>>

    suspend fun fetchUserPlaylist(limit: Int = Int.MAX_VALUE): List<PlayList>

    suspend fun fetchMusicInfoList(): List<MediaInfo>

    suspend fun fetchVideoInfoList(): List<MediaInfo>

    suspend fun fetchMediaInfo(mediaInfoId: String): MediaInfo?

    fun observableMediaInfo(mediaInfoId: String): Flow<MediaInfo?>

    suspend fun fetchLatestUpdatedMediaInfoList(limit: Int): List<MediaInfo>

    fun observableLatestUpdatedMediaInfoList(limit: Int): Flow<List<MediaInfo>>

    suspend fun updateMediaInfo(mediaInfo: MediaInfo): Boolean

    suspend fun insertPlayList(playList: PlayList)

    suspend fun updatePlayList(playList: PlayList)

    suspend fun deletePlayList(playList: PlayList)

    suspend fun queryPlayList(playListId: String): PlayList?

    fun observablePlayList(playListId: String): Flow<PlayList?>

    suspend fun addMusicInfoToPlayList(playList: PlayList, musicInfoIdList: List<String>)

    suspend fun addMusicInfoToPlayList(playListId: String, musicInfoIdList: List<String>)

    suspend fun insertMediaInfo(mediaInfoList: List<MediaInfo>)

    suspend fun deleteMediaInfoFromPlayList(playList: PlayList, musicInfoIdList: List<String>)

    /**
     * 指定删除某一首歌，会同步删除所有与其关联的歌单
     */
    suspend fun deleteMediaInfo(mediaInfo: MediaInfo)

    suspend fun deleteMediaInfo(playList: PlayList, mediaInfo: MediaInfo)

    suspend fun deleteMediaInfo(playList: PlayList, id: String)

    suspend fun fetchAlbumList(): List<Album>

    suspend fun getAlbum(albumId: String): Album?

    suspend fun getAlbumMusicInfoList(albumId: String): List<MediaInfo>

    suspend fun recordPlayRecord(mediaInfoId: String, resourceType: MediaType)

    suspend fun recordPlayRecord(mediaInfo: MediaInfo)

    suspend fun fetchPlayRecords(limit: Int = Int.MAX_VALUE): List<PlayRecord>

    fun observableRecentPlayItems(limit: Int = Int.MAX_VALUE): Flow<List<RecentPlayItem>>

    suspend fun fetchRecentPlayItems(playRecordList: List<PlayRecord>): List<RecentPlayItem>

    suspend fun fetchMediaInfo(idList: List<String>): List<MediaInfo>
}