package com.music.android.lin.player.service.metadata

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2024/10/24 13:55
 */
data class PlayInformation(
    val playList: PlayList? = null,
    val mediaInfo: MediaInfo? = null,
    val playMode: PlayMode = PlayMode.PlayListLoop,
    val playerMetadata: PlayerMetadata = PlayerMetadata()
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 6922798811344408584L
    }
}
