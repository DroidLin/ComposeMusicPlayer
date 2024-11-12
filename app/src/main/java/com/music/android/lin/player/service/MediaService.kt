package com.music.android.lin.player.service

import com.music.android.lin.player.metadata.MediaResource
import com.music.android.lin.player.metadata.PlayMessage
import com.music.android.lin.player.service.controller.MediaController

internal interface MediaService {

    val mediaController: MediaController

    fun init()

    fun setMediaResourceParams(mediaResource: MediaResource)

    fun runCommand(
        command: Int,
        async: Boolean = true,
        customParams: PlayMessage.() -> Unit
    ): PlayMessage
}