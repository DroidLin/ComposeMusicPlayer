package com.music.android.lin.application.framework.router.impl

import com.music.android.lin.application.PageDeepLinks
import com.music.android.lin.application.framework.router.UriContext
import com.music.android.lin.application.framework.router.UriHandler
import org.koin.core.annotation.Factory

@Factory
class PlayerViewRouteHandler : UriHandler {
    override suspend fun handle(context: UriContext): Boolean {
        val dataUri = context.intent.data ?: return false
        if (dataUri.toString() != PageDeepLinks.PATH_PLAYER) {
            return false
        }
        context.appState.openAudioPlayerScreen()
        return true
    }
}