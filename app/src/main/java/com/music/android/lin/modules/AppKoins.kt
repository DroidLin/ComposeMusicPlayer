package com.music.android.lin.modules

import android.content.Context
import com.music.android.lin.player.service.MediaService

val AppKoin.applicationContext: Context
    get() = this.get(AppIdentifier.Qualifier.ApplicationContext)

internal val AppKoin.mediaService: MediaService
    get() = this.get<MediaService>()