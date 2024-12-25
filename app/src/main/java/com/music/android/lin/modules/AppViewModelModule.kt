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
import com.music.android.lin.application.music.component.vm.SelectMediaInfoViewModel
import com.music.android.lin.application.music.play.domain.LyricParser
import com.music.android.lin.application.music.play.ui.vm.PlayerListViewModel
import com.music.android.lin.application.music.play.ui.vm.PlayerLyricViewModel
import com.music.android.lin.application.music.play.ui.vm.PlayerPageViewModel
import com.music.android.lin.application.music.playlist.ui.vm.PlayListDetailViewModel
import com.music.android.lin.application.music.single.ui.vm.SingleMusicViewModel
import com.music.android.lin.application.settings.ui.vm.AppSettingsViewModel
import com.music.android.lin.application.settings.usecase.SaveMediaInfoUseCase
import com.music.android.lin.application.settings.usecase.ScanAndroidContentUseCase
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController
import com.music.android.lin.widget.ui.vm.GlancePlayerViewModel
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Qualifier
import java.util.logging.Logger

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
    internal fun glancePlayerViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        mediaService: MediaService,
    ): GlancePlayerViewModel = GlancePlayerViewModel(context, mediaService)

    @Factory
    internal fun mediaRepositoryViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        deleteMediaInfoUseCase: DeleteMediaInfoUseCase,
        fetchMyPlayListUseCase: FetchMyPlayListUseCase,
        addMediaItemToPlayListUseCase: AddMediaItemToPlayListUseCase,
        createPlayListUseCase: CreatePlayListUseCase,
    ): MediaRepositoryViewModel = MediaRepositoryViewModel(
        context = context,
        deleteMediaInfoUseCase = deleteMediaInfoUseCase,
        fetchMyPlayListUseCase = fetchMyPlayListUseCase,
        addMediaItemToPlayListUseCase = addMediaItemToPlayListUseCase,
        createPlayListUseCase = createPlayListUseCase
    )

    @Factory
    internal fun playListDetailViewModel(
        savedStateHandle: SavedStateHandle,
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        fetchPlayListUseCase: FetchPlayListUseCase,
    ): PlayListDetailViewModel = PlayListDetailViewModel(savedStateHandle, context, fetchPlayListUseCase)

    @Factory
    internal fun playerPageViewModel(
        mediaService: MediaService,
        logger: Logger,
        @Qualifier(name = AppIdentifier.globalCoroutineScope)
        coroutineScope: CoroutineScope
    ): PlayerPageViewModel = PlayerPageViewModel(mediaService, logger, coroutineScope)

    @Factory
    internal fun playerLyricViewModel(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context,
        lyricParser: LyricParser
    ): PlayerLyricViewModel = PlayerLyricViewModel(context, lyricParser)

    @Factory
    internal fun selectMediaInfoViewModel(
        prepareMusicInfoUseCase: PrepareMusicInfoUseCase,
    ): SelectMediaInfoViewModel = SelectMediaInfoViewModel(prepareMusicInfoUseCase)

    @Factory
    internal fun playerListViewModel(
        mediaService: MediaService
    ): PlayerListViewModel = PlayerListViewModel(mediaService)
}