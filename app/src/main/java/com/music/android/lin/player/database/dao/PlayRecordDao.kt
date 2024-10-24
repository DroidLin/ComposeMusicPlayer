package com.harvest.musicplayer.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.music.android.lin.player.database.metadata.LocalPlayRecord
import kotlinx.coroutines.flow.Flow

/**
 * @author liuzhongao
 * @since 2024/4/6 01:34
 */
@Dao
internal interface PlayRecordDao {

    @Insert(entity = LocalPlayRecord::class)
    suspend fun recordPlayRecord(localPlayRecord: LocalPlayRecord)

    @Query("select `play_record_id`, `play_record_time_stamp`, `play_record_media_resource_id`, `play_record_media_resource_type` from `table_local_play_record` where play_record_id  in (select `play_record_id` from (select `play_record_id`, max(`play_record_time_stamp`) from `table_local_play_record` group by `play_record_media_resource_id` order by `play_record_time_stamp` desc)) order by `play_record_time_stamp` desc limit :limit")
    suspend fun getAllPlayRecord(limit: Int): List<@JvmWildcard LocalPlayRecord>

    @Query("select `play_record_id`, `play_record_time_stamp`, `play_record_media_resource_id`, `play_record_media_resource_type` from `table_local_play_record` where play_record_id  in (select `play_record_id` from (select `play_record_id`, max(`play_record_time_stamp`) from `table_local_play_record` group by `play_record_media_resource_id` order by `play_record_time_stamp` desc)) order by `play_record_time_stamp` desc limit :limit")
    fun fetchAllPlayRecordFlow(limit: Int): Flow<List<@JvmWildcard LocalPlayRecord>>
}