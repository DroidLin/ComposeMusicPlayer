package com.music.android.lin.player

import com.music.android.lin.player.service.PlayService
import com.music.android.lin.player.service.player.datasource.DataSource
import com.music.android.lin.player.service.player.datasource.LocalFileDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton

/**
 * @author liuzhongao
 * @since 2024/11/21 13:53
 */
@Module
internal class PlayServiceModule {

    @Singleton
    fun dataSourceFactory(): DataSource.Factory {
        return DataSource.Factory {
            LocalFileDataSource(it)
        }
    }

    @Singleton
    fun coroutineScope(
        @Qualifier(name = PlayerIdentifier.playerCoroutineScope)
        coroutineScope: CoroutineScope
    ): CoroutineScope = coroutineScope

    @Factory
    fun playService(
        @Qualifier(name = PlayerIdentifier.playService)
        service: PlayService
    ): PlayService = service

    companion object {
        @JvmStatic
        val unInstallPropertiesKeys: List<String>
            get() = listOf(PlayerIdentifier.playService, PlayerIdentifier.playerCoroutineScope)

        @JvmStatic
        fun customParams(service: PlayService, coroutineScope: CoroutineScope): Map<String, Any> {
            return mapOf(
                PlayerIdentifier.playService to service,
                PlayerIdentifier.playerCoroutineScope to coroutineScope
            )
        }
    }
}