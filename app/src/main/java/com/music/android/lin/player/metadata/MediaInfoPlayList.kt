package com.music.android.lin.player.metadata

import com.music.android.lin.player.utils.ExtensionMap

/**
 * @author liuzhongao
 * @since 2023/10/5 18:38
 */
interface MediaInfoPlayList : PlayList {

    val mediaInfoList: List<MediaInfo>

    companion object EmptyMediaInfoPlayList : MediaInfoPlayList {
        private fun readResolve(): Any = EmptyMediaInfoPlayList
        private const val serialVersionUID: Long = 3830854502781727616L
        override val id: String = ""
        override val type: PlayListType = PlayListType.UnknownType
        override val name: String = ""
        override val description: String = ""
        override val playListCover: String? = ""
        override val mediaInfoList: List<MediaInfo> = emptyList()
        override val extensions: ExtensionMap = ExtensionMap()
        override val updateTimeStamp: Long = 0L
        override val countOfPlayable: Int = 0
    }
}

fun MediaInfoPlayList(
    id: String,
    type: PlayListType,
    name: String,
    description: String,
    playListCover: String?,
    mediaInfoList: List<MediaInfo>,
    extensions: ExtensionMap,
    updateTimeStamp: Long,
    countOfPlayable: Int
): MediaInfoPlayList {
    return MediaInfoPlayListImpl(
        id = id,
        type = type,
        name = name,
        description = description,
        playListCover = playListCover,
        mediaInfoList = mediaInfoList,
        extensions = extensions,
        updateTimeStamp = updateTimeStamp,
        countOfPlayable = countOfPlayable
    )
}

fun MutableMediaInfoPlayList.toImmutable(): MediaInfoPlayList {
    return MediaInfoPlayList(this)
}

fun MediaInfoPlayList(mutableMediaInfoPlayList: MutableMediaInfoPlayList): MediaInfoPlayList {
    return MediaInfoPlayList(
        id = mutableMediaInfoPlayList.id,
        type = mutableMediaInfoPlayList.type,
        name = mutableMediaInfoPlayList.name,
        description = mutableMediaInfoPlayList.description,
        playListCover = mutableMediaInfoPlayList.playListCover,
        mediaInfoList = mutableMediaInfoPlayList.mediaInfoList,
        extensions = mutableMediaInfoPlayList.extensions,
        updateTimeStamp = mutableMediaInfoPlayList.updateTimeStamp,
        countOfPlayable = mutableMediaInfoPlayList.countOfPlayable
    )
}

private data class MediaInfoPlayListImpl(
    override val id: String,
    override val type: PlayListType,
    override val name: String,
    override val description: String,
    override val playListCover: String?,
    override val mediaInfoList: List<MediaInfo>,
    override val extensions: ExtensionMap,
    override val updateTimeStamp: Long,
    override val countOfPlayable: Int
) : MediaInfoPlayList {
    companion object {
        private const val serialVersionUID: Long = -6082097215268659880L
    }
}