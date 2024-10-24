package com.music.android.lin.player.service.player

import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.metadata.data
import com.music.android.lin.player.service.PlayCommand
import com.music.android.lin.player.service.controller.IPlayerService
import com.music.android.lin.player.service.controller.PlayerControl
import com.music.android.lin.player.service.player.datasource.DataSource

/**
 * @author liuzhongao
 * @since 2024/10/24 17:18
 */
class MediaPlayerProxy(
    private val playerService: IPlayerService,
    playerControl: PlayerControl
) : PlayerControl by playerControl {

    override fun setVolume(volumeLevel: Float) {
        dispatchPlayerMessage(PlayCommand.SET_VOLUME) {
            this.data = volumeLevel
        }
    }

    override fun setPlayMode(playMode: PlayMode) {
        dispatchPlayerMessage(PlayCommand.SET_PLAY_MODE) {
            this.data = playMode
        }
    }

    override fun playResource(playList: PlayList, fromIndex: Int, playWhenReady: Boolean) {
        dispatchPlayerMessage(PlayCommand.PLAY_RESOURCE) {
            this.data = arrayOf(playList, fromIndex, playWhenReady)
        }
    }

    override fun setResource(playList: PlayList, fromIndex: Int) {
        dispatchPlayerMessage(PlayCommand.SET_RESOURCE) {
            data = arrayOf(playList, fromIndex)
        }
    }

    override fun playOrResume() {
        dispatchPlayerMessage(PlayCommand.PLAY_OR_RESUME)
    }

    override fun pause() {
        dispatchPlayerMessage(PlayCommand.PAUSE)
    }

    override fun skipToPrevious() {
        dispatchPlayerMessage(PlayCommand.SKIP_TO_PREV)
    }

    override fun skipToNext() {
        dispatchPlayerMessage(PlayCommand.SKIP_TO_NEXT)
    }

    override fun seekToPosition(position: Long) {
        dispatchPlayerMessage(PlayCommand.SEEK_TO_POSITION) {
            this.data = position
        }
    }

    override fun stop() {
        dispatchPlayerMessage(PlayCommand.PLAYER_STOP)
    }

    override fun release() {
        dispatchPlayerMessage(PlayCommand.PLAYER_RELEASE)
    }

    private inline fun dispatchPlayerMessage(command: Int, function: PlayMessage.() -> Unit = {}) {
        val playerMessage = PlayMessage.ofCommand(command).apply(function)
        playerService.dispatchMessage(playerMessage)
    }
}