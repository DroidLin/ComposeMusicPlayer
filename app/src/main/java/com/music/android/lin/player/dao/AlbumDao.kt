package com.harvest.musicplayer.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.harvest.musicplayer.metadata.local.LocalAlbum

/**
 * @author liuzhongao
 * @since 2023/10/6 10:45
 */
@Dao
internal interface AlbumDao {

    @Insert(entity = LocalAlbum::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg album: LocalAlbum)

    @Insert(entity = LocalAlbum::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(album: LocalAlbum)

    @Delete
    suspend fun delete(album: LocalAlbum)

    @Update
    suspend fun update(album: LocalAlbum)

    @Query(value = "select * from table_local_album order by album_name")
    suspend fun getAllAlbum(): List<@JvmWildcard LocalAlbum>

    @Query(value = "select * from table_local_album where album_id in (:albumIdList) order by album_name")
    suspend fun getAlbum(albumIdList: List<String>): List<@JvmWildcard LocalAlbum>

    @Query(value = "select * from table_local_album where album_id = :albumId limit 1")
    suspend fun getAlbum(albumId: String): LocalAlbum?

    @Query(value = "select * from table_local_album where album_id = :albumId limit 1")
    suspend fun isAlbumExist(albumId: String): LocalAlbum?
}