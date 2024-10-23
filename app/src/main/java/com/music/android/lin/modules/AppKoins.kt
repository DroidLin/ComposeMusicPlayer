package com.music.android.lin.modules

import android.content.Context

val AppKoin.applicationContext: Context
    get() = this.koin.get(AppIdentifier.ApplicationContext)