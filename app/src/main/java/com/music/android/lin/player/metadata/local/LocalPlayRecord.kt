package com.music.android.lin.player.metadata.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author liuzhongao
 * @since 2024/4/6 01:19
 */
@Entity(tableName = "table_local_play_record")
internal class LocalPlayRecord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "play_record_id") val id: Long,
    @ColumnInfo(name = "play_record_time_stamp") val timeStamp: Long,
    @ColumnInfo(name = "play_record_media_resource_id") val resourceId: String,
    @ColumnInfo(name = "play_record_media_resource_type") val resourceType: Int,
)