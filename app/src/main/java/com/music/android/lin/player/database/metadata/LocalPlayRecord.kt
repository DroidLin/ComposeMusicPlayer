package com.music.android.lin.player.database.metadata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.music.android.lin.player.MediaDatabase

/**
 * @author liuzhongao
 * @since 2024/4/6 01:19
 */
@Entity(tableName = MediaDatabase.Table.PlayRecord.NAME)
internal class LocalPlayRecord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = MediaDatabase.Table.PlayRecord.Columns.ID) val id: Long,
    @ColumnInfo(name = MediaDatabase.Table.PlayRecord.Columns.TIMESTAMP) val timeStamp: Long,
    @ColumnInfo(name = MediaDatabase.Table.PlayRecord.Columns.RESOURCE_ID) val resourceId: String,
    @ColumnInfo(name = MediaDatabase.Table.PlayRecord.Columns.RESOURCE_TYPE) val resourceType: Int,
)