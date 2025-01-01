package com.music.android.lin.application.framework.router

import android.content.Context
import android.content.Intent

class UriContext(val intent: Intent)

interface UriHandler {

    suspend fun handle(context: UriContext): Boolean
}