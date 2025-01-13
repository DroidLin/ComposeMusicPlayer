package com.music.android.lin.player.database.metadata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.music.android.lin.player.MediaDatabase

/**
 * @author liuzhongao
 * @since 2023/10/5 23:13
 */
@Entity(
    tableName = MediaDatabase.Table.Artist.NAME
)
internal class LocalArtist(
    @PrimaryKey
    @ColumnInfo(name = MediaDatabase.Table.Artist.Columns.ID) val id: String,
    @ColumnInfo(name = MediaDatabase.Table.Artist.Columns.NAME) val name: String,
    @ColumnInfo(name = MediaDatabase.Table.Artist.Columns.DESCRIPTION) val description: String,
    @ColumnInfo(name = MediaDatabase.Table.Artist.Columns.NUMBER_OF_ALBUM, defaultValue = "0") val numberOfAlbum: Int
)