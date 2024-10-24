package com.music.android.lin.player.service.controller

import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.metadata.PlayInformation
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.service.player.datasource.DataSource
import com.music.android.lin.player.service.playlist.MediaPlayingList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.logging.Logger

internal class BizPlayerControl(
    private val player: Player,
    private val mediaList: MediaPlayingList,
    private val dataSourceFactory: DataSource.Factory,
    private val logger: Logger,
    private val coroutineScope: CoroutineScope
) : PlayerControl {

    override val information: Flow<PlayInformation> = combine(
        this.player.playerMetadata,
        this.mediaList.metadata
    ) { playerMetadata, mediaMetadata ->
        PlayInformation(
            playList = mediaMetadata.playList,
            mediaInfo = mediaMetadata.mediaInfo,
            playMode = mediaMetadata.playMode,
            playerMetadata = playerMetadata
        )
    }.distinctUntilChanged()

    override fun setVolume(volumeLevel: Float) {
        val fixedVolume = volumeLevel.coerceIn(0f, 1f)
        this.player.setVolume(fixedVolume)
    }

    override fun seekToPosition(position: Long) {
        this.player.seekToPosition(position)
    }

    override fun playOrResume() {
        this.player.playOrResume()
    }

    override fun pause() {
        this.player.pause()
    }

    override fun stop() {
        this.player.stop()
    }

    override fun setPlayMode(playMode: PlayMode) {
        this.mediaList.setPlayMode(playMode)
    }

    override fun skipToPrevious() {
        val currentMediaInfo = this.mediaList.prevMediaInfo
        this.playResourceInner(currentMediaInfo, true)
    }

    override fun skipToNext() {
        val currentMediaInfo = this.mediaList.nextMediaInfo
        this.playResourceInner(currentMediaInfo, true)
    }

    override fun release() {

    }

    override fun setResource(playList: PlayList, fromIndex: Int) {
        this.playResource(playList, fromIndex, false)
    }

    override fun playResource(playList: PlayList, fromIndex: Int, playWhenReady: Boolean) {
        this.mediaList.setResource(playList, fromIndex)

        val currentMediaInfo = this.mediaList.mediaInfo
        this.playResourceInner(currentMediaInfo, playWhenReady)
    }

    private fun playResourceInner(mediaInfo: MediaInfo?, playWhenReady: Boolean): Boolean {
        if (mediaInfo == null) {
            return false
        }
        val dataSource = this.dataSourceFactory.create(mediaInfo) ?: return false
        try {
            return this.player.setDataSource(dataSource)
        } finally {
            if (playWhenReady) {
                this.player.playOrResume()
            } else {
                this.player.pause()
            }
        }
    }
}