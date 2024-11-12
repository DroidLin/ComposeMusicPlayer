package com.music.android.lin.player

import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.service.controller.IPlayerService

internal class MediaServiceInterfaceHandlerStub(
    private val playerService: () -> IPlayerService?
) : IMediaServiceInterface.Stub() {

    override fun dispatchSync(message: PlayMessage?) {
        message ?: return
        this.playerService()?.syncDispatch(message)
    }

    override fun dispatchAsync(message: PlayMessage?) {
        message ?: return
        this.playerService()?.asyncDispatch(message)
    }
}