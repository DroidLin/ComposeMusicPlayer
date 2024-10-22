package com.harvest.musicplayer.metadata.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author liuzhongao
 * @since 2023/10/6 00:32
 */
@Entity(
    tableName = "table_local_album"
)
internal class LocalAlbum(
    @PrimaryKey
    @ColumnInfo(name = "album_id") val id: String,
    @ColumnInfo(name = "album_name") val albumName: String,
    @ColumnInfo(name = "album_description") val albumDescription: String,
    @ColumnInfo(name = "album_publish_date") val publishDate: Long,
    @ColumnInfo(name = "album_media_number") val mediaNumber: Int,
    @ColumnInfo(name = "album_cover_url", defaultValue = "\'null\'") val coverUrl: String
)