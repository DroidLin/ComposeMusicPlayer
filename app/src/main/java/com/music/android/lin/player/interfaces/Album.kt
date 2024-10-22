package com.music.android.lin.player.interfaces

import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/5 23:13
 */
interface Album : Serializable {

    val id: String

    val albumName: String

    val albumDescription: String

    val publishDate: Long

    val mediaNumber: Int

    val coverUrl: String

    companion object Empty : Album {
        private const val serialVersionUID: Long = -181157103833628741L
        override val id: String get() = ""
        override val albumName: String get() = "默认专辑"
        override val albumDescription: String get() = ""
        override val publishDate: Long get() = 0
        override val mediaNumber: Int get() = 0
        override val coverUrl: String get() = ""
    }
}