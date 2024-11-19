package com.music.android.lin.modules

import android.content.Context
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Property
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Single

@Module
class ApplicationModule {

    @Factory
    @Qualifier(name = AppIdentifier.applicationContext)
    fun applicationContext(
        @Property(AppIdentifier.applicationContext)
        context: Context
    ): Context = context

    @Single
    fun mediaService(
        @Named(AppIdentifier.applicationContext)
        context: Context
    ): MediaService = MediaService(context)

    @Factory
    fun mediaController(mediaService: MediaService): MediaController = mediaService.mediaController

    companion object {
        @JvmStatic
        fun customProperties(context: Context): Map<String, Any> = mapOf(
            AppIdentifier.applicationContext to context
        )
    }
}