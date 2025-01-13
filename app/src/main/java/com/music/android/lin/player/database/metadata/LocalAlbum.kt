package com.music.android.lin.player.database.metadata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.music.android.lin.player.MediaDatabase

/**
 * @author liuzhongao
 * @since 2023/10/6 00:32
 */
@Entity(
    tableName = MediaDatabase.Table.Album.NAME
)
internal class LocalAlbum(
    @PrimaryKey
    @ColumnInfo(name = MediaDatabase.Table.Album.Columns.ID) val id: String,
    @ColumnInfo(name = MediaDatabase.Table.Album.Columns.NAME) val albumName: String,
    @ColumnInfo(name = MediaDatabase.Table.Album.Columns.DESCRIPTION) val albumDescription: String,
    @ColumnInfo(name = MediaDatabase.Table.Album.Columns.PUBLISH_DATE) val publishDate: Long,
    @ColumnInfo(name = MediaDatabase.Table.Album.Columns.MEDIA_NUMBER) val mediaNumber: Int,
    @ColumnInfo(name = MediaDatabase.Table.Album.Columns.COVER_URL, defaultValue = "\'null\'") val coverUrl: String
)