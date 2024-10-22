package com.music.android.lin

import android.content.Context
import com.music.android.lin.modules.installAccessTokenComponent
import com.music.android.lin.modules.installAppComponent
import com.music.android.lin.modules.playerModule
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

/**
 * @author: liuzhongao
 * @since: 2024/10/23 00:12
 */
object AppKoin {

    val koin: Koin get() = GlobalContext.get()

    val applicationContext: Context
        get() = this.koin.get(AppIdentifier.ApplicationContext)

    fun init(context: Context) {
        startKoin {
            modules(playerModule)
        }
        installAppComponent(context)
        installAccessTokenComponent("anonymous_user")
    }
}