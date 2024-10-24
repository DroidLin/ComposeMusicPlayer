package com.music.android.lin.modules

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
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
                module {
                    single<Context>(AppIdentifier.ApplicationContext) {
                        context
                    }
                    single<CoroutineScope>(AppIdentifier.GlobalCoroutineScope) {
                        CoroutineScope(Dispatchers.Default + SupervisorJob())
                    }
                }
            )
        }
        installAccessTokenComponent("anonymous_user")
    }
}