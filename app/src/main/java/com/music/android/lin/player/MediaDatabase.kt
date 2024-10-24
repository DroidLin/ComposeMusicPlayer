package com.music.android.lin.player

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harvest.musicplayer.dao.MusicInfoDao
import com.harvest.musicplayer.dao.PlayRecordDao
import com.music.android.lin.player.database.dao.AlbumDao
import com.music.android.lin.player.database.dao.ArtistDao
import com.music.android.lin.player.database.dao.PlayListDao
import com.music.android.lin.player.database.metadata.LocalAlbum
import com.music.android.lin.player.database.metadata.LocalArtist
import com.music.android.lin.player.database.metadata.LocalMusicInfo
import com.music.android.lin.player.database.metadata.LocalPlayList
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