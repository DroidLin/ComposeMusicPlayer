package com.music.android.lin.player.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.metadata.command
import com.music.android.lin.player.service.controller.IPlayerService
import com.music.android.lin.player.service.metadata.PlayHistory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parameterSetOf
import org.koin.core.parameter.parametersOf
import java.io.File

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:37
 */
const val KEY_PLAY_MESSAGE = "key_play_message"

internal class PlayService : Service() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val playerService: IPlayerService = AppKoin.koin.get {
        parametersOf(
            Handler(Looper.getMainLooper()),
            this.coroutineScope
        )
    }

    override fun onCreate() {
        super.onCreate()
        initService()
    }

    private fun initService() {
        val playMessage = PlayMessage()
        playMessage.command = PlayCommand.PLAYER_INIT
        this.playerService.handleMessage(playMessage)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val playMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(KEY_PLAY_MESSAGE, PlayMessage::class.java)
        } else {
            intent?.getParcelableExtra(KEY_PLAY_MESSAGE)
        }
        if (playMessage != null) {
            this.playerService?.handleMessage(playMessage)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        this.coroutineScope.cancel()
    }
}