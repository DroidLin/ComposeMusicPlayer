package com.music.android.lin.player.metadata

import com.music.android.lin.player.interfaces.MediaInfo
import com.music.android.lin.player.interfaces.PlayRecord
import com.music.android.lin.player.interfaces.RecentPlayItem

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