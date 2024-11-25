package com.music.android.lin.modules

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.music.android.lin.application.common.ui.vm.MediaRepositoryViewModel
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.common.usecase.AddMediaItemToPlayListUseCase
import com.music.android.lin.application.common.usecase.CreatePlayListUseCase
import com.music.android.lin.application.common.usecase.DeleteMediaInfoUseCase
import com.music.android.lin.application.common.usecase.FetchMyPlayListUseCase
import com.music.android.lin.application.common.usecase.FetchPlayListUseCase
import com.music.android.lin.application.common.usecase.MediaResourceGeneratorUseCase
import com.music.android.lin.application.common.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.application.framework.vm.AppFrameworkViewModel
import com.music.android.lin.application.framework.vm.AppMusicFrameworkViewModel
import com.music.android.lin.application.framework.vm.AppNavigationViewModel
import com.music.android.lin.application.guide.ui.vm.MediaInformationScannerViewModel
import com.music.android.lin.application.minibar.ui.vm.MinibarViewModel
import com.music.android.lin.application.music.play.ui.vm.PlayerPageViewModel
import com.music.android.lin.application.music.playlist.ui.vm.PlayListDetailViewModel
import com.music.android.lin.application.music.single.ui.vm.SingleMusicViewModel
import com.music.android.lin.application.settings.ui.vm.AppSettingsViewModel
import com.music.android.lin.application.settings.usecase.SaveMediaInfoUseCase
import com.music.android.lin.application.settings.usecase.ScanAndroidContentUseCase
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier

@Module
internal class AppViewModelModule {

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
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context
    ): AppFrameworkViewModel = AppFrameworkViewModel(context)

    @Factory
    internal fun appNavigationViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context
    ): AppNavigationViewModel = AppNavigationViewModel(context)

    @Factory
    internal fun appMusicFrameworkViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context
    ): AppMusicFrameworkViewModel = AppMusicFrameworkViewModel(context)

    @Factory
    internal fun singleMusicViewModel(
        prepareMusicInfoUseCase: PrepareMusicInfoUseCase,
        mediaResourceGeneratorUseCase: MediaResourceGeneratorUseCase,
        mediaController: MediaController,
    ): SingleMusicViewModel = SingleMusicViewModel(
        prepareMusicInfoUseCase = prepareMusicInfoUseCase,
        mediaResourceGeneratorUseCase = mediaResourceGeneratorUseCase,
        mediaController = mediaController
    )

    @Factory
    internal fun appSettingsViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context
    ): AppSettingsViewModel = AppSettingsViewModel(context)

    @Factory
    internal fun minibarViewModel(
        mediaService: MediaService,
    ): MinibarViewModel = MinibarViewModel(mediaService)

    @Factory
    internal fun playControllerViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        mediaResourceGeneratorUseCase: MediaResourceGeneratorUseCase,
        mediaService: MediaService,
        mediaController: MediaController,
    ): PlayViewModel =
        PlayViewModel(context, mediaResourceGeneratorUseCase, mediaService, mediaController)

    @Factory
    internal fun mediaRepositoryViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        deleteMediaInfoUseCase: DeleteMediaInfoUseCase,
        fetctMyPlayListUseCase: FetchMyPlayListUseCase,
        addMediaItemToPlayListUseCase: AddMediaItemToPlayListUseCase,
        createPlayListUseCase: CreatePlayListUseCase,
    ): MediaRepositoryViewModel = MediaRepositoryViewModel(
        context = context,
        deleteMediaInfoUseCase = deleteMediaInfoUseCase,
        fetchMyPlayListUseCase = fetctMyPlayListUseCase,
        addMediaItemToPlayListUseCase = addMediaItemToPlayListUseCase,
        createPlayListUseCase = createPlayListUseCase
    )

    @Factory
    internal fun playListDetailViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        fetchPlayListUseCase: FetchPlayListUseCase,
    ): PlayListDetailViewModel = PlayListDetailViewModel(context, fetchPlayListUseCase)

    @Factory
    internal fun playerPageViewModel(
        mediaService: MediaService,
    ): PlayerPageViewModel = PlayerPageViewModel(mediaService)
}