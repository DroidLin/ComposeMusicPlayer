package com.music.android.lin.player.service

import com.music.android.lin.player.MessageDispatcher
import com.music.android.lin.player.metadata.command
import com.music.android.lin.player.metadata.data
import com.music.android.lin.player.service.metadata.PlayInformation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

class MediaServiceInformation {

    val flow = MutableSharedFlow<PlayInformation>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val dispatcher = MessageDispatcher { playMessage ->
        when (playMessage.command) {
            PlayCommand.UPDATE_PLAY_INFORMATION -> {
                val information = playMessage.data as? PlayInformation
                if (information != null) {
                    while (!flow.tryEmit(information)) {
                    }
                }
            }
        }
    }
}