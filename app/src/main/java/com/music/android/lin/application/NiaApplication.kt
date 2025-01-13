package com.music.android.lin.application

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.music.android.lin.application.images.setupImageFactories
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.modules.mediaService

internal class NiaApplication : Application() {

    private val isDebuggable: Boolean
        get() = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    override fun onCreate() {
        super.onCreate()
        setRestrictModePolicy()
        AppKoin.init(this)
        AppKoin.mediaService.initService()
        setupImageFactories()
    }

    private fun setRestrictModePolicy() {
        if (isDebuggable) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )

            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }
}