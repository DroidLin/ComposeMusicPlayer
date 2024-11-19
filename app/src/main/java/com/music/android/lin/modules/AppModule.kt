package com.music.android.lin.modules

import org.koin.core.annotation.Module

@Module(
    includes = [
        ApplicationModule::class,
        AppDatabaseModule::class,
        AppUseCaseModule::class,
        AppViewModelModule::class,
        AppScannerModule::class
    ]
)
internal object AppModule