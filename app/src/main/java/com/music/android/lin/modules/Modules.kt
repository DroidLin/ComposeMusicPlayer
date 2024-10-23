package com.music.android.lin.modules

import com.music.android.lin.player.PlayerIdentifier
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

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