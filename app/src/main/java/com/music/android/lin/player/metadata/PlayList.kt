package com.music.android.lin.player.metadata

import com.music.android.lin.player.utils.ExtensionMap
import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/5 18:38
 */
interface PlayList : Serializable {

    val id: String

    val type: PlayListType

    val name: String

    val description: String

    val mediaInfoList: List<MediaInfo>

    val extensions: ExtensionMap?

    val updateTimeStamp: Long

    companion object EmptyPlayList : PlayList {
        private fun readResolve(): Any = EmptyPlayList
        private const val serialVersionUID: Long = 3830854502781727616L
        override val id: String = ""
        override val type: PlayListType = PlayListType.UnknownType
        override val name: String = ""
        override val description: String = ""
        override val mediaInfoList: List<MediaInfo> = emptyList()
        override val extensions: ExtensionMap? = null
        override val updateTimeStamp: Long = 0L
    }
}

fun PlayList(
    id: String,
    type: PlayListType,
    name: String,
    description: String,
    mediaInfoList: List<MediaInfo>,
    extensions: ExtensionMap?,
    updateTimeStamp: Long
): PlayList {
    return PlayListImpl(id, type, name, description, mediaInfoList, extensions, updateTimeStamp)
}

fun MutablePlayList.toImmutable(): PlayList {
    return PlayList(this)
}

fun PlayList(mutablePlayList: MutablePlayList): PlayList {
    return PlayList(
        id = mutablePlayList.id,
        type = mutablePlayList.type,
        name = mutablePlayList.name,
        description = mutablePlayList.description,
        mediaInfoList = mutablePlayList.mediaInfoList,
        extensions = mutablePlayList.extensions,
        updateTimeStamp = mutablePlayList.updateTimeStamp,
    )
}

private data class PlayListImpl(
    override val id: String,
    override val type: PlayListType,
    override val name: String,
    override val description: String,
    override val mediaInfoList: List<MediaInfo>,
    override val extensions: ExtensionMap?,
    override val updateTimeStamp: Long
) : PlayList {
    companion object {
        private const val serialVersionUID: Long = -6082097215268659880L
    }
}