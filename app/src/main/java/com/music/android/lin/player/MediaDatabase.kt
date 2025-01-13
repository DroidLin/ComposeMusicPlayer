package com.music.android.lin.player

import androidx.room.Database
import androidx.room.RoomDatabase
import com.music.android.lin.player.database.dao.AlbumDao
import com.music.android.lin.player.database.dao.ArtistDao
import com.music.android.lin.player.database.dao.MusicInfoDao
import com.music.android.lin.player.database.dao.PlayListDao
import com.music.android.lin.player.database.dao.PlayListMediaInfoDao
import com.music.android.lin.player.database.dao.PlayRecordDao
import com.music.android.lin.player.database.metadata.LocalAlbum
import com.music.android.lin.player.database.metadata.LocalArtist
import com.music.android.lin.player.database.metadata.LocalMusicInfo
import com.music.android.lin.player.database.metadata.LocalPlayList
import com.music.android.lin.player.database.metadata.LocalPlayListMediaInfoRecord
import com.music.android.lin.player.database.metadata.LocalPlayRecord

/**
 * @author liuzhongao
 * @since 2023/10/6 00:20
 */
@Database(
    entities = [
        LocalArtist::class,
        LocalMusicInfo::class,
        LocalAlbum::class,
        LocalPlayList::class,
        LocalPlayRecord::class,
        LocalPlayListMediaInfoRecord::class
    ],
    version = 1,
    exportSchema = false,
)
internal abstract class MediaDatabase : RoomDatabase() {

    abstract val playlistDao: PlayListDao

    abstract val artistDao: ArtistDao

    abstract val musicInfoDao: MusicInfoDao

    abstract val albumDao: AlbumDao

    abstract val playRecordDao: PlayRecordDao

    abstract val playlistMediaInfoDao: PlayListMediaInfoDao

    object Table {
        object PlayList {
            const val NAME = "table_local_playlist"

            object Columns {
                const val ID = "playList_id"
                const val TYPE = "playlist_type"
                const val NAME = "playlist_name"
                const val COVER = "playlist_cover"
                const val DESCRIPTION = "playlist_description"
                const val EXTENSIONS_STR = "playlist_extensions_str"
                const val UPDATE_TIMESTAMP = "playlist_update_timestamp"
            }
        }

        object Artist {
            const val NAME = "table_local_artist"

            object Columns {
                const val ID = "local_artist_id"
                const val NAME = "local_artist_name"
                const val DESCRIPTION = "local_artist_description"
                const val NUMBER_OF_ALBUM = "local_artist_number_of_album"
            }
        }

        object MusicInfo {
            const val NAME = "table_local_music_info"

            object Columns {
                const val ID = "music_info_id"
                const val TITLE = "music_info_title"
                const val DESCRIPTION = "music_info_description"
                const val COVER = "music_info_cover"
                const val SONG_SOURCE = "music_info_song_source"
                const val ARTISTS_IDS = "music_info_artists_ids"
                const val ALBUM_ID = "music_info_album_id"
                const val UPDATE_TIMESTAMP = "music_info_update_timestamp"
                const val MEDIA_TYPE = "music_info_media_type"
                const val MEDIA_EXTRAS = "music_info_media_extras"
            }
        }

        object Album {
            const val NAME = "table_local_album"
            object Columns {
                const val ID = "album_id"
                const val NAME = "album_name"
                const val DESCRIPTION = "album_description"
                const val PUBLISH_DATE = "album_publish_date"
                const val MEDIA_NUMBER = "album_media_number"
                const val COVER_URL = "album_cover_url"
            }
        }

        object PlayRecord {
            const val NAME = "table_local_play_record"

            object Columns {
                const val ID = "play_record_id"
                const val TIMESTAMP= "play_record_time_stamp"
                const val RESOURCE_ID = "play_record_media_resource_id"
                const val RESOURCE_TYPE = "play_record_media_resource_type"
            }
        }

        object PlayListMediaInfo {
            const val NAME = "table_local_playlist_media_info"
            object Columns {
                const val PLAYLIST_ID = "playlist_id"
                const val MEDIA_INFO_ID = "media_info_id"
                const val INSERT_TIMESTAMP = "insertTimestamp"
            }
        }
    }
}