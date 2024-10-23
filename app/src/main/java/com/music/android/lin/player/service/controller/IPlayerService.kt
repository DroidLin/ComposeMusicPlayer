package com.music.android.lin.player.service.controller

import android.content.Context
import android.os.Binder
import android.os.Handler
import android.view.Surface
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.metadata.command
import com.music.android.lin.player.service.PlayCommand
import com.music.android.lin.player.service.metadata.PlayHistory
import com.music.android.lin.player.service.readObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:43
 */
interface IPlayerService {

    val playerControl: PlayerControl

    fun handleMessage(playMessage: PlayMessage)

    fun surfaceAttach(surface: Surface?)
}

internal class PlayerServiceHost(
    override val playerControl: PlayerControl,
    private val coroutineScope: CoroutineScope,
    private val context: Context,
) : Binder(), IPlayerService {

    override fun handleMessage(playMessage: PlayMessage) {
        when (playMessage.command) {
            PlayCommand.PLAYER_INIT -> {
                this.coroutineScope.launch(Dispatchers.IO) {
                    val directory = File(this@PlayerServiceHost.context.filesDir, "player")
                    val playHistory = if (directory.exists() || directory.mkdirs()) {
                        readObject<PlayHistory>(File(directory, "playHistory.data"))
                    } else null
                    if (playHistory != null) {
                        withContext(Dispatchers.Main) {
                            this@PlayerServiceHost.playerControl.setResource(
                                playList = playHistory.playList,
                                fromIndex = playHistory.indexOfCurrentPosition
                            )
                            this@PlayerServiceHost.playerControl.seekToPosition(playHistory.playingProgress)
                        }
                    }
                }
            }
        }
    }

    override fun surfaceAttach(surface: Surface?) {
        TODO("Not yet implemented")
    }

}