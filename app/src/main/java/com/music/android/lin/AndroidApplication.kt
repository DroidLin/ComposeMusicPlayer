package com.music.android.lin

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

val applicationContext: Context
    get() = requireNotNull(AndroidApplication.context)

internal class AndroidApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        context = this
    }

    companion object {
        private var applicationReference: WeakReference<Context>? = null
        internal var context: Context?
            set(value) { this.applicationReference = WeakReference(value) }
            get() = this.applicationReference?.get()
    }
}