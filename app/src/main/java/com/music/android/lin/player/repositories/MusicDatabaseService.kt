package com.music.android.lin.player.repositories

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.music.android.lin.player.MediaDatabase
import com.music.android.lin.player.database.MediaRepository

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
            .addMigrations(MigrateFromOneToTwo, MigrateFromTwoToThree, MigrateFromThreeToFour, MigrateFrom4To5, MigrateFrom5To6)
            .build()
    }

    override val mediaRepository: MediaRepository by lazy {
        MediaRepository(mediaDatabase = this.mediaDatabase)
    }

    override fun release() {
        this.mediaDatabase.close()
    }
}

private object MigrateFromOneToTwo : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table `table_local_album` add column `album_cover_url` TEXT not null default 'null';")
    }
}

private object MigrateFromTwoToThree : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table `table_local_artist` add column `local_artist_number_of_album` integer not null default 0;")
    }
}

private object MigrateFromThreeToFour : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table `table_local_music_info` add column `music_info_media_type` integer not null default 1")
    }
}

private object MigrateFrom4To5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("create table if not exists `table_local_play_record` (`play_record_id` integer primary key autoincrement not null, `play_record_time_stamp` integer not null, `play_record_media_info_id` text not null, foreign key(`play_record_media_info_id`) references `table_local_music_info`(`music_info_id`) on update no action on delete no action)")
        database.execSQL("create index if not exists `index_table_local_play_record_play_record_media_info_id` on `table_local_play_record` (`play_record_media_info_id`)")
    }
}

private object MigrateFrom5To6 : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("alter table `table_local_music_info` add column `music_info_media_extras` text not null default '';")
    }
}