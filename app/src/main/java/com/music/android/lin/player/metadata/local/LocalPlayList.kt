package com.harvest.musicplayer.metadata.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author liuzhongao
 * @since 2023/10/6 10:21
 */
@Entity(
    tableName = "table_local_playlist"
)
internal class LocalPlayList(
    @PrimaryKey
    @ColumnInfo(name = "playList_id") val id: String,
    @ColumnInfo(name = "playlist_type") val typeCode: Int,
    @ColumnInfo(name = "playlist_name") val name: String,
    @ColumnInfo(name = "playlist_description") val description: String,
    @ColumnInfo(name = "playlist_music_info_ids") val musicInfoIdStr: String,
    @ColumnInfo(name = "playlist_extensions_str") val extensionsStr: String,
    @ColumnInfo(name = "playlist_update_timestamp") val updateTimeStamp: Long
)