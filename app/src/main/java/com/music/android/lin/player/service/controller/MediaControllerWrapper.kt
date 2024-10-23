package com.music.android.lin.player.service.controller

import android.view.Surface
import com.music.android.lin.player.interfaces.MediaController
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.PlayInfo
import com.music.android.lin.player.metadata.PlayList
import com.music.android.lin.player.metadata.PlayMode

/**
 * @author liuzhongao
 * @since 2024/4/4 20:07
 */
class MediaControllerWrapper(private val mediaControllerGetter: () -> MediaController?) : MediaController {

    private val mediaController: MediaController?
        get() = this.mediaControllerGetter()

    override val playInfo: PlayInfo
        get() = this.mediaController?.playInfo ?: PlayInfo()

    private var _surfaceToken: String? = null

    override fun setSurface(token: String?, surface: Surface?) {
        // reset surface while token is different, skip
        if (token != this._surfaceToken && surface == null) {
            return
        }
        this.mediaController?.setSurface(token, surface)
        this._surfaceToken = token
    }

    override fun setPlayList(playList: PlayList?) {
        this.mediaController?.setPlayList(playList)
    }

    override fun setPlayList(playList: PlayList?, mediaInfo: MediaInfo?) {
        this.mediaController?.setPlayList(playList, mediaInfo)
    }

    override fun startPlay(playList: PlayList?, mediaInfo: MediaInfo?): Result<Boolean> {
        return this.mediaController?.startPlay(playList, mediaInfo) ?: Result.failure(NullPointerException("media controller is null."))
    }

    override fun setPlayMode(playMode: PlayMode) {
        this.mediaController?.setPlayMode(playMode)
    }

    override fun skipToPrevious(fromUser: Boolean) {
        this.mediaController?.skipToPrevious()
    }

    override fun skipToNext(fromUser: Boolean) {
        this.mediaController?.skipToNext()
    }

    override fun play() {
        this.mediaController?.play()
    }

    override fun pause() {
        this.mediaController?.pause()
    }

    override fun seekToPosition(position: Long) {
        this.mediaController?.seekToPosition(position)
    }

    override fun reset() {
        this.mediaController?.reset()
    }

    override fun stop() {
        this.mediaController?.stop()
    }
}