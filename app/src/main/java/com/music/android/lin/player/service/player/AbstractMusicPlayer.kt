package com.music.android.lin.player.service.player

import com.harvest.musicplayer.MediaPlayerEvent
import com.harvest.musicplayer.PlayInfo
import com.harvest.musicplayer.PlaybackState
import com.harvest.musicplayer.PlayerType
import com.music.android.lin.player.service.MusicPlayer
import com.music.android.lin.player.service.state.IMutablePlayerCenter

/**
 * @author liuzhongao
 * @since 2023/10/10 11:54
 */
internal abstract class AbstractMusicPlayer constructor(
    protected val mutablePlayerCenter: IMutablePlayerCenter
) : MusicPlayer {

    abstract val playerType: PlayerType

    protected fun updateInnerState(block: (PlayInfo) -> PlayInfo) {
        this.mutablePlayerCenter.updatePlayInfo(block)
    }

    protected fun setPlaybackState(playbackState: PlaybackState) {
        this.mutablePlayerCenter.updatePlayInfo {
            it.copy(
                previousPlaybackState = it.playbackState,
                playbackState = playbackState,
                isPlaying = playbackState == PlaybackState.Playing
            )
        }
    }

    protected fun trySendPlayEvent(mediaPlayerEvent: MediaPlayerEvent) {
        this.mutablePlayerCenter.emitPlayEvent(mediaPlayerEvent = mediaPlayerEvent)
    }

    override fun play() {
        setPlaybackState(playbackState = PlaybackState.Playing)
    }

    override fun pause() {
        setPlaybackState(playbackState = PlaybackState.Paused)
    }

    override fun stop() {
        setPlaybackState(playbackState = PlaybackState.Stopped)
    }

    override fun reset() {
        setPlaybackState(playbackState = PlaybackState.Stopped)
    }

    override fun release() {
        setPlaybackState(playbackState = PlaybackState.Released)
    }
}