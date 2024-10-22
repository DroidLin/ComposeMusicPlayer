package com.harvest.musicplayer

import android.content.Context
import com.harvest.common.services.KServiceFacade
import com.harvest.musicplayer.repositories.MusicDatabaseModule
import com.harvest.musicplayer.service.controller.PlayerServiceControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author liuzhongao
 * @since 2023/10/13 9:31 PM
 */
@Module
@InstallIn(SingletonComponent::class)
object MusicPlayerHiltModule {

    @Provides
    @Singleton
    fun provideMusicDatabaseModule(@ApplicationContext context: Context): MusicDatabaseModule =
        MusicDatabaseModule(context, "key_anonymous_user")

    @Provides
    fun provideMusicService(): MediaService = KServiceFacade[MediaService::class.java]

    @Provides
    fun provideMusicController(mediaService: MediaService): MediaController {
        return mediaService.mediaController
    }

    @Provides
    fun providePlayerServiceController(musicDatabaseModule: MusicDatabaseModule): PlayerServiceController {
        return PlayerServiceControllerImpl(musicDatabaseModule.mediaRepository)
    }

    @Provides
    fun provideMusicRepository(musicDatabaseModule: MusicDatabaseModule): MediaRepository {
        return musicDatabaseModule.mediaRepository
    }
}