package com.music.android.lin.player.database.metadata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.music.android.lin.player.MediaDatabase

/**
 * @author liuzhongao
 * @since 2023/10/6 10:21
 */
@Entity(
    tableName = MediaDatabase.Table.PlayList.NAME
)
internal class LocalPlayList(
    @PrimaryKey
    @ColumnInfo(name = MediaDatabase.Table.PlayList.Columns.ID) val id: String,
    @ColumnInfo(name = MediaDatabase.Table.PlayList.Columns.TYPE) val typeCode: Int,
    @ColumnInfo(name = MediaDatabase.Table.PlayList.Columns.NAME) val name: String,
    @ColumnInfo(name = MediaDatabase.Table.PlayList.Columns.COVER) val cover: String,
    @ColumnInfo(name = MediaDatabase.Table.PlayList.Columns.DESCRIPTION) val description: String,
    @ColumnInfo(name = MediaDatabase.Table.PlayList.Columns.EXTENSIONS_STR) val extensionsStr: String,
    @ColumnInfo(name = MediaDatabase.Table.PlayList.Columns.UPDATE_TIMESTAMP) val updateTimeStamp: Long
)