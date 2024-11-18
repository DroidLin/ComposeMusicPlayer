package com.music.android.lin.player

import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.metadata.data
import com.music.android.lin.player.service.PlayCommand
import com.music.android.lin.player.service.controller.PlayEventLoop

fun interface MessageDispatcher {
    fun dispatch(playMessage: PlayMessage)
}

internal class MediaServiceInterfaceHandlerStub(
    private val dispatcher: MessageDispatcher,
) : IMediaServiceInterface.Stub() {

    override fun dispatch(playMessage: PlayMessage?) {
        playMessage ?: return
        dispatcher.dispatch(playMessage)
    }
}

fun IMediaServiceInterface.attachStubHandle(mediaServiceInterface: IMediaServiceInterface) {
    val playMessage = PlayMessage.ofCommand(PlayCommand.ATTACH_MEDIA_INTERFACE_HANDLE)
    playMessage.data = mediaServiceInterface
    this.dispatch(playMessage)
}