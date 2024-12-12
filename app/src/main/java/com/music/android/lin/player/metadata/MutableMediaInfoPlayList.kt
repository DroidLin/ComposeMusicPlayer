package com.music.android.lin.player.metadata

import com.music.android.lin.player.utils.ExtensionMap
import java.io.Serializable

/**
 * @author: liuzhongao
 * @since: 2024/10/23 21:18
 */
interface MutableMediaInfoPlayList : MediaInfoPlayList {

    abstract override val id: String

    abstract override val type: PlayListType

    abstract override var name: String

    abstract override var description: String

    abstract override val mediaInfoList: MutableList<MediaInfo>

    abstract override val extensions: ExtensionMap

    abstract override val updateTimeStamp: Long

    abstract override val countOfPlayable: Int
}

fun MutableMediaInfoPlayList(mediaInfoPlayList: MediaInfoPlayList): MutableMediaInfoPlayList {
    return MutableMediaInfoPlayListImpl(
        id = mediaInfoPlayList.id,
        type = mediaInfoPlayList.type,
        name = mediaInfoPlayList.name,
        description = mediaInfoPlayList.description,
        playListCover=  mediaInfoPlayList.playListCover,
        mediaInfoList = mediaInfoPlayList.mediaInfoList.toMutableList(),
        extensions = mediaInfoPlayList.extensions,
        updateTimeStamp = mediaInfoPlayList.updateTimeStamp,
        countOfPlayable = mediaInfoPlayList.countOfPlayable
    )
}

fun MediaInfoPlayList.toMutable(): MutableMediaInfoPlayList {
    return MutableMediaInfoPlayList(this)
}

private data class MutableMediaInfoPlayListImpl(
    override val id: String,
    override val type: PlayListType,
    override var name: String,
    override var description: String,
    override val playListCover: String?,
    override val mediaInfoList: MutableList<MediaInfo>,
    override val extensions: ExtensionMap,
    override val updateTimeStamp: Long,
    override val countOfPlayable: Int
) : MutableMediaInfoPlayList, Serializable {
    companion object {
        private const val serialVersionUID: Long = -5727308029357464013L
    }
}