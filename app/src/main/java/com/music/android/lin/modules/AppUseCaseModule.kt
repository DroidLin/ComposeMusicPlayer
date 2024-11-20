package com.music.android.lin.modules

import android.content.Context
import com.music.android.lin.application.settings.usecase.SaveMediaInfoUseCase
import com.music.android.lin.application.settings.usecase.ScanAndroidContentUseCase
import com.music.android.lin.application.settings.usecase.scanner.MediaContentScanner
import com.music.android.lin.application.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.repositories.DatabaseService
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Qualifier

@Module
class AppUseCaseModule {

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
}