package com.harvest.musicplayer.service

import com.harvest.persistence.interfaces.AbsPreference

/**
 * @author liuzhongao
 * @since 2023/10/10 10:33
 */
internal object PlayProcessPersistence : AbsPreference() {
    override val localFileName: String = "PlayService"
}