package com.music.android.lin.player.service.player

import android.net.Uri
import android.view.Surface
import com.music.android.lin.player.interfaces.PlayInfo
import com.music.android.lin.player.interfaces.PlayerType
import com.music.android.lin.player.service.MusicPlayer
import com.music.android.lin.player.service.state.IMutablePlayerCenter

/**
 * @author liuzhongao
 * @since 2023/10/20 12:54 AM
 */
internal class MusicPlayerWrapper constructor(
    private val mutablePlayerCenter: IMutablePlayerCenter
) : MusicPlayer {

    private var _musicPlayer: AbstractMusicPlayer? = null

    override val currentMusicInfoDuration: Long
        get() = this._musicPlayer?.currentMusicInfoDuration ?: 0L
    override val currentProgress: Long
        get() = this._musicPlayer?.currentProgress ?: 0L

    val playerType: PlayerType get() = this._musicPlayer?.playerType ?: PlayerType.UnInitialized

    @JvmOverloads
    fun switchPlayer(playerType: PlayerType, playInfo: PlayInfo? = null) {
        if (this.playerType == playerType) {
            return
        }
        val oldMusicPlayer = this._musicPlayer
        oldMusicPlayer?.pause()
        oldMusicPlayer?.stop()
        oldMusicPlayer?.reset()
        oldMusicPlayer?.release()

        val newMusicPlayer = MusicPlayerFactory.create(
            playerType = playerType,
            mutablePlayerCenter = this.mutablePlayerCenter
        )
        this._musicPlayer = newMusicPlayer
        if (playInfo != null) {
            val songSourceUri = playInfo.mediaInfo?.sourceUri
            if (!songSourceUri.isNullOrEmpty()) {
                newMusicPlayer.reset()
                newMusicPlayer.setDataSource(uri = Uri.parse(songSourceUri))
                newMusicPlayer.prepareWithoutStart {
                    newMusicPlayer.seekToPosition(position = playInfo.currentProgress)
                }
            }
        }
    }

    override fun setSurface(surface: Surface?) {
        this._musicPlayer?.setSurface(surface)
    }

    override fun setDataSource(uri: Uri) {
        this._musicPlayer?.setDataSource(uri = uri)
    }

    override fun prepareWithAutoStart() {
        this._musicPlayer?.prepareWithAutoStart()
    }

    override fun prepareWithoutStart(onPrepared: () -> Unit) {
        this._musicPlayer?.prepareWithoutStart(onPrepared)
    }

    override fun play() {
        this._musicPlayer?.play()
    }

    override fun pause() {
        this._musicPlayer?.pause()
    }

    override fun stop() {
        this._musicPlayer?.stop()
    }

    override fun seekToPosition(position: Long) {
        this._musicPlayer?.seekToPosition(position = position)
    }

    override fun reset() {
        this._musicPlayer?.reset()
    }

    override fun release() {
        this._musicPlayer?.release()
    }
}