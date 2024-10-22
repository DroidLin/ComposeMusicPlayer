package com.harvest.musicplayer.service.controller

import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.PlayList
import com.harvest.musicplayer.PlayMode
import com.harvest.musicplayer.PlaybackState
import com.harvest.musicplayer.PlayerEvent
import com.harvest.musicplayer.service.MusicPlayer

/**
 * @author liuzhongao
 * @since 2023/10/11 16:08
 */
internal class MusicPlayerListenerWrapper : MusicPlayer.Listener {

    private val _listener = ArrayList<MusicPlayer.Listener>()

    fun addListener(listener: MusicPlayer.Listener) {
        synchronized(this._listener) {
            this._listener += listener
        }
    }

    fun removeListener(listener: MusicPlayer.Listener) {
        synchronized(this._listener) {
            this._listener -= listener
        }
    }

    override fun onPlayModeChanged(playMode: PlayMode) {
        this.notifyListener { onPlayModeChanged(playMode) }
    }

    override fun onNewPlayList(playList: PlayList?) {
        this.notifyListener { onNewPlayList(playList) }
    }

    override fun onNewMusicInfo(mediaInfo: MediaInfo?) {
        this.notifyListener { onNewMusicInfo(mediaInfo) }
    }

    override fun onEvent(playerEvent: PlayerEvent) {
        this.notifyListener { onEvent(playerEvent) }
    }

    override fun onPlaybackStateChanged(playbackState: PlaybackState) {
        this.notifyListener { onPlaybackStateChanged(playbackState) }
    }

    override fun onSeekProgressChanged(progress: Long, duration: Long) {
        this.notifyListener { onSeekProgressChanged(progress, duration) }
    }

    override fun notifyInfo(code: Int, ext: Map<String, Any?>?) {
        this.notifyListener { notifyInfo(code, ext) }
    }

    private inline fun notifyListener(block: MusicPlayer.Listener.() -> Unit) {
        synchronized(this._listener) {
            val iterator = this._listener.iterator()
            while (iterator.hasNext()) {
                iterator.next().block()
            }
        }
    }
}