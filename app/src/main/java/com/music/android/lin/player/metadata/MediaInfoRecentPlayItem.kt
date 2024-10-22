package com.harvest.musicplayer.metadata

import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.PlayRecord
import com.harvest.musicplayer.RecentPlayItem

/**
 * @author liuzhongao
 * @since 2024/4/6 13:11
 */
data class MediaInfoRecentPlayItem(
    val playRecord: PlayRecord,
    val mediaInfo: MediaInfo
) : RecentPlayItem {
    override val id: Long get() = playRecord.id
}