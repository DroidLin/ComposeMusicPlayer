package com.music.android.lin.player.database.metadata

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.music.android.lin.player.MediaDatabase

/**
 * @author liuzhongao
 * @since 2023/10/5 23:00
 */
@Entity(
    tableName = MediaDatabase.Table.MusicInfo.NAME,
    foreignKeys = [
        ForeignKey(
            entity = LocalAlbum::class,
            parentColumns = [MediaDatabase.Table.Album.Columns.ID],
            childColumns = [MediaDatabase.Table.MusicInfo.Columns.ALBUM_ID]
        )
    ]
)
internal class LocalMusicInfo(
    @PrimaryKey
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.ID, index = true) val id: String,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.TITLE) val songTitle: String,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.DESCRIPTION) val songDescription: String,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.COVER, defaultValue = "") val cover: String?,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.SONG_SOURCE, defaultValue = "") val songSource: String?,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.ARTISTS_IDS) val artistIds: String,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.ALBUM_ID, index = true) val albumId: String,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.UPDATE_TIMESTAMP) val updateTimeStamp: Long,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.MEDIA_TYPE) val mediaType: Int,
    @ColumnInfo(name = MediaDatabase.Table.MusicInfo.Columns.MEDIA_EXTRAS, defaultValue = "") val mediaExtras: String
)