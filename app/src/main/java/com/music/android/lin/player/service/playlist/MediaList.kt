package com.music.android.lin.player.service.playlist

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaInfoPlayList

internal sealed interface MediaList {

    val mediaInfoPlayList: MediaInfoPlayList?

    /**
     * get current playing media info.
     */
    val mediaInfo: MediaInfo?

    val prevMediaInfo: MediaInfo?

    val nextMediaInfo: MediaInfo?
}