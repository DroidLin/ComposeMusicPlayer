package com.music.android.lin.modules

import org.koin.core.annotation.Module

/**
 * as a matter of fact, we don`t need to write this manually,
 * this is used to tell how many component we have been generated
 * through koin-dependencies-injection tool kit.
 */
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