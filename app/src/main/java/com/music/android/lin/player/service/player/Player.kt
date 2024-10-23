package com.music.android.lin.player.service.player

import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.player.datasource.DataSource

sealed interface Player {

    val playingProgress: Long

    val mediaDuration: Long

    val isPlaying: Boolean

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    fun setDataSource(dataSource: DataSource)

    fun playOrResume()

    fun pause()

    fun seekToPosition(position: Long)

    fun stop()

    fun release()

    interface Listener {

        fun onPlayingChange(isPlaying: Boolean) {}

        fun onMediaBegin(dataSource: DataSource) {}

        fun onMediaEnd(dataSource: DataSource) {}

        fun onPlayModeChange(playMode: PlayMode) {}
    }
}