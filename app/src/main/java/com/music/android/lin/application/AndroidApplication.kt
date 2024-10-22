package com.music.android.lin.application

import android.app.Application
import android.content.Context
import com.music.android.lin.AppIdentifier
import com.music.android.lin.AppKoin
import java.lang.ref.WeakReference

val applicationContext: Context
    get() = AppKoin.koin.get(AppIdentifier.ApplicationContext)

internal class AndroidApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        context = this
    }

    override fun onCreate() {
        super.onCreate()
        AppKoin.init(this)
    }

    companion object {
        private var applicationReference: WeakReference<Context>? = null
        internal var context: Context?
            set(value) { applicationReference = WeakReference(value) }
            get() = applicationReference?.get()
    }
}