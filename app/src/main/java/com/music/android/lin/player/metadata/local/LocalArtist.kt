package com.music.android.lin.player.metadata.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author liuzhongao
 * @since 2023/10/5 23:13
 */
@Entity(
    tableName = "table_local_artist"
)
internal class LocalArtist(
    @PrimaryKey
    @ColumnInfo(name = "local_artist_id") val id: String,
    @ColumnInfo(name = "local_artist_name") val name: String,
    @ColumnInfo(name = "local_artist_description") val description: String,
    @ColumnInfo(name = "local_artist_number_of_album", defaultValue = "0") val numberOfAlbum: Int
)