package com.music.android.lin.player.service.metadata

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2024/10/24 19:54
 */
data class MediaListMetadata(
    val playList: PlayList? = null,
    val mediaInfo: MediaInfo? = null,
    val indexOfCurrentMediaInfo: Int = -1,
    val playMode: PlayMode = PlayMode.PlayListLoop
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = -5093034838294868313L
    }
}
