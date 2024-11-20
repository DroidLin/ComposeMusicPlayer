package com.music.android.lin.modules

import android.content.Context
import com.music.android.lin.player.PlayerIdentifier
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.repositories.DatabaseService
import com.music.android.lin.player.repositories.MusicDatabaseService
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Property
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Single

@Module
class AppDatabaseModule {

    @Single
    fun databaseService(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        @Property(PlayerIdentifier.playerDatabaseAccessToken)
        accessToken: String
    ): DatabaseService = MusicDatabaseService(context, accessToken)

    @Factory
    fun mediaRepository(
        databaseService: DatabaseService
    ): MediaRepository = databaseService.mediaRepository

    companion object {
        @JvmStatic
        fun customProperties(accessToken: String): Map<String, Any> = mapOf(
            PlayerIdentifier.playerDatabaseAccessToken to accessToken
        )
    }
}