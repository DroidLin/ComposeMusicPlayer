package com.music.android.lin.player.service.player

import androidx.annotation.FloatRange
import com.music.android.lin.player.service.metadata.PlayerMetadata
import com.music.android.lin.player.service.player.datasource.DataSource
import kotlinx.coroutines.flow.Flow

sealed interface Player {

    val playerMetadata: Flow<PlayerMetadata>

    val contentProgress: Long

    val contentDuration: Long

    val isPlaying: Boolean

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    fun setVolume(@FloatRange volume: Float)

    fun setDataSource(dataSource: DataSource): Boolean

    fun playOrResume()

    fun pause()

    fun seekToPosition(position: Long)

    fun stop()

    fun release()

    interface Listener {

        fun onPlayEnd()
    }
}