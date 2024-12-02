package com.music.android.lin.modules

import android.content.Context
import com.music.android.lin.application.music.play.domain.LyricParser
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Property
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Single
import java.util.logging.Logger

@Module
internal class ApplicationModule {

    @Factory
    @Qualifier(name = AppIdentifier.applicationContext)
    fun applicationContext(
        @Property(AppIdentifier.applicationContext)
        context: Context
    ): Context = context

    @Single
    fun mediaService(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context
    ): MediaService = MediaService(context)

    @Factory
    fun mediaController(mediaService: MediaService): MediaController = mediaService.mediaController

    @Single
    fun appLogger(): Logger = Logger.getLogger("AppLogger")

    @Single
    @Qualifier(name = AppIdentifier.globalCoroutineScope)
    fun coroutineScope() = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @Factory
    fun lyricParser(): LyricParser = LyricParser()

    companion object {
        @JvmStatic
        fun customProperties(context: Context): Map<String, Any> = mapOf(
            AppIdentifier.applicationContext to context
        )
    }
}