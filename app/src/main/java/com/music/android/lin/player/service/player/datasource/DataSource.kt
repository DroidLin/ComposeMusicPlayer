package com.music.android.lin.player.service.player.datasource

import android.net.Uri
import com.music.android.lin.player.metadata.MediaInfo

fun interface DataSource {

    fun tryGetResourceUri(): Uri?

    fun interface Factory {

        fun create(mediaInfo: MediaInfo): DataSource?
    }
}
