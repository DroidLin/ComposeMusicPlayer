package com.music.android.lin.player.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.music.android.lin.player.database.metadata.LocalPlayListMediaInfoRecord

@Dao
interface PlayListMediaInfoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRecord(record: LocalPlayListMediaInfoRecord)

    @Upsert
    suspend fun insertOrUpdate(record: LocalPlayListMediaInfoRecord)

    @Upsert
    suspend fun insertOrUpdateList(recordList: List<LocalPlayListMediaInfoRecord>)

    @Delete
    suspend fun deleteRecord(record: LocalPlayListMediaInfoRecord)

    @Query("select * from table_local_playlist_media_info where playlist_id = :playListId order by insertTimestamp desc")
    suspend fun fetchMediaInfoAboutPlayList(playListId: String): List<LocalPlayListMediaInfoRecord>
}