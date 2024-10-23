package com.music.android.lin.player.service.datasource

import android.net.Uri
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.service.player.Player

/**
 * @author liuzhongao
 * @since 2024/10/23 17:33
 */
internal class LocalFileDataSource(val mediaInfo: MediaInfo) : Player.DataSource {
    override fun tryGetResourceUri(): Uri? {
        val resourceUri = this.mediaInfo.sourceUri
        if (resourceUri.isNullOrEmpty()) {
            return null
        }
        if (!resourceUri.startsWith("file:///")) {
            return null
        }
        return Uri.parse(resourceUri)
    }
}