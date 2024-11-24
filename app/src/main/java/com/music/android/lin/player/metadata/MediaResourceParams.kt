package com.music.android.lin.player.metadata

import java.io.Serializable

interface MediaResource : Serializable

data class PositionalPlayMediaResource(
    val startPosition: Int
) : MediaResource {
    companion object {
        private const val serialVersionUID: Long = 7311919811729592607L
    }
}

data class CommonPlayMediaResource(
    val mediaInfoPlayList: MediaInfoPlayList,
    val startPosition: Int,
    val autoStart: Boolean = true,
) : MediaResource {
    companion object {
        private const val serialVersionUID: Long = -2923127246850405976L
    }
}