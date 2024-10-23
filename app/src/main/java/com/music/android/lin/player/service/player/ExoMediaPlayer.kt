package com.music.android.lin.player.service.player

import android.os.Handler
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.modules.applicationContext
import java.util.logging.Level
import java.util.logging.Logger

@UnstableApi
internal class ExoMediaPlayer(
    private val handler: Handler,
    private val logger: Logger,
) : Player {

    private val playingListener = PlayerListener()

    private val playerListener = object : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@ExoMediaPlayer.playingListener.onPlayingChange(isPlaying)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
        }

        override fun onPlayerError(error: PlaybackException) {
        }
    }

    private val exoPlayer = ExoPlayer.Builder(AppKoin.applicationContext)
        .setName("custom_exo_player")
        .setLooper(this.handler.looper)
        .setWakeMode(C.WAKE_MODE_NONE)
        .build()

    @Volatile
    private var _currentDataSource: Player.DataSource? = null

    override val playingProgress: Long
        get() = this.exoPlayer.currentPosition

    override val mediaDuration: Long
        get() = this.exoPlayer.duration

    init {
        this.exoPlayer.addListener(this.playerListener)
        this.exoPlayer.playWhenReady = true

        this.exoPlayer.bufferedPosition
    }

    override fun addListener(listener: Player.Listener) {
        this.playingListener.addListener(listener)
    }

    override fun removeListener(listener: Player.Listener) {
        this.playingListener.removeListener(listener)
    }

    override fun setDataSource(dataSource: Player.DataSource) {
        val currentRunningDataSource = this._currentDataSource
        val runningMediaItem = this.exoPlayer.currentMediaItem
        if (runningMediaItem != null && currentRunningDataSource != null) {
            this.exoPlayer.stop()
            this.exoPlayer.clearMediaItems()
            this.playingListener.onMediaEnd(currentRunningDataSource)
        }

        val resourceUri = dataSource.tryGetResourceUri()
        if (resourceUri == null) {
            this.logger.log(Level.INFO, "empty resource uri, do nothing.")
            return
        }
        val mediaItem = MediaItem.fromUri(resourceUri)
        this.exoPlayer.setMediaItem(mediaItem)
        this.exoPlayer.prepare()
        this.playingListener.onMediaBegin(dataSource)
    }

    override fun playOrResume() {
        if (this.exoPlayer.playbackState == ExoPlayer.STATE_READY) {
            this.exoPlayer.play()
        }
    }

    override fun pause() {
        this.exoPlayer.pause()
    }

    override fun seekToPosition(position: Long) {
        this.exoPlayer.seekTo(position)
    }

    override fun stop() {
        this.exoPlayer.stop()
    }

    override fun release() {
        if (!this.exoPlayer.isReleased) {
            this.exoPlayer.release()
        }
    }

}