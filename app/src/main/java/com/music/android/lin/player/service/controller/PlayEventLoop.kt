package com.music.android.lin.player.service.controller

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.music.android.lin.player.IMediaServiceInterface
import com.music.android.lin.player.MessageDispatcher
import com.music.android.lin.player.metadata.CommonMediaResourceParameter
import com.music.android.lin.player.metadata.MediaResource
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
interface PlayEventLoop {

    val playInfo: PlayInfo

    val mediaController: MediaController

    fun addDispatcher(dispatcher: MessageDispatcher)

    fun removeDispatcher(dispatcher: MessageDispatcher)

    fun dispatchMessage(playMessage: PlayMessage)

    fun release()
}

internal class PlayEventLoopHost(
    override val playInfo: PlayInfo,
    override val mediaController: MediaController,
    private val coroutineScope: CoroutineScope,
    private val context: Context,
    handler: Handler
) : PlayEventLoop {

    private val dispatcherList = ArrayList<MessageDispatcher>()

    private val innerHandler = object : Handler(handler.looper) {
        override fun handleMessage(msg: Message) {
            val playMessage = msg.obj as? PlayMessage
            this@PlayEventLoopHost.handleMessage(playMessage)
        }
    }

    override fun addDispatcher(dispatcher: MessageDispatcher) {
        synchronized(dispatcherList) {
            if (!dispatcherList.contains(dispatcher)) {
                dispatcherList += dispatcher
            }
        }
    }

    override fun removeDispatcher(dispatcher: MessageDispatcher) {
        synchronized(dispatcherList) {
            if (dispatcherList.contains(dispatcher)) {
                dispatcherList -= dispatcher
            }
        }
    }

    override fun dispatchMessage(playMessage: PlayMessage) {
        dispatchToDispatchers(playMessage)
        innerHandler.obtainMessage().also { message ->
            message.obj = playMessage
            if (Looper.myLooper() == innerHandler.looper) {
                innerHandler.handleMessage(message)
            } else {
                runBlocking {
                    suspendCoroutine<Unit> { continuation ->
                        innerHandler.post {
                            innerHandler.handleMessage(message)
                            continuation.resume(Unit)
                        }
                    }
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
                    val mediaResource = CommonMediaResourceParameter(
                        playList = playHistory.playList,
                        startPosition = playHistory.indexOfCurrentPosition,
                        autoStart = false
                    )
                    this.mediaController.playMediaResource(mediaResource)
                    this.mediaController.seekToPosition(playHistory.playingProgress)
                }
            }

            PlayCommand.PAUSE -> this.mediaController.pause()
            PlayCommand.PLAY_OR_RESUME -> this.mediaController.playOrResume()
            PlayCommand.SKIP_TO_NEXT -> this.mediaController.skipToNext()
            PlayCommand.SKIP_TO_PREV -> this.mediaController.skipToPrevious()
            PlayCommand.SET_VOLUME -> this.mediaController.setVolume(playMessage.data as Float)
            PlayCommand.SEEK_TO_POSITION -> this.mediaController.seekToPosition(playMessage.data as Long)
            PlayCommand.PLAYER_STOP -> this.mediaController.stop()
            PlayCommand.PLAYER_RELEASE -> this.mediaController.release()
            PlayCommand.SET_PLAY_MODE -> this.mediaController.setPlayMode(playMessage.data as PlayMode)
            PlayCommand.PLAY_RESOURCE -> {
                val mediaResource = playMessage.data as MediaResource
                this.mediaController.playMediaResource(mediaResource)
            }
        }
    }

    private fun dispatchToDispatchers(playMessage: PlayMessage) {
        synchronized(dispatcherList) {
            dispatcherList.forEach { dispatcher ->
                dispatcher.dispatch(playMessage)
            }
        }
    }

    override fun release() {
        this.mediaController.release()
    }

}