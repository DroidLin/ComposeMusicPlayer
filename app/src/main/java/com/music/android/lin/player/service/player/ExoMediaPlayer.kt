package com.music.android.lin.player.service.player

import android.annotation.SuppressLint
import android.os.Handler
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.modules.applicationContext
import com.music.android.lin.player.service.metadata.PlayerMetadata
import com.music.android.lin.player.service.player.datasource.DataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.logging.Level
import java.util.logging.Logger

@SuppressLint("UnsafeOptInUsageError")
internal class ExoMediaPlayer(
    private val handler: Handler,
    private val logger: Logger,
) : Player {

    private val playerListener = object : androidx.media3.common.Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            this@ExoMediaPlayer.metadataFlow.update {
                it.copy(isPlaying = isPlaying)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val isBuffering = playbackState == androidx.media3.common.Player.STATE_BUFFERING
            if (playbackState == androidx.media3.common.Player.STATE_READY) {
                this@ExoMediaPlayer.updatePlayerMetadata()
            } else if (playbackState == androidx.media3.common.Player.STATE_ENDED) {
                this@ExoMediaPlayer.notifyListener { onPlayEnd() }
            }
            this@ExoMediaPlayer.metadataFlow.update { it.copy(isBuffering = isBuffering) }
        }

        override fun onVolumeChanged(volume: Float) {
            this@ExoMediaPlayer.metadataFlow.update { it.copy(volume = volume) }
        }

        override fun onPlayerError(error: PlaybackException) {
        }

        override fun onPlayerErrorChanged(error: PlaybackException?) {
        }
    }

    private val exoPlayer by lazy {
        ExoPlayer.Builder(AppKoin.applicationContext)
            .setName("custom_exo_player")
            .setLooper(this.handler.looper)
            .setWakeMode(C.WAKE_MODE_NONE)
            .build()
    }

    private val listenerList = ArrayList<Player.Listener>()

    private val metadataFlow = MutableStateFlow(PlayerMetadata())

    override val playerMetadata: Flow<PlayerMetadata> = this.metadataFlow.asStateFlow()

    override val contentProgress: Long
        get() = this.exoPlayer.contentPosition

    override val contentDuration: Long
        get() = this.exoPlayer.contentDuration

    override val isPlaying: Boolean
        get() = this.exoPlayer.isPlaying

    init {
        this.exoPlayer.addListener(this.playerListener)
    }

    override fun addListener(listener: Player.Listener) {
        synchronized(this.listenerList) {
            if (!this.listenerList.contains(listener)) {
                this.listenerList += listener
            }
        }
    }

    override fun removeListener(listener: Player.Listener) {
        synchronized(this.listenerList) {
            if (this.listenerList.contains(listener)) {
                this.listenerList -= listener
            }
        }
    }

    override fun setVolume(volume: Float) {
        this.exoPlayer.volume = volume
    }

    override fun setDataSource(dataSource: DataSource): Boolean {
        val resourceUri = dataSource.tryGetResourceUri()
        if (resourceUri == null) {
            this.logger.log(Level.INFO, "empty resource uri, do nothing.")
            return false
        }
        val mediaItem = MediaItem.fromUri(resourceUri)
        this.exoPlayer.setMediaItem(mediaItem)
        this.exoPlayer.prepare()
        return true
    }

    override fun playOrResume() {
        this.exoPlayer.play()
        this.updatePlayerMetadata()
    }

    override fun pause() {
        this.exoPlayer.pause()
        this.updatePlayerMetadata()
    }

    override fun seekToPosition(position: Long) {
        this.exoPlayer.seekTo(position)
        this.updatePlayerMetadata()
    }

    override fun sync() {
        this.updatePlayerMetadata()
    }

    override fun stop() {
        this.exoPlayer.stop()
        this.updatePlayerMetadata()
    }

    override fun release() {
        if (!this.exoPlayer.isReleased) {
            this.exoPlayer.release()
        }
    }

    private inline fun notifyListener(function: Player.Listener.() -> Unit) {
        synchronized(this.listenerList) {
            this.listenerList.forEach(function)
        }
    }

    private fun updatePlayerMetadata() {
        this.metadataFlow.update {
            it.copy(
                contentProgress = this.contentProgress,
                contentDuration = this.contentDuration,
                volume = this.exoPlayer.volume,
                isPlaying = this.exoPlayer.isPlaying,
            )
        }
    }
}