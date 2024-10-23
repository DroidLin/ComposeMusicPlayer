package com.music.android.lin.player.service.controller

import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:46
 */
interface PlayerControl {

    fun seekToPosition(position: Long)

    fun playOrResume()

    fun pause()

    fun stop()

    fun skipToPrevious()

    fun skipToNext()

    fun setPlayMode(playMode: PlayMode)

    fun playResource(playList: PlayList, fromIndex: Int, playWhenReady: Boolean)

    fun setResource(playList: PlayList, fromIndex: Int)
}