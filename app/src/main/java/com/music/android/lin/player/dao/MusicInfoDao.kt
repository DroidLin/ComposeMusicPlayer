package com.harvest.musicplayer.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.harvest.musicplayer.metadata.local.LocalMusicInfo
import kotlinx.coroutines.flow.Flow

/**
 * @author liuzhongao
 * @since 2023/10/6 10:09
 */
@Dao
internal interface MusicInfoDao {

    @Insert(entity = LocalMusicInfo::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg musicInfo: LocalMusicInfo)

    @Insert(entity = LocalMusicInfo::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(musicInfo: LocalMusicInfo)

    @Delete(entity = LocalMusicInfo::class)
    suspend fun delete(musicInfo: LocalMusicInfo): Int

    @Update(entity = LocalMusicInfo::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(musicInfo: LocalMusicInfo): Int

    @Query(value = "select * from table_local_music_info where music_info_id in (:ids)")
    suspend fun getMediaInfoList(ids: List<String>): List<@JvmWildcard LocalMusicInfo>

    @Query(value = "select * from table_local_music_info where music_info_id in (:ids)")
    fun fetchMediaInfoListFlow(ids: List<String>): Flow<List<@JvmWildcard LocalMusicInfo>>

    @Query(value = "select * from table_local_music_info order by music_info_update_timestamp desc limit :limit")
    suspend fun fetchLatestUpdatedMediaInfoList(limit: Int): List<@JvmWildcard LocalMusicInfo>

    @Query(value = "select * from table_local_music_info order by music_info_update_timestamp desc limit :limit")
    fun fetchLatestUpdatedMediaInfoListFlow(limit: Int): Flow<List<@JvmWildcard LocalMusicInfo>>

    /**
     * music_info_media_type可参考[com.harvest.musicplayer.MediaType]
     */
    @Query(value = "select * from table_local_music_info where music_info_media_type = 1")
    suspend fun getAllMusicInfo(): List<@JvmWildcard LocalMusicInfo>

    /**
     * music_info_media_type可参考[com.harvest.musicplayer.MediaType]
     */
    @Query(value = "select * from table_local_music_info where music_info_media_type = 1")
    fun fetchMusicInfoFlow(): Flow<List<@JvmWildcard LocalMusicInfo>>

    /**
     * music_info_media_type可参考[com.harvest.musicplayer.MediaType]
     */
    @Query(value = "select * from table_local_music_info where music_info_media_type = 2")
    suspend fun getAllVideoInfo(): List<@JvmWildcard LocalMusicInfo>

    @Query(value = "select * from table_local_music_info where music_info_id in (:ids) and music_info_media_type = 1")
    suspend fun getMusicInfoList(ids: List<String>): List<@JvmWildcard LocalMusicInfo>

    @Query(value = "select * from table_local_music_info where music_info_id = :id and music_info_media_type = 1 limit 1")
    suspend fun getMusicInfo(id: String): LocalMusicInfo?

    @Query(value = "select * from table_local_music_info where music_info_id = :id and music_info_media_type = 2 limit 1")
    suspend fun getVideoInfo(id: String): LocalMusicInfo?

    @Query(value = "select * from table_local_music_info where music_info_id = :id and music_info_media_type = 1 limit 1")
    suspend fun isMusicInfoExist(id: String): LocalMusicInfo?

    @Query(value = "select * from table_local_music_info where music_info_album_id = :albumId and music_info_media_type = 1 order by music_info_update_timestamp desc")
    suspend fun getAlbumMusicInfo(albumId: String): List<@JvmWildcard LocalMusicInfo>
}