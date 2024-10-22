package com.music.android.lin.player.metadata

import com.harvest.musicplayer.ExtensionMap
import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.PlayList
import com.harvest.musicplayer.PlayListType

/**
 * @author liuzhongao
 * @since 2023/10/6 14:10
 */
internal data class PlayListImpl(
    override val id: String,
    override val type: PlayListType,
    override val name: String,
    override val description: String,
    override val mediaInfoList: MutableList<MediaInfo>,
    override val extensions: ExtensionMap?,
    override val updateTimeStamp: Long
) : PlayList {
    companion object {
        private const val serialVersionUID: Long = -6082097215268659880L
    }
}