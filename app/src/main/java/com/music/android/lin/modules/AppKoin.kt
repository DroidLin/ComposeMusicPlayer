package com.music.android.lin.modules

import android.content.Context
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * @author: liuzhongao
 * @since: 2024/10/23 00:12
 */
object AppKoin {

    val koin: Koin get() = GlobalContext.get()

    fun init(context: Context) {
        startKoin {
            modules(
                playerModule,
                module {
                    single(AppIdentifier.ApplicationContext) { context } bind Context::class
                }
            )
        }
        installAccessTokenComponent("anonymous_user")
    }
}