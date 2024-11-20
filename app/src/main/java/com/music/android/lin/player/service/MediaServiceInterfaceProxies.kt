package com.music.android.lin.player.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.music.android.lin.player.IMediaServiceInterface
import com.music.android.lin.player.MediaServiceInterfaceHandlerStub
import com.music.android.lin.player.MessageDispatcher
import com.music.android.lin.player.attachStubHandle
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.metadata.command
import com.music.android.lin.player.metadata.data
import com.music.android.lin.player.service.controller.PlayInfo
import com.music.android.lin.player.service.metadata.PlayMessageCommand
import com.music.android.lin.player.utils.collectWithScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import java.util.LinkedList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal interface RemoteListener {

    fun onConnected(context: Context)

    fun onDisconnected(context: Context)

    fun onRemoteDead(context: Context)
}

internal class MediaServiceInterfaceWrapper(
    private val context: Context
) : IMediaServiceInterface.Default() {

    private val mediaServiceInterfaceStub = MediaServiceInterfaceHandlerStub(
        dispatcher = { playMessage ->
            synchronized(dispatcherList) {
                dispatcherList.forEach { dispatcher ->
                    dispatcher.dispatch(playMessage)
                }
            }
        }
    )

    private val dispatcherList = ArrayList<MessageDispatcher>()
    private val remoteListenerList = ArrayList<RemoteListener>()
    private val playMessageCache = LinkedList<PlayMessageCommand>()

    private val deathRecipient = IBinder.DeathRecipient {
        notifyRemoteListener {
            onRemoteDead(context = this@MediaServiceInterfaceWrapper.context)
        }
        doConnect(this.context)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val mediaServiceInterface = IMediaServiceInterface.Stub.asInterface(service)
            if (mediaServiceInterface != null) {
                mediaServiceInterface.asBinder().linkToDeath(deathRecipient, 0)
                mediaServiceInterface.attachStubHandle(mediaServiceInterfaceStub)
                this@MediaServiceInterfaceWrapper.remoteService = mediaServiceInterface
                releaseAllCachedMessages()
                notifyRemoteListener {
                    onConnected(this@MediaServiceInterfaceWrapper.context)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            val context = this@MediaServiceInterfaceWrapper.context.applicationContext
            notifyRemoteListener {
                onDisconnected(context)
            }
            doConnect(context)
        }
    }

    private var remoteService: IMediaServiceInterface? = null

    val isConnected: Boolean
        get() = this.remoteService?.asBinder()?.isBinderAlive ?: false

    @JvmOverloads
    fun doConnect(context: Context = this.context, force: Boolean = false) {
        val iMediaServiceInterface = this.remoteService
        if (iMediaServiceInterface != null && iMediaServiceInterface.asBinder().isBinderAlive && !force) {
            return
        }

        val intent = Intent(context, PlayService::class.java)
        context.startService(intent)
        context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun addDispatcher(dispatcher: MessageDispatcher) {
        synchronized(dispatcherList) {
            if (!dispatcherList.contains(dispatcher)) {
                dispatcherList += dispatcher
            }
        }
    }

    fun removeDispatcher(dispatcher: MessageDispatcher) {
        synchronized(dispatcherList) {
            if (dispatcherList.contains(dispatcher)) {
                dispatcherList -= dispatcher
            }
        }
    }

    private fun releaseAllCachedMessages() {
        val newCacheList = synchronized(playMessageCache) {
            try {
                playMessageCache.toList()
            } finally {
                playMessageCache.clear()
            }
        }
        newCacheList.forEach { (playMessage) ->
            this.cacheOrDispatch(playMessage)
        }
    }

    fun addListener(remoteListener: RemoteListener) {
        if (this.remoteListenerList.contains(remoteListener)) {
            return
        }
        synchronized(this.remoteListenerList) {
            this.remoteListenerList += remoteListener
        }
    }

    fun removeListener(remoteListener: RemoteListener) {
        if (!this.remoteListenerList.contains(remoteListener)) {
            return
        }
        synchronized(this.remoteListenerList) {
            this.remoteListenerList -= remoteListener
        }
    }

    override fun dispatch(playMessage: PlayMessage?) {
        requireNotNull(playMessage) { "dispatched with null play message!" }
        this.cacheOrDispatch(playMessage)
    }

    private fun cacheOrDispatch(playMessage: PlayMessage) {
        val iMediaServiceInterface = this.remoteService
        if (iMediaServiceInterface == null || !iMediaServiceInterface.asBinder().isBinderAlive) {
            synchronized(this.playMessageCache) {
                this.playMessageCache += PlayMessageCommand(playMessage)
            }
            return
        }
        iMediaServiceInterface.runCatching {
            dispatch(playMessage)
        }.onFailure { it.printStackTrace() }
    }

    private inline fun notifyRemoteListener(block: RemoteListener.() -> Unit) {
        val copyOfRemoteList = synchronized(this.remoteListenerList) {
            this.remoteListenerList.toList()
        }
        copyOfRemoteList.forEach(block)
    }

}

internal class RemoteMediaServiceProxy(
    private val handler: Handler,
    private val playInfo: PlayInfo,
    private val coroutineScope: CoroutineScope
) {

    private var iMediaServiceInterface: IMediaServiceInterface? = null

    private val playMessageCache = LinkedList<PlayMessageCommand>()

    private val remoteDispatcherProxy = MessageDispatcher { playMessage ->
        val mediaServiceInterface = this.iMediaServiceInterface
        if (mediaServiceInterface == null || !mediaServiceInterface.asBinder().isBinderAlive) {
            this.playMessageCache += PlayMessageCommand(playMessage)
            return@MessageDispatcher
        }
        mediaServiceInterface.runCatching {
            dispatch(playMessage)
        }.onFailure { it.printStackTrace() }
    }

    val dispatcher = MessageDispatcher { playMessage ->
        if (Looper.myLooper() == handler.looper) {
            handleMessage(playMessage)
        } else {
            handler.post {
                handleMessage(playMessage)
            }
        }
    }

    init {
        this.playInfo.information
            .distinctUntilChanged()
            .debounce(200L)
            .onEach { information ->
                val playMessage = PlayMessage.ofCommand(PlayCommand.UPDATE_PLAY_INFORMATION)
                playMessage.data = information
                this.dispatch(playMessage)
            }
            .collectWithScope(this.coroutineScope)
    }

    private fun handleMessage(playMessage: PlayMessage) {
        when (playMessage.command) {
            PlayCommand.ATTACH_MEDIA_INTERFACE_HANDLE -> {
                this.iMediaServiceInterface = playMessage.data as? IMediaServiceInterface
                releaseAllCachedMessages()
            }
        }
    }

    private fun releaseAllCachedMessages() {
        val newCacheList = synchronized(playMessageCache) {
            try {
                playMessageCache.toList()
            } finally {
                playMessageCache.clear()
            }
        }
        newCacheList.forEach { (playMessage) ->
            this.dispatch(playMessage)
        }
    }

    fun dispatch(playMessage: PlayMessage) {
        remoteDispatcherProxy.dispatch(playMessage)
    }
}