package com.music.android.lin.player.interfaces

import java.io.File
import java.io.Serial
import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/16 10:40
 */
class MediaStoreUpdateOptions private constructor(
    val contentProviderOptions: ContentProviderOptions? = null,
    val fileScannerOptions: FileScannerOptions? = null,
    val videoContentProviderOptions: VideoContentProviderOptions? = null
) : Serializable {

    class Builder {
        private var contentProviderOptions: ContentProviderOptions? = null
        private var fileScannerOptions: FileScannerOptions? = null
        private var videoContentProviderOptions: VideoContentProviderOptions? = null

        constructor()
        constructor(mediaStoreUpdateOptions: MediaStoreUpdateOptions) {
            this.contentProviderOptions = mediaStoreUpdateOptions.contentProviderOptions
            this.fileScannerOptions = mediaStoreUpdateOptions.fileScannerOptions
        }

        fun contentProviderOptions(contentProviderOptions: ContentProviderOptions?) = apply {
            this.contentProviderOptions = contentProviderOptions
        }

        fun fileScannerOptions(fileScannerOptions: FileScannerOptions?) = apply {
            this.fileScannerOptions = fileScannerOptions
        }

        fun videoContentProviderOptions(videoContentProviderOptions: VideoContentProviderOptions?) = apply {
            this.videoContentProviderOptions = videoContentProviderOptions
        }

        fun build(): MediaStoreUpdateOptions {
            return MediaStoreUpdateOptions(
                contentProviderOptions = this.contentProviderOptions,
                fileScannerOptions = this.fileScannerOptions,
                videoContentProviderOptions = videoContentProviderOptions
            )
        }
    }

    companion object {
        private const val serialVersionUID: Long = 1382323793858422283L
    }
}

class ContentProviderOptions(
    val filterDuration: Long,
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 5328184595906018157L
    }
}

data class VideoContentProviderOptions(
    val filterDuration: Long
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 7292818984698806709L
    }
}

class FileScannerOptions(
    val filterDuration: Long,
    val includeFileDirectory: List<File>
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 7228233776977119893L
    }
}