package com.music.android.lin.modules

import android.content.Context
import com.music.android.lin.application.common.usecase.AddMediaItemToPlayListUseCase
import com.music.android.lin.application.common.usecase.CreatePlayListUseCase
import com.music.android.lin.application.common.usecase.DeleteMediaInfoUseCase
import com.music.android.lin.application.common.usecase.FetchMyPlayListUseCase
import com.music.android.lin.application.common.usecase.FetchPlayListMediaInfoUseCase
import com.music.android.lin.application.common.usecase.FetchPlayListUseCase
import com.music.android.lin.application.common.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.application.common.usecase.MediaResourceGeneratorUseCase
import com.music.android.lin.application.settings.usecase.SaveMediaInfoUseCase
import com.music.android.lin.application.settings.usecase.ScanAndroidContentUseCase
import com.music.android.lin.application.settings.usecase.scanner.MediaContentScanner
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.repositories.DatabaseService
import com.music.android.lin.player.service.MediaService
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier

@Module
internal class AppUseCaseModule {

    @Factory
    internal fun prepareMusicInfoUseCase(
        mediaRepository: MediaRepository,
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
    ): PrepareMusicInfoUseCase = PrepareMusicInfoUseCase(mediaRepository, context)

    @Factory
    internal fun saveMediaInfoUseCase(
        databaseService: DatabaseService
    ): SaveMediaInfoUseCase = SaveMediaInfoUseCase(databaseService)

    @Factory
    internal fun scanAndroidContentUseCase(
        @Qualifier(name = AppIdentifier.androidScanner)
        mediaContentScanner: MediaContentScanner
    ): ScanAndroidContentUseCase = ScanAndroidContentUseCase(mediaContentScanner)

    @Factory
    internal fun deleteMediaInfoUseCase(
        mediaRepository: MediaRepository,
    ): DeleteMediaInfoUseCase = DeleteMediaInfoUseCase(mediaRepository)

    @Factory
    internal fun fetchMyPlayListUseCase(
        mediaRepository: MediaRepository,
    ): FetchMyPlayListUseCase = FetchMyPlayListUseCase(mediaRepository)

    @Factory
    internal fun addMediaItemToPlaylistUseCase(
        mediaRepository: MediaRepository,
    ): AddMediaItemToPlayListUseCase = AddMediaItemToPlayListUseCase(mediaRepository)

    @Factory
    internal fun createPlayListUseCase(
        mediaRepository: MediaRepository,
    ): CreatePlayListUseCase = CreatePlayListUseCase(mediaRepository)

    @Factory
    internal fun fetchPlayListMediaInfoUseCase(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        mediaRepository: MediaRepository,
        playListId: String,
    ): FetchPlayListMediaInfoUseCase = FetchPlayListMediaInfoUseCase(context, mediaRepository, playListId)

    @Factory
    internal fun fetchMediaInfoPlayListUseCase(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        mediaRepository: MediaRepository,
    ): FetchPlayListUseCase = FetchPlayListUseCase(context, mediaRepository)

    @Factory
    internal fun resourceGeneratorUseCase(
        mediaService: MediaService,
        mediaRepository: MediaRepository,
    ): MediaResourceGeneratorUseCase = MediaResourceGeneratorUseCase(mediaService, mediaRepository)


}