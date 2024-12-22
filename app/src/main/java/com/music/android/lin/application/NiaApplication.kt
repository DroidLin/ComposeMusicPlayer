package com.music.android.lin.application

import android.app.Application
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.modules.mediaService

internal class NiaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppKoin.init(this)
        AppKoin.mediaService.initService()
    }
}