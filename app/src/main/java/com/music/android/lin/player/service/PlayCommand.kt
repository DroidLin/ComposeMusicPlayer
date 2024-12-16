package com.music.android.lin.player.service

/**
 * @author: liuzhongao
 * @since: 2024/10/24 01:03
 */
internal object PlayCommand {

    const val PLAYER_INIT = 1
    const val SYNC_PLAY_HISTORY = 2
    const val PAUSE = 3
    const val PLAY_OR_RESUME = 4
    const val SKIP_TO_NEXT = 5
    const val SKIP_TO_PREV = 6
    const val SET_VOLUME = 7
    const val SET_DATA_SOURCE = 8
    const val SEEK_TO_POSITION = 9
    const val PLAYER_STOP = 10
    const val PLAYER_RELEASE = 11
    const val SET_PLAY_MODE = 12
    const val PLAY_RESOURCE = 14
    const val ATTACH_MEDIA_INTERFACE_HANDLE = 15
    const val UPDATE_PLAY_INFORMATION = 16
    const val SYNC_PLAY_METADATA = 17
}