package com.music.android.lin.application.framework.router

import android.content.Context
import android.content.Intent
import com.music.android.lin.application.framework.NiaAppState
import com.music.android.lin.application.framework.router.impl.AudioContentUriHandler
import com.music.android.lin.application.framework.router.impl.PlayerViewRouteHandler
import com.music.android.lin.modules.AppKoin
import org.koin.core.annotation.Factory
import java.util.logging.Logger

object Router {

    private val routerModule by lazy { AppKoin.get<RouterModule>() }

    suspend fun handleIntent(intent: Intent, appState: NiaAppState): Boolean {
        val uriContext = UriContext(intent, appState)
        return routerModule.handle(uriContext)
    }
}

@Factory
internal class RouterModule(
    private val logger: Logger,
    audioContentUriHandler: AudioContentUriHandler,
    playerViewRouteHandler: PlayerViewRouteHandler,
) : UriHandler {

    private val handlers: List<UriHandler> = listOf(audioContentUriHandler, playerViewRouteHandler)

    override suspend fun handle(context: UriContext): Boolean {
        logger.info("launch router handle")
        var handled = false
        for (handler in handlers) {
            logger.info("handle by ${handler.javaClass.name}")
            handled = handler.handle(context)
            logger.info("handle result: $handled")
            if (handled) break
        }
        logger.info("end router handle")
        return handled
    }
}