package com.music.android.lin.player.service.controller

import android.content.Context
import android.os.Handler
import android.os.Message
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.metadata.command
import com.music.android.lin.player.metadata.data
import com.music.android.lin.player.service.PlayCommand
import com.music.android.lin.player.service.metadata.PlayHistory
import com.music.android.lin.player.service.readObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:43
 */
interface IPlayerService {

    val playerControl: PlayerControl

    fun syncDispatch(playMessage: PlayMessage)

    fun asyncDispatch(playMessage: PlayMessage)

    fun release()
}

internal class PlayerServiceHost(
    override val playerControl: PlayerControl,
    private val coroutineScope: CoroutineScope,
    private val context: Context,
    handler: Handler
) : IPlayerService {

    private val innerHandler = object : Handler(handler.looper) {
        override fun handleMessage(msg: Message) {
            val playMessage = msg.obj as? PlayMessage
            this@PlayerServiceHost.handleMessage(playMessage)
        }
    }

    override fun asyncDispatch(playMessage: PlayMessage) {
        val message = innerHandler.obtainMessage()
        message.obj = playMessage
        message.sendToTarget()
    }

    override fun syncDispatch(playMessage: PlayMessage) {
        runBlocking {
            suspendCoroutine<Unit> { continuation ->
                innerHandler.postAtFrontOfQueue {
                    val message = innerHandler.obtainMessage()
                    message.obj = playMessage
                    innerHandler.handleMessage(message)
                    continuation.resume(Unit)
                }
            }
        }
    }

    private fun handleMessage(playMessage: PlayMessage?) {
        playMessage ?: return
        when (playMessage.command) {
            PlayCommand.PLAYER_INIT -> {
                val directory = File(this.context.filesDir, "player")
                val playHistory = if (directory.exists() || directory.mkdirs()) {
                    val playHistoryFile = File(directory, "playHistory.data")
                    if (playHistoryFile.exists()) {
                        readObject<PlayHistory>(playHistoryFile)
                    } else null
                } else null
                if (playHistory != null) {
                    this.playerControl.setResource(
                        playList = playHistory.playList,
                        fromIndex = playHistory.indexOfCurrentPosition
                    )
                    this.playerControl.seekToPosition(playHistory.playingProgress)
                }
            }
            PlayCommand.PAUSE -> this.playerControl.pause()
            PlayCommand.PLAY_OR_RESUME -> this.playerControl.playOrResume()
            PlayCommand.SKIP_TO_NEXT -> this.playerControl.skipToNext()
            PlayCommand.SKIP_TO_PREV -> this.playerControl.skipToPrevious()
            PlayCommand.SET_VOLUME -> this.playerControl.setVolume(playMessage.data as Float)
            PlayCommand.SEEK_TO_POSITION -> this.playerControl.seekToPosition(playMessage.data as Long)
            PlayCommand.PLAYER_STOP -> this.playerControl.stop()
            PlayCommand.PLAYER_RELEASE -> this.playerControl.release()
            PlayCommand.SET_PLAY_MODE -> this.playerControl.setPlayMode(playMessage.data as PlayMode)
            PlayCommand.SET_RESOURCE -> {
                val (playList, fromIndex) = playMessage.data as Array<Any>
                this.playerControl.setResource(playList as PlayList, fromIndex as Int)
            }
            PlayCommand.PLAY_RESOURCE -> {
                val (playList, fromIndex, playWhenReady) = playMessage.data as Array<Any>
                this.playerControl.playResource(playList as PlayList, fromIndex as Int, playWhenReady as Boolean)
            }
        }
    }

    override fun release() {
        this.playerControl.release()
    }

}