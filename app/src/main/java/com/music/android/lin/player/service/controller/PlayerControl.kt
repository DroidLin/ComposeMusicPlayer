package com.music.android.lin.player.service.controller

import androidx.annotation.FloatRange
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.metadata.PlayInformation
import kotlinx.coroutines.flow.Flow

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:46
 */
interface PlayerControl {

    val information: Flow<PlayInformation>

    fun setVolume(@FloatRange(from = 0.0, to = 1.0) volumeLevel: Float)

    fun seekToPosition(position: Long)

    fun playOrResume()

    fun pause()

    fun stop()

    fun skipToPrevious()

    fun skipToNext()

    fun release()

    fun setPlayMode(playMode: PlayMode)

    fun playResource(playList: PlayList, fromIndex: Int, playWhenReady: Boolean)

    fun setResource(playList: PlayList, fromIndex: Int)
}