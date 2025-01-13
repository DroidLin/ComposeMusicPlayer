package com.music.android.lin.player.database.metadata

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.music.android.lin.player.MediaDatabase

@Entity(
    tableName = MediaDatabase.Table.PlayListMediaInfo.NAME,
    primaryKeys = [
        MediaDatabase.Table.PlayListMediaInfo.Columns.PLAYLIST_ID,
        MediaDatabase.Table.PlayListMediaInfo.Columns.MEDIA_INFO_ID
    ]
)
data class LocalPlayListMediaInfoRecord(
    @ColumnInfo(name = MediaDatabase.Table.PlayListMediaInfo.Columns.PLAYLIST_ID) val playlistId: String,
    @ColumnInfo(name = MediaDatabase.Table.PlayListMediaInfo.Columns.MEDIA_INFO_ID) val mediaInfoId: String,
    @ColumnInfo(name = MediaDatabase.Table.PlayListMediaInfo.Columns.INSERT_TIMESTAMP) val timestamp: Long = System.currentTimeMillis(),
)
