package com.music.android.lin.player.service.controller

import com.music.android.lin.player.service.metadata.PlayInformation
import kotlinx.coroutines.flow.Flow

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:46
 */
interface PlayInfo {

    val information: Flow<PlayInformation>

    fun synchronize()
}