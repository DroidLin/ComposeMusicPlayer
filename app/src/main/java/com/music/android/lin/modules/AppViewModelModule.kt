package com.music.android.lin.modules

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.music.android.lin.application.framework.vm.AppFrameworkViewModel
import com.music.android.lin.application.framework.vm.AppMusicFrameworkViewModel
import com.music.android.lin.application.framework.vm.AppNavigationViewModel
import com.music.android.lin.application.guide.ui.vm.MediaInformationScannerViewModel
import com.music.android.lin.application.minibar.audio.ui.vm.AudioMinibarViewModel
import com.music.android.lin.application.music.ui.vm.SingleMusicViewModel
import com.music.android.lin.application.settings.ui.vm.AppSettingsViewModel
import com.music.android.lin.application.settings.usecase.SaveMediaInfoUseCase
import com.music.android.lin.application.settings.usecase.ScanAndroidContentUseCase
import com.music.android.lin.application.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named

@Module
class AppViewModelModule {

    @Factory
    internal fun mediaInformationScannerViewModel(
        scanAndroidContentUseCase: ScanAndroidContentUseCase,
        saveMediaInfoUseCase: SaveMediaInfoUseCase,
        savedStateHandle: SavedStateHandle
    ): MediaInformationScannerViewModel {
        return MediaInformationScannerViewModel(
            scanAndroidContentUseCase = scanAndroidContentUseCase,
            saveMediaInfoUseCase = saveMediaInfoUseCase,
            savedStateHandle = savedStateHandle
        )
    }

    @Factory
    internal fun appFrameworkViewModel(
        @Named(AppIdentifier.applicationContext)
        context: Context
    ): AppFrameworkViewModel = AppFrameworkViewModel(context)

    @Factory
    internal fun appNavigationViewModel(
        @Named(AppIdentifier.applicationContext)
        context: Context
    ): AppNavigationViewModel = AppNavigationViewModel(context)

    @Factory
    internal fun appMusicFrameworkViewModel(
        @Named(AppIdentifier.applicationContext)
        context: Context
    ): AppMusicFrameworkViewModel = AppMusicFrameworkViewModel(context)

    @Factory
    internal fun singleMusicViewModel(
        prepareMusicInfoUseCase: PrepareMusicInfoUseCase,
        mediaController: MediaController
    ): SingleMusicViewModel = SingleMusicViewModel(prepareMusicInfoUseCase, mediaController)

    @Factory
    internal fun appSettingsViewModel(
        @Named(AppIdentifier.applicationContext)
        context: Context
    ): AppSettingsViewModel = AppSettingsViewModel(context)

    @Factory
    internal fun audioMinibarViewModel(
        mediaService: MediaService,
        mediaController: MediaController,
    ): AudioMinibarViewModel = AudioMinibarViewModel(mediaService, mediaController)
}