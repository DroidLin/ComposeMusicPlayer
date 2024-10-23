package com.music.android.lin.player.metadata

import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/9 17:03
 */
data class SimplifiedPlayInfo(
    val playListId: String?,
    val musicInfoId: String?,
    val playingPosition: Int = -1,

    val isPlaying: Boolean,
    val currentDuration: Long,
    val currentProgress: Long,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 3319428040875577178L
    }
}