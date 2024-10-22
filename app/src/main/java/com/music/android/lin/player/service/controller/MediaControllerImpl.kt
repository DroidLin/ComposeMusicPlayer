package com.harvest.musicplayer.service.controller

import android.content.Context
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.Looper
import android.view.Surface
import com.harvest.musicplayer.MediaController
import com.harvest.musicplayer.MediaDatabaseEntryPoint
import com.harvest.musicplayer.MediaInfo
import com.harvest.musicplayer.PlayInfo
import com.harvest.musicplayer.PlayList
import com.harvest.musicplayer.PlayMode
import com.harvest.musicplayer.R
import com.harvest.musicplayer.service.isUriAvailable
import com.harvest.musicplayer.service.metadata.MediaConfiguration
import com.harvest.musicplayer.service.player.MusicPlayerWrapper
import com.harvest.musicplayer.service.state.IMutablePlayerCenter
import com.harvest.musicplayer.service.strategy.PlayStrategyWrapper
import com.harvest.musicplayer.utils.isStoragePermissionGranted
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

/**
 * @author liuzhongao
 * @since 2023/10/9 11:45 PM
 */
internal class MediaControllerImpl constructor(
    private val context: Context,
    private val mutablePlayerCenter: IMutablePlayerCenter,
    private val playInfoGetter: () -> PlayInfo
) : Binder(), MediaController {

    private val mediaRepository = EntryPointAccessors.fromApplication<MediaDatabaseEntryPoint>(this.context).mediaRepository
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private val mainHandler = Handler(Looper.getMainLooper())
    private val playStrategy = PlayStrategyWrapper(this.mutablePlayerCenter)
    private val musicPlayer = MusicPlayerWrapper(this.mutablePlayerCenter)

    override val playInfo: PlayInfo get() = this.playInfoGetter()

    fun init(mediaConfig: MediaConfiguration) {
        this.playStrategy.switchPlayMode(mediaConfig.playMode)
        this.musicPlayer.switchPlayer(mediaConfig.playerType)
        if (isStoragePermissionGranted(context = this.context)) {
            this.startPlayInner(mediaConfig.playList, mediaConfig.mediaInfo, false)
        }
    }

    override fun setSurface(token: String?, surface: Surface?) {
        this.musicPlayer.setSurface(surface)
    }

    override fun setPlayList(playList: PlayList?) {
        this.playStrategy.setPlayListAndMusicInfo(
            playList = playList,
            mediaInfo = playList?.mediaInfoList?.firstOrNull()
        )
    }

    override fun setPlayList(playList: PlayList?, mediaInfo: MediaInfo?) {
        this.playStrategy.setPlayListAndMusicInfo(
            playList = playList,
            mediaInfo = mediaInfo
        )
    }

    override fun startPlay(playList: PlayList?, mediaInfo: MediaInfo?): Result<Boolean> {
        if (mediaInfo != null) {
            this.coroutineScope.launch {
                this@MediaControllerImpl.mediaRepository.recordPlayRecord(mediaInfo)
            }
        }
        return if (isStoragePermissionGranted(context = this.context)) {
            kotlin.runCatching { this.startPlayInner(playList, mediaInfo, true); true }
        } else Result.failure(RuntimeException(this.context.getString(R.string.string_file_permission_not_granted)))
    }

    private fun startPlayInner(playList: PlayList?, mediaInfo: MediaInfo?, autoStart: Boolean) {
        this.setPlayList(playList = playList, mediaInfo = mediaInfo)

        val songSourceUriString = mediaInfo?.sourceUri ?: ""
        val uri = Uri.parse(songSourceUriString)
        this.reset()
        if (songSourceUriString.isNotEmpty() && isUriAvailable(uri = uri)) {
            this.musicPlayer.setDataSource(uri)
            if (autoStart) {
                this.musicPlayer.prepareWithAutoStart()
            } else this.musicPlayer.prepareWithoutStart()
        }
    }

    override fun setPlayMode(playMode: PlayMode) {
        this.playStrategy.switchPlayMode(playMode = playMode)
    }

    override fun skipToPrevious(fromUser: Boolean) {
        this.skipToPreviousInner(fromUser = fromUser)
    }

    private fun skipToPreviousInner(fromUser: Boolean) {
        val playList = this.playStrategy.playList
        val nextMusicInfo = this.playStrategy.previousMusicInfo()
        if (playList == null || nextMusicInfo == null) {
            return
        }
        this.startPlay(playList = playList, mediaInfo = nextMusicInfo)
    }

    override fun skipToNext(fromUser: Boolean) {
        this.skipToNextInner(fromUser = fromUser)
    }

    private fun skipToNextInner(fromUser: Boolean) {
        val playList = this.playStrategy.playList
        val nextMusicInfo = this.playStrategy.nextMusicInfo()
        if (playList == null || nextMusicInfo == null) {
            return
        }
        this.startPlay(playList = playList, mediaInfo = nextMusicInfo)
    }

    override fun play() {
        this.musicPlayer.play()
    }

    override fun pause() {
        this.musicPlayer.pause()
    }

    override fun seekToPosition(position: Long) {
//        this.musicPlayer.pause()
        this.musicPlayer.seekToPosition(position = position)
//        this.musicPlayer.play()
    }

    override fun reset() {
        this.musicPlayer.reset()
    }

    override fun stop() {
        this.playStrategy.setMusicInfo(mediaInfo = null)
        this.musicPlayer.stop()
    }

    fun release() {
        this.musicPlayer.release()
    }
}