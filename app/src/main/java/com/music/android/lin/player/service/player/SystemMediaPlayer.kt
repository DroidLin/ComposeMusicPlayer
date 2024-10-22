package com.harvest.musicplayer.service.player

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Surface
import com.harvest.common.services.KServiceFacade
import com.harvest.musicplayer.MediaPlayerEvent
import com.harvest.musicplayer.PlayerType
import com.harvest.musicplayer.service.state.IMutablePlayerCenter
import com.harvest.statistic.interfaces.IStatistic

/**
 * @author liuzhongao
 * @since 2023/10/9 11:48 PM
 */
internal class SystemMediaPlayer(mutablePlayerCenter: IMutablePlayerCenter) :
    AbstractMusicPlayer(mutablePlayerCenter) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val _player = MediaPlayer()
    private val iStatisticService: IStatistic get() = KServiceFacade[IStatistic::class.java]

    override val playerType: PlayerType get() = PlayerType.System
    override val currentMusicInfoDuration: Long get() = _player.duration.toLong()
    override val currentProgress: Long get() = _player.currentPosition.toLong()

    private val onCompleteListener = MediaPlayer.OnCompletionListener {
        this.trySendPlayEvent(mediaPlayerEvent = MediaPlayerEvent.MediaPlayEnd)
    }

    private val onErrorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
        this.iStatisticService.logInfo(
            "SystemMediaPlayer",
            "kind", "playerOnError",
            "what", what,
            "extra", extra
        )
        true
    }

    private val onInfoListener = MediaPlayer.OnInfoListener { mp, what, extra ->
        this.iStatisticService.logInfo(
            "SystemMediaPlayer",
            "kind", "playerOnInfo",
            "what", what,
            "extra", extra
        )
        when (what) {
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                this.trySendPlayEvent(mediaPlayerEvent = MediaPlayerEvent.BufferingStart)
                true
            }

            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                this.trySendPlayEvent(mediaPlayerEvent = MediaPlayerEvent.BufferingEnd)
                true
            }

            else -> false
        }
    }

    init {
        this._player.setOnCompletionListener(this.onCompleteListener)
        this._player.setOnInfoListener(this.onInfoListener)
        this._player.setOnErrorListener(this.onErrorListener)
        this._player.setAudioAttributes(
            AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        this._player.setAudioStreamType(AudioManager.STREAM_MUSIC)
    }

    override fun setSurface(surface: Surface?) {
        this._player.setSurface(surface)
    }

    override fun setDataSource(uri: Uri) {
        this._player.setDataSource(uri.toString())
    }

    override fun prepareWithAutoStart() {
        this._player.onPreparedOneshot {
            // 此处使用post是为了解决部分机型在onPrepared回调中获取不到duration、position等数据
            this.mainHandler.post { this.play() }
        }
        this._player.prepareAsync()
    }

    override fun prepareWithoutStart(onPrepared: () -> Unit) {
        this._player.onPreparedOneshot(onPrepared)
        this._player.prepareAsync()
    }

    override fun play() {
        super.play()
        this._player.start()
        this.syncProgress()
    }

    override fun pause() {
        super.pause()
        this._player.pause()
        this.syncProgress()
    }

    override fun stop() {
        super.stop()
        this._player.stop()
        this.resetProgress()
    }

    override fun reset() {
        super.reset()
        this._player.reset()
        this.resetProgress()
    }

    private fun syncProgress() {
        this.updateInnerState {
            it.copy(
                currentDuration = this.currentMusicInfoDuration.coerceAtLeast(0),
                currentProgress = this.currentProgress
            )
        }
    }

    private fun resetProgress() {
        this.updateInnerState {
            it.copy(currentDuration = 0, currentProgress = 0)
        }
    }

    override fun seekToPosition(position: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this._player.seekTo(position, MediaPlayer.SEEK_PREVIOUS_SYNC)
        } else {
            this._player.seekTo(position.toInt())
        }
        this.syncProgress()
    }

    override fun release() {
        super.release()
        this._player.release()
    }

    private inline fun MediaPlayer.onPreparedOneshot(crossinline onPrepare: () -> Unit) {
        this.setOnPreparedListener {
            this.setOnPreparedListener(null)
            onPrepare()
        }
    }
}