package com.music.android.lin.modules

import com.music.android.lin.application.framework.vm.AppFrameworkViewModel
import com.music.android.lin.application.framework.vm.AppMusicFrameworkViewModel
import com.music.android.lin.application.framework.vm.AppNavigationViewModel
import com.music.android.lin.application.guide.ui.vm.MediaInformationScannerViewModel
import com.music.android.lin.application.minibar.audio.ui.vm.AudioMinibarViewModel
import com.music.android.lin.application.music.ui.vm.SingleMusicViewModel
import com.music.android.lin.application.settings.ui.vm.AppSettingsViewModel
import com.music.android.lin.application.settings.usecase.SaveMediaInfoUseCase
import com.music.android.lin.application.settings.usecase.ScanAndroidContentUseCase
import com.music.android.lin.application.settings.usecase.scanner.MediaContentScanner
import com.music.android.lin.application.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.application.usecase.PrepareUserPersonalInformationUseCase
import com.music.android.lin.player.PlayerIdentifier
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.repositories.DatabaseService
import com.music.android.lin.player.repositories.MusicDatabaseService
import org.koin.dsl.module

val viewModelModule = module {
    factory {
        MediaInformationScannerViewModel(
            scanAndroidContentUseCase = get(),
            saveMediaInfoUseCase = get(),
            savedStateHandle = get()
        )
    }
    factory {
        AppFrameworkViewModel(
            applicationContext = get(AppIdentifier.ApplicationContext)
        )
    }
    factory {
        AppNavigationViewModel(
            applicationContext = get(AppIdentifier.ApplicationContext)
        )
    }
    factory {
        AppMusicFrameworkViewModel(
            applicationContext = get(AppIdentifier.ApplicationContext)
        )
    }
    factory {
        SingleMusicViewModel(
            prepareMusicInfoUseCase = get(),
            mediaController = get()
        )
    }
    factory {
        AppSettingsViewModel(
            applicationContext = get(AppIdentifier.ApplicationContext)
        )
    }
    factory {
        AudioMinibarViewModel(
            mediaService = get(),
            mediaController = get()
        )
    }
}

val databaseModule = module {
    single<DatabaseService> {
        MusicDatabaseService(
            applicationContext = get(AppIdentifier.ApplicationContext),
            accessToken = get(PlayerIdentifier.PlayerDatabaseAccessToken)
        )
    }
    factory<MediaRepository> {
        get<DatabaseService>().mediaRepository
    }
}

val useCaseModule = module {
    factory {
        PrepareUserPersonalInformationUseCase(
            applicationContext = get(AppIdentifier.ApplicationContext)
        )
    }
    factory {
        PrepareMusicInfoUseCase(
            applicationContext = get(AppIdentifier.ApplicationContext),
            mediaRepository = get()
        )
    }
    factory {
        SaveMediaInfoUseCase(
            databaseService = get()
        )
    }
    factory {
        ScanAndroidContentUseCase(
            scannerAndroid = get(AppIdentifier.AndroidScanner)
        )
    }
}

val scannerModule = module {
    factory(AppIdentifier.AndroidScanner) {
        MediaContentScanner(context = get(AppIdentifier.ApplicationContext))
    }
}