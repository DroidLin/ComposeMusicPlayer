package com.harvest.musicplayer.service.metadata

import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.PlayList
import com.harvest.musicplayer.PlayMode
import com.harvest.musicplayer.PlayerType
import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/9 11:43 PM
 */
internal data class MediaConfiguration(
    val playList: PlayList?,
    val mediaInfo: MediaInfo?,
    val playingProgress: Long,
    val playingDuration: Long,
    val playMode: PlayMode = PlayMode.PlayListLoop,
    val playerType: PlayerType = PlayerType.System,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = -6432447408224978115L

        @JvmField
        val Default = MediaConfiguration(
            playList = null,
            mediaInfo = null,
            playingProgress = -1,
            playingDuration = -1,
            playerType = PlayerType.System
        )
    }
}