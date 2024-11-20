package com.music.android.lin.player.service

import androidx.compose.runtime.MutableState
import com.music.android.lin.player.MessageDispatcher
import com.music.android.lin.player.metadata.command
import com.music.android.lin.player.metadata.data
import com.music.android.lin.player.service.metadata.PlayInformation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

class MediaServiceInformation {

    val flow = MutableStateFlow<PlayInformation>(PlayInformation())

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