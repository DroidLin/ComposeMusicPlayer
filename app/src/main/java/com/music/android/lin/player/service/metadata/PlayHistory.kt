package com.music.android.lin.player.service.metadata

import com.music.android.lin.player.metadata.MediaInfoPlayList
import com.music.android.lin.player.metadata.PlayMode
import java.io.Serializable

data class PlayHistory(
    val mediaInfoPlayList: MediaInfoPlayList,
    val indexOfCurrentPosition: Int,
    val playMode: PlayMode,
    val playingProgress: Long,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 8291727162882528075L
    }
}

