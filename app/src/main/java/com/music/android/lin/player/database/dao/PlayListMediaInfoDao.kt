package com.music.android.lin.player.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.music.android.lin.player.database.metadata.LocalPlayListMediaInfoRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayListMediaInfoDao {

    @Insert
    suspend fun insertRecord(record: LocalPlayListMediaInfoRecord)

    @Upsert
    suspend fun insertOrUpdate(record: LocalPlayListMediaInfoRecord)

    @Upsert
    suspend fun insertOrUpdateList(recordList: List<LocalPlayListMediaInfoRecord>)

    @Delete
    suspend fun deleteRecord(record: LocalPlayListMediaInfoRecord)

    @Delete
    suspend fun deleteRecord(record: List<LocalPlayListMediaInfoRecord>)

    @Query("select * from table_local_playlist_media_info where playlist_id = :playListId order by insertTimestamp desc")
    suspend fun fetchMediaInfoAboutPlayList(playListId: String): List<LocalPlayListMediaInfoRecord>

    @Query("select * from table_local_playlist_media_info where media_info_id = :mediaInfoId")
    suspend fun fetchRecordByMediaInfoId(mediaInfoId: String): List<LocalPlayListMediaInfoRecord>

    @Query("select count(*) from table_local_playlist_media_info where playlist_id = :playlistId")
    suspend fun fetchCountOfPlayList(playlistId: String): Int

    @Query("select * from table_local_playlist_media_info where playlist_id = :playListId order by insertTimestamp desc")
    fun observableRecordAboutPlayList(playListId: String): Flow<List<LocalPlayListMediaInfoRecord>>
}