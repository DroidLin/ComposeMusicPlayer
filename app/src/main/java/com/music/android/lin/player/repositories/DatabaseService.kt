package com.music.android.lin.player.repositories

import com.music.android.lin.player.interfaces.MediaRepository

/**
 * @author: liuzhongao
 * @since: 2024/10/22 22:54
 */
interface DatabaseService {

    val mediaRepository: MediaRepository

    fun release()
}