package com.music.android.lin.player.metadata

interface MediaResource

data class PositionalMediaSourceParameter(
    val startPosition: Int
) : MediaResource

data class CommonMediaResourceParameter(
    val playList: PlayList,
    val startPosition: Int,
    val autoStart: Boolean = true,
) : MediaResource