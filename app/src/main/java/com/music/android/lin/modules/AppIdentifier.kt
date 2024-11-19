package com.music.android.lin.modules

import org.koin.core.qualifier.named

/**
 * @author: liuzhongao
 * @since: 2024/10/22 23:08
 */
object AppIdentifier {

    const val applicationContext = "ApplicationContext"

    @JvmField
    val ApplicationContext = named("ApplicationContext")

    @JvmField
    val GlobalCoroutineScope = named("global_coroutine_scope")

    @JvmField
    val AndroidScanner = named("android_media_information_scanner")
}