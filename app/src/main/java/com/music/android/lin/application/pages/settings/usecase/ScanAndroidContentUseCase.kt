package com.music.android.lin.application.pages.settings.usecase

import com.music.android.lin.application.pages.settings.usecase.scanner.MediaContentScanner
import com.music.android.lin.player.metadata.MediaInfo
import org.koin.core.annotation.Factory

class ScanAndroidContentUseCase(
    private val scannerAndroid: MediaContentScanner
) {

    suspend fun scanContentProvider(): List<MediaInfo> {
        return this.scannerAndroid.scan()
    }
}