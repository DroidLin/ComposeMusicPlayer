package com.music.android.lin.player.database.metadata

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "table_local_playlist_media_info",
    primaryKeys = ["playlist_id", "media_info_id"]
)
data class LocalPlayListMediaInfoRecord(
    @ColumnInfo(name = "playlist_id") val playlistId: String,
    @ColumnInfo(name = "media_info_id") val mediaInfoId: String,
    @ColumnInfo(name = "insertTimestamp") val timestamp: Long = System.currentTimeMillis()
)
