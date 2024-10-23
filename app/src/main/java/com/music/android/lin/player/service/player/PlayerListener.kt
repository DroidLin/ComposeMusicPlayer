package com.music.android.lin.player.service.player

import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.player.datasource.DataSource
import java.util.concurrent.CopyOnWriteArrayList

internal class PlayerListener : Player.Listener {

    private val listeners = CopyOnWriteArrayList<Player.Listener>()

    fun addListener(listener: Player.Listener) {
        if (!listeners.contains(listener)) {
            listeners += listener
        }
    }

    fun removeListener(listener: Player.Listener) {
        if (listeners.contains(listener)) {
            listeners -= listener
        }
    }

    override fun onPlayingChange(isPlaying: Boolean) {
        dispatchListener { onPlayingChange(isPlaying) }
    }

    override fun onMediaBegin(dataSource: DataSource) {
        dispatchListener { onMediaBegin(dataSource) }
    }

    override fun onMediaEnd(dataSource: DataSource) {
        dispatchListener { onMediaEnd(dataSource) }
    }

    override fun onPlayModeChange(playMode: PlayMode) {
        dispatchListener { onPlayModeChange(playMode) }
    }

    private inline fun dispatchListener(function: Player.Listener.() -> Unit) {
        this.listeners.forEach(function)
    }
}