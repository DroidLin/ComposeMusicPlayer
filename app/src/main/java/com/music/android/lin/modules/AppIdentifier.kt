package com.music.android.lin.modules

import org.koin.core.qualifier.named

/**
 * @author: liuzhongao
 * @since: 2024/10/22 23:08
 */
object AppIdentifier {

    const val applicationContext = "ApplicationContext"
    const val androidScanner = "android_media_information_scanner"

    object Qualifier {
        @JvmField
        val ApplicationContext = named(applicationContext)
        @JvmField
        val GlobalCoroutineScope = named("global_coroutine_scope")
        @JvmField
        val AndroidScanner = named(androidScanner)
    }
}