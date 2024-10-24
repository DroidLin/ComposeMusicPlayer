package com.music.android.lin.player.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.music.android.lin.player.database.metadata.LocalPlayList
import kotlinx.coroutines.flow.Flow

/**
 * @author liuzhongao
 * @since 2023/10/6 10:31
 */
@Dao
internal interface PlayListDao {

    @Insert(onConflict = OnConflictStrategy.NONE)
    suspend fun insertAll(vararg playList: LocalPlayList)

    @Delete
    suspend fun delete(playList: LocalPlayList): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(playList: LocalPlayList): Int

    @Query(value = "select * from table_local_playlist order by playlist_update_timestamp desc limit :limit ")
    suspend fun getAllPlaylist(limit: Int): List<@JvmWildcard LocalPlayList>

    @Query(value = "select * from table_local_playlist order by playlist_update_timestamp desc limit :limit ")
    fun fetchAllPlayList(limit: Int): Flow<List<@JvmWildcard LocalPlayList>>

    @Query(value = "select * from table_local_playlist where playlist_id = :playListId")
    suspend fun getPlayList(playListId: String): LocalPlayList?

    @Query(value = "select * from table_local_playlist where playlist_id = :playListId")
    fun getPlayListFlow(playListId: String): Flow<LocalPlayList?>
}