package com.music.android.lin.player.interfaces

import java.io.File

/**
 * @author liuzhongao
 * @since 2023/10/16 2:59 PM
 */
@DslMarker
annotation class MediaStoreUpdateOptionsDsl

@MediaStoreUpdateOptionsDsl
class MediaStoreUpdateOptionsScope {

    private var fileScannerOptions: FileScannerOptions? = null
    private var contentProviderOptions: ContentProviderOptions? = null
    private var videoContentProviderOptions: VideoContentProviderOptions? = null

    fun fileScannerOptions(block: FileScannerOptionsScope.() -> Unit) {
        this.fileScannerOptions = FileScannerOptionsScope().also(block).build()
    }

    fun contentProviderOptions(block: ContentProviderOptionsScope.() -> Unit) {
        this.contentProviderOptions = ContentProviderOptionsScope().also(block).build()
    }

    fun videoContentProviderOptions(block: VideoContentProviderOptionsScope.() -> Unit) {
        this.videoContentProviderOptions = VideoContentProviderOptionsScope().apply(block).build()
    }

    fun build(): MediaStoreUpdateOptions {
        return MediaStoreUpdateOptions.Builder()
            .contentProviderOptions(this.contentProviderOptions)
            .fileScannerOptions(this.fileScannerOptions)
            .videoContentProviderOptions(this.videoContentProviderOptions)
            .build()
    }
}

@MediaStoreUpdateOptionsDsl
class FileScannerOptionsScope {
    var includeFileDirectory: List<File>? = null
    var filterDuration: Long = 0

    fun build(): FileScannerOptions {
        return FileScannerOptions(filterDuration, includeFileDirectory ?: emptyList())
    }
}

@MediaStoreUpdateOptionsDsl
class ContentProviderOptionsScope {

    var filterDuration: Long = 0

    fun build(): ContentProviderOptions {
        return ContentProviderOptions(filterDuration)
    }
}

@MediaStoreUpdateOptionsDsl
class VideoContentProviderOptionsScope {
    var videoFilterDuration: Long = 0
    fun build(): VideoContentProviderOptions {
        return VideoContentProviderOptions(filterDuration = this.videoFilterDuration)
    }
}