package com.harvest.musicplayer

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

    val mediaInfoList: MutableList<MediaInfo>

    val extensions: ExtensionMap?

    val updateTimeStamp: Long

    companion object EmptyPlayList : PlayList {
        private const val serialVersionUID: Long = 3830854502781727616L
        override val id: String = ""
        override val type: PlayListType = PlayListType.UnknownType
        override val name: String = ""
        override val description: String = ""
        override val mediaInfoList: MutableList<MediaInfo> = ArrayList()
        override val extensions: ExtensionMap? = null
        override val updateTimeStamp: Long = 0L
    }
}