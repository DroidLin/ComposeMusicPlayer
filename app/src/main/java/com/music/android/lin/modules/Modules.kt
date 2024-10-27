package com.music.android.lin.modules

import com.music.android.lin.application.ui.composables.music.vm.SingleMusicViewModel
import com.music.android.lin.application.ui.vm.AppFrameworkViewModel
import com.music.android.lin.application.ui.vm.AppMusicFrameworkViewModel
import com.music.android.lin.application.ui.vm.AppNavigationViewModel
import com.music.android.lin.application.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.application.usecase.PrepareUserPersonalInformationUseCase
import com.music.android.lin.player.PlayerIdentifier
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.repositories.DatabaseService
import com.music.android.lin.player.repositories.MusicDatabaseService
import org.koin.dsl.module

val viewModelModule = module {
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
            prepareMusicInfoUseCase = get()
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
}