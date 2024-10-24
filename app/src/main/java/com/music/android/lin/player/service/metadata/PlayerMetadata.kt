package com.music.android.lin.player.service.metadata

import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2024/10/24 15:28
 */
data class PlayerMetadata(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val contentProgress: Long = 0,
    val contentDuration: Long = 0,
    val volume: Float = 0f,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1367031966649527805L
    }
}
