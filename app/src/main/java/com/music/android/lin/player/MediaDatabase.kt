package com.harvest.musicplayer

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harvest.musicplayer.dao.AlbumDao
import com.harvest.musicplayer.dao.ArtistDao
import com.harvest.musicplayer.dao.MusicInfoDao
import com.harvest.musicplayer.dao.PlayListDao
import com.harvest.musicplayer.dao.PlayRecordDao
import com.harvest.musicplayer.metadata.local.LocalAlbum
import com.harvest.musicplayer.metadata.local.LocalArtist
import com.harvest.musicplayer.metadata.local.LocalMusicInfo
import com.harvest.musicplayer.metadata.local.LocalPlayList
import com.harvest.musicplayer.metadata.local.LocalPlayRecord

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
    ],
    version = 6,
    exportSchema = false,
)
internal abstract class MediaDatabase : RoomDatabase() {

    abstract val playlistDao: PlayListDao

    abstract val artistDao: ArtistDao

    abstract val musicInfoDao: MusicInfoDao

    abstract val albumDao: AlbumDao

    abstract val playRecordDao: PlayRecordDao
}