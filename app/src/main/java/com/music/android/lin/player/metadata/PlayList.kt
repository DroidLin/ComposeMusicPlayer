package com.music.android.lin.player.metadata

import com.music.android.lin.player.utils.ExtensionMap
import java.io.Serializable

interface PlayList : Serializable {

    val id: String

    val type: PlayListType

    val name: String

    val description: String

    val playListCover: String?

    val extensions: ExtensionMap

    val updateTimeStamp: Long

    val countOfPlayable: Int

    enum class SortType(val value: Int) {
        Default(0),
        Title(1),
        Artist(2),
        AddOrder(3),
        AddOrderDesc(4);

        companion object {
            @JvmStatic
            fun fromValue(value: Int): SortType {
                return when (value) {
                    Default.value -> Default
                    Title.value -> Title
                    Artist.value -> Artist
                    AddOrder.value -> AddOrder
                    AddOrderDesc.value -> AddOrderDesc
                    else -> Default
                }
            }
        }
    }
}

fun PlayList(
    id: String,
    type: PlayListType,
    name: String,
    playListCover: String?,
    description: String,
    extensions: ExtensionMap,
    updateTimeStamp: Long,
    countOfPlayable: Int,
): PlayList {
    return PlayListImpl(
        id = id,
        type = type,
        name = name,
        playListCover = playListCover,
        description = description,
        extensions = extensions,
        updateTimeStamp = updateTimeStamp,
        countOfPlayable = countOfPlayable
    )
}


private data class PlayListImpl(
    override val id: String,
    override val type: PlayListType,
    override val name: String,
    override val description: String,
    override val playListCover: String?,
    override val extensions: ExtensionMap,
    override val updateTimeStamp: Long,
    override val countOfPlayable: Int,
) : PlayList {
    companion object {
        private const val serialVersionUID: Long = -4768866654400842990L
    }
}