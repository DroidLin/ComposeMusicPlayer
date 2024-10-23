package com.music.android.lin.player.service.playlist

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList

internal sealed interface MediaList {

    val playList: PlayList?

    /**
     * get current playing media info.
     */
    val mediaInfo: MediaInfo?

    val prevMediaInfo: MediaInfo?

    val nextMediaInfo: MediaInfo?
}