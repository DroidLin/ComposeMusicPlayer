package com.harvest.musicplayer

import android.view.Surface

/**
 * @author liuzhongao
 * @since 2023/10/9 14:21
 */
interface MediaController {

    val playInfo: PlayInfo

    fun setSurface(token: String?, surface: Surface?)

    fun setPlayList(playList: PlayList?)

    fun setPlayList(playList: PlayList?, mediaInfo: MediaInfo?)

    fun startPlay(playList: PlayList?, mediaInfo: MediaInfo?): Result<Boolean>

    fun setPlayMode(playMode: PlayMode)

    fun skipToPrevious(fromUser: Boolean = true)

    fun skipToNext(fromUser: Boolean = true)

    fun play()

    fun pause()

    fun seekToPosition(position: Long)

    fun reset()

    fun stop()
}