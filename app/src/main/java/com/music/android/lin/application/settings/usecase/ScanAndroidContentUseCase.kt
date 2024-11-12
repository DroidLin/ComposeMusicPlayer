package com.music.android.lin.application.settings.usecase

import com.music.android.lin.application.settings.usecase.scanner.MediaContentScanner
import com.music.android.lin.player.metadata.MediaInfo

class ScanAndroidContentUseCase(
    private val scannerAndroid: MediaContentScanner
) {

    suspend fun scanContentProvider(): List<MediaInfo> {
        return this.scannerAndroid.scan()
    }
}