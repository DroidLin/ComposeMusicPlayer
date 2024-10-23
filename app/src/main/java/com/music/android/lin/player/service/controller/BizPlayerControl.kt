package com.music.android.lin.player.service.controller

import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.service.player.datasource.DataSource
import com.music.android.lin.player.service.playlist.MediaPlayingList
import java.util.logging.Logger

/**
 * @author: liuzhongao
 * @since: 2024/10/23 22:50
 */
internal class BizPlayerControl(
    private val player: Player,
    private val mediaList: MediaPlayingList,
    private val dataSourceFactory: DataSource.Factory,
    private val logger: Logger
) : PlayerControl {

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
        TODO("Not yet implemented")
    }

    override fun skipToNext() {
        TODO("Not yet implemented")
    }

    override fun setResource(playList: PlayList, fromIndex: Int) {
        this.playResource(playList, fromIndex, false)
    }

    override fun playResource(playList: PlayList, fromIndex: Int, playWhenReady: Boolean) {
        this.mediaList.setResource(playList, fromIndex)

        val currentMediaInfo = this.mediaList.mediaInfo
        if (currentMediaInfo != null) {
            val dataSource = this.dataSourceFactory.create(currentMediaInfo)
            if (dataSource != null) {
                this.player.setDataSource(dataSource)
                if (playWhenReady) {
                    this.player.playOrResume()
                } else {
                    this.player.pause()
                }
            }
        }
    }
}