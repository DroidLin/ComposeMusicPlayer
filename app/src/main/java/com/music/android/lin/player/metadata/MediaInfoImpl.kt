package com.music.android.lin.player.metadata

import com.music.android.lin.player.interfaces.Album
import com.music.android.lin.player.interfaces.Artist
import com.music.android.lin.player.interfaces.MediaExtras
import com.music.android.lin.player.interfaces.MediaInfo
import com.music.android.lin.player.interfaces.MediaType

/**
 * @author liuzhongao
 * @since 2023/10/6 14:15
 */
internal data class MediaInfoImpl(
    override val id: String,
    override val mediaTitle: String,
    override val mediaDescription: String,
    override val artists: List<Artist>,
    override val album: Album,
    override val coverUri: String?,
    override val sourceUri: String?,
    override val updateTimeStamp: Long,
    override val mediaType: MediaType,
    override val mediaExtras: MediaExtras
) : MediaInfo {
    companion object {
        private const val serialVersionUID: Long = -8145443343434315122L
    }
}