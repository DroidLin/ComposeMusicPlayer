package com.music.android.lin.player

import android.app.Service
import android.net.Uri
import com.music.android.lin.player.service.PlayService
import com.music.android.lin.player.service.player.datasource.DataSource
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Property
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton
import java.io.File

/**
 * @author liuzhongao
 * @since 2024/11/21 13:53
 */
@Module
internal class PlayServiceModule {

    @Singleton
    fun dataSourceFactory(): DataSource.Factory {
        return DataSource.Factory {
            val sourceUri = it.sourceUri
            if (sourceUri.isNullOrEmpty()) {
                return@Factory null
            }
            if (sourceUri.startsWith("content")) {
                return@Factory DataSource { Uri.parse(sourceUri) }
            }
            if (sourceUri.startsWith("file:///")) {
                return@Factory DataSource { Uri.parse(sourceUri) }
            }
            if (sourceUri.startsWith("/")) {
                val file = File(sourceUri)
                return@Factory DataSource { Uri.fromFile(file) }
            }
            return@Factory null
        }
    }

    @Singleton
    @Qualifier(name = PlayerIdentifier.playerCoroutineScope)
    fun coroutineScope(
        @Property(PlayerIdentifier.playerCoroutineScope)
        coroutineScope: CoroutineScope
    ): CoroutineScope = coroutineScope

    @Factory
    @Qualifier(name = PlayerIdentifier.playService)
    fun playService(
        @Property(PlayerIdentifier.playService)
        service: PlayService
    ): Service = service

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