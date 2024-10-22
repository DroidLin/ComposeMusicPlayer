package com.music.android.lin.player.interfaces

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

/**
 * @author liuzhongao
 * @since 2023/10/5 17:28
 */
interface MediaService {

    val musicServicePlaybackState: StateFlow<MusicServicePlaybackState>

    val mediaController: MediaController

    fun init(context: Context)
}