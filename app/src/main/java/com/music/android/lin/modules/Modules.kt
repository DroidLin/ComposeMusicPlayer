package com.music.android.lin.modules

import android.content.Context
import com.music.android.lin.AppIdentifier
import com.music.android.lin.player.PlayerIdentifier
import com.music.android.lin.player.repositories.MusicDatabaseService
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

private var appComponent: Module? = null

@Synchronized
fun installAppComponent(context: Context) {
    if (appComponent != null) return
    val appComponent = module {
        single(AppIdentifier.ApplicationContext) { context } bind Context::class
    }
    com.music.android.lin.modules.appComponent = appComponent
    loadKoinModules(appComponent)
}

@Synchronized
fun uninstallAppComponent() {
    val appComponent = appComponent ?: return
    unloadKoinModules(appComponent)
}

val playerModule = module {
    single {
        MusicDatabaseService(
            applicationContext = get(AppIdentifier.ApplicationContext),
            accessToken = get(PlayerIdentifier.PlayerDatabaseAccessToken)
        )
    }
}

private var accessTokenModule: Module? = null

@Synchronized
fun installAccessTokenComponent(accessToken: String) {
    if (accessTokenModule != null) return
    val appComponent = module {
        single(PlayerIdentifier.PlayerDatabaseAccessToken) { accessToken } bind String::class
    }
    accessTokenModule = appComponent
    loadKoinModules(appComponent)
}

@Synchronized
fun uninstallAccessTokenComponent() {
    val accessTokenModule = accessTokenModule ?: return
    unloadKoinModules(accessTokenModule)
}