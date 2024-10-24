package com.music.android.lin.player.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.player.IMediaServiceInterface
import com.music.android.lin.player.PlayerIdentifier
import com.music.android.lin.player.PlayerModule
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.service.controller.IPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:37
 */
const val KEY_PLAY_MESSAGE = "key_play_message"

internal class PlayService : Service() {

    private val innerModule = module {
        factory<Service>(PlayerIdentifier.PlayService) { this@PlayService }
    }

    private var playerService: IPlayerService? = null

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val handlerThread = object : HandlerThread("player_thread") {
        override fun onLooperPrepared() {
            val playerService: IPlayerService = AppKoin.koin.get {
                parametersOf(
                    Handler(this.looper),
                    this@PlayService.coroutineScope
                )
            }
            val playMessage = PlayMessage.ofCommand(PlayCommand.PLAYER_INIT)
            playerService.dispatchMessage(playMessage)

            this@PlayService.playerService = playerService
        }
    }

    override fun onCreate() {
        super.onCreate()
        AppKoin.koin.loadModules(listOf(PlayerModule, innerModule))
        this.handlerThread.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val playMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(KEY_PLAY_MESSAGE, PlayMessage::class.java)
        } else {
            intent?.getParcelableExtra(KEY_PLAY_MESSAGE)
        }
        if (playMessage != null) {
            this.playerService?.dispatchMessage(playMessage)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return object : IMediaServiceInterface.Stub() {
            override fun dispatchMessage(message: PlayMessage?) {
                if (message != null) {
                    this@PlayService.playerService?.dispatchMessage(message)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.coroutineScope.cancel()
        this.handlerThread.quitSafely()
        this.playerService?.release()
        this.playerService = null
        AppKoin.koin.unloadModules(listOf(PlayerModule, innerModule))
    }
}