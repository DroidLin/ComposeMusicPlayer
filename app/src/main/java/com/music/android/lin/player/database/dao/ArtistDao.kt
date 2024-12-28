package com.music.android.lin.player.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.music.android.lin.player.database.metadata.LocalArtist

/**
 * @author liuzhongao
 * @since 2023/10/6 00:09
 */
@Dao
internal interface ArtistDao {

    @Insert(entity = LocalArtist::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg localArtists: LocalArtist)

    @Insert(entity = LocalArtist::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(artist: LocalArtist)

    @Upsert(entity = LocalArtist::class)
    suspend fun upsert(artist: LocalArtist)

    @Delete(entity = LocalArtist::class)
    suspend fun delete(artist: LocalArtist)

    @Update(entity = LocalArtist::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(artist: LocalArtist)

    @Query(value = "select * from table_local_artist where local_artist_id = :id")
    suspend fun getArtist(id: String): LocalArtist?

    @Query(value = "select * from table_local_artist where local_artist_id in (:ids)")
    suspend fun getArtists(vararg ids: String): List<@JvmWildcard LocalArtist>

    @Query(value = "select * from table_local_artist where local_artist_id = :id limit 1")
    suspend fun isArtistExists(id: String): LocalArtist?
}