package com.music.android.lin.modules

import android.content.Context
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

/**
 * @author: liuzhongao
 * @since: 2024/10/23 00:12
 */
object AppKoin {

    val koin: Koin get() = GlobalContext.get()

    fun init(context: Context) {
        startKoin {
            properties(
                ApplicationModule.customProperties(context) + AppDatabaseModule.customProperties("anonymous_user")
            )
            modules(AppModule.module)
        }
    }
}