package com.music.android.lin.player

import android.os.Handler
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier

/**
 * @author liuzhongao
 * @since 2024/11/21 14:23
 */
@Module
class PlayServiceLifecycleModule {

    @Factory
    fun playerHandler(
        @Qualifier(name = PlayerIdentifier.playServiceHandlerThread)
        handler: Handler
    ): Handler = handler

    companion object {
        @JvmStatic
        val unInstallPropertyKeys: List<String>
            get() = listOf(PlayerIdentifier.playServiceHandlerThread)

        @JvmStatic
        fun customParams(handler: Handler): Map<String, Any> {
            return mapOf(PlayerIdentifier.playServiceHandlerThread to handler)
        }
    }
}