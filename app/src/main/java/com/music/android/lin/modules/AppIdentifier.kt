package com.music.android.lin.modules

import org.koin.core.qualifier.named

/**
 * @author: liuzhongao
 * @since: 2024/10/22 23:08
 */
object AppIdentifier {

    @JvmField
    val ApplicationContext = named("ApplicationContext")
    const val applicationContext = "ApplicationContext"

    @JvmField
    val GlobalCoroutineScope = named("global_coroutine_scope")

    @JvmField
    val AndroidScanner = named("android_media_information_scanner")
    const val androidScanner = "android_media_information_scanner"

}