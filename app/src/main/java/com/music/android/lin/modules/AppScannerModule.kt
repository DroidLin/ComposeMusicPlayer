package com.music.android.lin.modules

import android.content.Context
import com.music.android.lin.application.pages.settings.usecase.scanner.MediaContentScanner
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Qualifier

@Module
internal class AppScannerModule {

    @Factory
    @Qualifier(name = AppIdentifier.androidScanner)
    internal fun mediaContentScanner(
        @Qualifier(name = AppIdentifier.applicationContext)
        context: Context
    ): MediaContentScanner = MediaContentScanner(context)

}