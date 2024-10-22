package com.harvest.musicplayer.metadata.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * @author liuzhongao
 * @since 2023/10/5 23:00
 */

@Entity(
    tableName = "table_local_music_info",
    foreignKeys = [
        ForeignKey(
            entity = LocalAlbum::class,
            parentColumns = ["album_id"],
            childColumns = ["music_info_album_id"]
        )
    ]
)
internal class LocalMusicInfo(
    @PrimaryKey @ColumnInfo(name = "music_info_id", index = true) val id: String,
    @ColumnInfo(name = "music_info_title") val songTitle: String,
    @ColumnInfo(name = "music_info_description") val songDescription: String,
    @ColumnInfo(name = "music_info_cover", defaultValue = "") val cover: String?,
    @ColumnInfo(name = "music_info_song_source", defaultValue = "") val songSource: String?,
    @ColumnInfo(name = "music_info_artists_ids") val artistIds: String,
    @ColumnInfo(name = "music_info_album_id", index = true) val albumId: String,
    @ColumnInfo(name = "music_info_update_timestamp") val updateTimeStamp: Long,
    @ColumnInfo(name = "music_info_media_type") val mediaType: Int,
    @ColumnInfo(name = "music_info_media_extras", defaultValue = "") val mediaExtras: String
)