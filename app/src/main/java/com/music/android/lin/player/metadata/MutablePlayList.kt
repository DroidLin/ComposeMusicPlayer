package com.music.android.lin.player.metadata

import com.music.android.lin.player.utils.ExtensionMap
import java.io.Serializable

/**
 * @author: liuzhongao
 * @since: 2024/10/23 21:18
 */
interface MutablePlayList {

    val id: String

    val type: PlayListType

    var name: String

    var description: String

    val mediaInfoList: MutableList<MediaInfo>

    val extensions: ExtensionMap?

    val updateTimeStamp: Long
}

fun MutablePlayList(playList: PlayList): MutablePlayList {
    return MutablePlayListImpl(
        id = playList.id,
        type = playList.type,
        name = playList.name,
        description = playList.description,
        mediaInfoList = playList.mediaInfoList.toMutableList(),
        extensions = playList.extensions,
        updateTimeStamp = playList.updateTimeStamp,
    )
}

fun PlayList.toMutable(): MutablePlayList {
    return MutablePlayList(this)
}

private data class MutablePlayListImpl(
    override val id: String,
    override val type: PlayListType,
    override var name: String,
    override var description: String,
    override val mediaInfoList: MutableList<MediaInfo>,
    override val extensions: ExtensionMap?,
    override val updateTimeStamp: Long
) : MutablePlayList, Serializable {
    companion object {
        private const val serialVersionUID: Long = -5727308029357464013L
    }
}