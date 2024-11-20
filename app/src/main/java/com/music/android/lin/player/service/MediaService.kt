package com.music.android.lin.player.service

import android.content.Context
import com.music.android.lin.player.MessageDispatcher
import com.music.android.lin.player.metadata.MediaResource
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.service.controller.MediaController
import com.music.android.lin.player.service.controller.ProxyMediaController
import com.music.android.lin.player.service.metadata.PlayInformation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MediaService {

    val isConnected: Boolean

    val mediaController: MediaController

    val information: StateFlow<PlayInformation>

    fun initService()

    fun addDispatcher(dispatcher: MessageDispatcher)

    fun removeDispatcher(dispatcher: MessageDispatcher)

    fun setMediaResourceParams(mediaResource: MediaResource)

    fun runCommand(
        command: Int,
        async: Boolean = true,
        customParams: PlayMessage.() -> Unit
    ): PlayMessage
}

internal fun MediaService(context: Context): MediaService = MediaServiceProcessor(context)

private class MediaServiceProcessor(context: Context) : MediaService {

    private val mediaServiceInterface by lazy { MediaServiceInterfaceWrapper(context) }
    private val mediaServiceInformation by lazy { MediaServiceInformation() }

    override val information: StateFlow<PlayInformation>
        get() = this.mediaServiceInformation.flow

    override val isConnected: Boolean
        get() = mediaServiceInterface.isConnected

    override val mediaController: MediaController by lazy {
        ProxyMediaController(
            dispatcher = { playMessage ->
                mediaServiceInterface.dispatch(playMessage)
            }
        )
    }

    init {
        this.mediaServiceInterface.addDispatcher(this.mediaServiceInformation.dispatcher)
    }

    override fun initService() {
        this.mediaServiceInterface.doConnect()
    }

    override fun addDispatcher(dispatcher: MessageDispatcher) {
        mediaServiceInterface.addDispatcher(dispatcher)
    }

    override fun removeDispatcher(dispatcher: MessageDispatcher) {
        mediaServiceInterface.removeDispatcher(dispatcher)
    }

    override fun setMediaResourceParams(mediaResource: MediaResource) {
        this.mediaController.playMediaResource(mediaResource)
    }

    override fun runCommand(
        command: Int,
        async: Boolean,
        customParams: PlayMessage.() -> Unit
    ): PlayMessage {
        val function = this.mediaServiceInterface::dispatch
        return PlayMessage.ofCommand(command).apply(customParams).apply(function)
    }

}