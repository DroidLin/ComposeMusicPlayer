package com.music.android.lin.application.framework.router

import android.content.Intent
import com.music.android.lin.application.framework.NiaAppState

class UriContext(val intent: Intent, val appState: NiaAppState)

interface UriHandler {

    suspend fun handle(context: UriContext): Boolean
}