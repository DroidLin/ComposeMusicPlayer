package com.music.android.lin.player.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.player.IMediaServiceInterface
import com.music.android.lin.player.MediaServiceInterfaceHandlerStub
import com.music.android.lin.player.PlayerIdentifier
import com.music.android.lin.player.PlayerModule
import com.music.android.lin.player.audiofocus.PlayerAudioFocusManager
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.notification.PlayNotificationManager
import com.music.android.lin.player.notification.PlayMediaSession
import com.music.android.lin.player.service.controller.PlayEventLoop
import com.music.android.lin.player.service.metadata.PlayMessageCommand
import com.music.android.lin.player.service.player.datasource.DataSource
import com.music.android.lin.player.service.player.datasource.LocalFileDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import java.util.LinkedList

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:37
 */
const val KEY_PLAY_MESSAGE = "key_play_message"

internal class PlayService : Service() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val innerModule = module {
        factory<Service>(PlayerIdentifier.PlayService) {
            this@PlayService
        }
        single {
            DataSource.Factory {
                LocalFileDataSource(it)
            }
        }
        single {
            coroutineScope
        }
    }

    private var playEventLoop: PlayEventLoop? = null
    private val playMessageCommandCache = LinkedList<PlayMessageCommand>()

    private val handlerThread = object : HandlerThread("player_thread") {
        private var handlerModule: Module? = null

        override fun onLooperPrepared() {
            val handler = Handler(looper)
            val handlerModule = module {
                single(PlayerIdentifier.PlayServiceHandlerThread) {
                    handler
                }
            }
            AppKoin.koin.loadModules(listOf(handlerModule))
            val remoteMediaServiceProxy: RemoteMediaServiceProxy = AppKoin.koin.get()
            val playerService: PlayEventLoop = AppKoin.koin.get()
            playerService.addDispatcher(remoteMediaServiceProxy.dispatcher)

            val playMessage = PlayMessage.ofCommand(PlayCommand.PLAYER_INIT)
            playerService.dispatchMessage(playMessage)

            val notificationManager: PlayNotificationManager = AppKoin.koin.get()
            val playMediaSession: PlayMediaSession = AppKoin.koin.get()
            val audioFocusManager: PlayerAudioFocusManager = AppKoin.koin.get()

            val mediaSessionToken = playMediaSession.mediaSessionToken
            notificationManager.setMediaSessionToken(mediaSessionToken)

            synchronized(playMessageCommandCache) {
                playMessageCommandCache.forEach { (playMessage) ->
                    playerService.dispatchMessage(playMessage)
                }
            }

            this@PlayService.playEventLoop = playerService
            this@PlayService.notificationManager = notificationManager
            this@PlayService.playMediaSession = playMediaSession
            this@PlayService.audioFocusManager = audioFocusManager

            this.handlerModule = handlerModule
        }

        override fun run() {
            super.run()
            val handlerModule = this.handlerModule
            if (handlerModule != null) {
                AppKoin.koin.unloadModules(listOf(handlerModule))
            }
        }
    }

    private var notificationManager: PlayNotificationManager? = null
    private var playMediaSession: PlayMediaSession? = null
    private var audioFocusManager: PlayerAudioFocusManager? = null

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
            this.playEventLoop?.dispatchMessage(playMessage)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder = MediaServiceInterfaceHandlerStub(
        dispatcher = { playMessage ->
            val playEventLoop = this.playEventLoop
            if (playEventLoop == null) {
                synchronized(playMessageCommandCache) {
                    playMessageCommandCache += PlayMessageCommand(playMessage)
                }
                return@MediaServiceInterfaceHandlerStub
            }
            playEventLoop.dispatchMessage(playMessage)
        }
    )

    override fun onDestroy() {
        super.onDestroy()
        this.coroutineScope.cancel()
        this.handlerThread.quitSafely()
        this.playEventLoop?.release()
        this.playEventLoop = null
        this.notificationManager?.release()
        this.notificationManager = null
        this.audioFocusManager?.release()
        this.audioFocusManager = null
        this.playMediaSession = null
        AppKoin.koin.unloadModules(listOf(PlayerModule, innerModule))
    }
}