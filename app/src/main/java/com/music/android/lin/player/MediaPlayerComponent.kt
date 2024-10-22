package com.harvest.musicplayer

import com.harvest.common.services.ComponentInstaller
import com.harvest.common.services.IComponent
import com.music.android.lin.player.service.MediaServiceImpl

/**
 * @author liuzhongao
 * @since 2023/10/5 17:11
 */
class MediaPlayerComponent : IComponent {
    override fun installComponent(installer: ComponentInstaller) {
        installer.installLazy(MediaService::class.java) { MediaServiceImpl() }
    }
}