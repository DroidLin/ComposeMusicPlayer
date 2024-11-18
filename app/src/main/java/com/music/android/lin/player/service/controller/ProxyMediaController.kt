package com.music.android.lin.player.service.controller

import com.music.android.lin.player.MessageDispatcher
import com.music.android.lin.player.metadata.MediaResource
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.metadata.data
import com.music.android.lin.player.service.PlayCommand

class ProxyMediaController(
    private val dispatcher: MessageDispatcher,
) : MediaController {

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

    override fun playMediaResource(mediaResource: MediaResource) {
        dispatchPlayerMessage(PlayCommand.PLAY_RESOURCE) {
            data = mediaResource
        }
    }

    private inline fun dispatchPlayerMessage(command: Int, function: PlayMessage.() -> Unit = {}) {
        val playerMessage = PlayMessage.ofCommand(command).apply(function)
        this.dispatcher.dispatch(playerMessage)
    }

}