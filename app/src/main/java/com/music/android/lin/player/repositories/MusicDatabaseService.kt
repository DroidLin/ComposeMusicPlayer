package com.music.android.lin.player.repositories

import android.content.Context
import androidx.room.Room
import com.music.android.lin.player.MediaDatabase
import com.music.android.lin.player.database.MediaRepository
import org.koin.core.annotation.Factory

/**
 * @author liuzhongao
 * @since 2023/10/13 15:13
 */
class MusicDatabaseService(
    private val applicationContext: Context,
    private val accessToken: String
) : DatabaseService {

    private val mediaDatabase: MediaDatabase by lazy {
        Room.databaseBuilder(
            context = this.applicationContext,
            klass = MediaDatabase::class.java,
            name = "music_database_${accessToken}"
        )
            .allowMainThreadQueries()
            .build()
    }

    override val mediaRepository: MediaRepository by lazy {
        MediaRepository(mediaDatabase = this.mediaDatabase)
    }

    override fun release() {
        this.mediaDatabase.close()
    }
}