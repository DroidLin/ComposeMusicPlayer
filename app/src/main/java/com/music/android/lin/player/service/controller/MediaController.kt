package com.music.android.lin.player.service.controller

import androidx.annotation.FloatRange
import com.music.android.lin.player.metadata.MediaResource
import com.music.android.lin.player.metadata.PlayMode

interface MediaController {

    fun setVolume(@FloatRange(from = 0.0, to = 1.0) volumeLevel: Float)

    fun seekToPosition(position: Long)

    fun playOrResume()

    fun pause()

    fun stop()

    fun skipToPrevious()

    fun skipToNext()

    fun release()

    fun setPlayMode(playMode: PlayMode)

    fun playMediaResource(mediaResource: MediaResource)
}
