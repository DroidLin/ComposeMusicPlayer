package com.music.android.lin.player.service.controller

import com.music.android.lin.player.service.metadata.PlayInformation
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.service.playlist.MediaPlayingList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class)
internal class BizPlayInfo(
    private val player: Player,
    private val mediaList: MediaPlayingList,
) : PlayInfo {

    override val information: Flow<PlayInformation> = combine(
        this.player.playerMetadata,
        this.mediaList.metadata
    ) { playerMetadata, mediaMetadata ->
        PlayInformation(
            mediaInfoPlayList = mediaMetadata.mediaInfoPlayList,
            mediaInfo = mediaMetadata.mediaInfo,
            playMode = mediaMetadata.playMode,
            playerMetadata = playerMetadata,
            indexOfCurrentMediaInfo = mediaMetadata.indexOfCurrentMediaInfo
        )
    }
        .distinctUntilChanged()
        .debounce(16L)

    override fun synchronize() {
        this.player.sync()
    }
}