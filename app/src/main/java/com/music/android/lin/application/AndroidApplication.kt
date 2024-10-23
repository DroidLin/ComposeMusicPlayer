package com.music.android.lin.application

import android.app.Application
import com.music.android.lin.modules.AppKoin

internal class AndroidApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppKoin.init(this)
    }
}