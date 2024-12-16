package com.music.android.lin.player.service.controller

import com.music.android.lin.player.metadata.CommonPlayMediaResource
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.MediaResource
import com.music.android.lin.player.metadata.MediaInfoPlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.metadata.PositionalPlayMediaResource
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.service.player.datasource.DataSource
import com.music.android.lin.player.service.playlist.MediaPlayingList
import java.util.logging.Logger

class BizMediaController(
    private val player: Player,
    private val mediaList: MediaPlayingList,
    private val dataSourceFactory: DataSource.Factory,
    private val logger: Logger,
) : MediaController {

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
        this.player.sync()
    }

    override fun skipToPrevious() {
        this.skipToPrevious(fromUser = true)
    }

    override fun skipToPrevious(fromUser: Boolean) {
        val previousMediaInfo = this.mediaList.tryGetPreviousMediaInfo(fromUser)
        this.playResourceInner(previousMediaInfo, true)
    }

    override fun skipToNext() {
        this.skipToNext(fromUser = true)
    }

    override fun skipToNext(fromUser: Boolean) {
        val nextMediaInfo = this.mediaList.tryGetNextMediaInfo(fromUser)
        this.playResourceInner(nextMediaInfo, true)
    }

    override fun release() {
        this.logger.info("deprecated release call.")
    }

    override fun playMediaResource(mediaResource: MediaResource) {
        when (mediaResource) {
            is PositionalPlayMediaResource -> {
                val playList = this.mediaList.mediaInfoPlayList
                if (playList != null) {
                    this.playResource(
                        mediaInfoPlayList = playList,
                        fromIndex = mediaResource.startPosition,
                        playWhenReady = true
                    )
                }
            }

            is CommonPlayMediaResource -> {
                this.playResource(
                    mediaInfoPlayList = mediaResource.mediaInfoPlayList,
                    fromIndex = mediaResource.startPosition,
                    playWhenReady = mediaResource.autoStart
                )
            }

            else -> {
                this.logger.info("unrecognized mediaResource: ${mediaResource.javaClass.name}")
            }
        }
    }

    private fun playResource(mediaInfoPlayList: MediaInfoPlayList, fromIndex: Int, playWhenReady: Boolean) {
        this.mediaList.setResource(mediaInfoPlayList, fromIndex)

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