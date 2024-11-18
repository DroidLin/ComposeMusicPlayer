package com.music.android.lin.player.notification

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.service.controller.MediaController
import com.music.android.lin.player.service.controller.PlayInfo
import com.music.android.lin.player.utils.collectWithScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * @author liuzhongao
 * @since 2023/10/18 12:09 AM
 */
internal class PlayMediaSession(
    private val context: Context,
    private val mediaController: MediaController,
    private val playInfo: PlayInfo,
    private val coroutineScope: CoroutineScope
) {

    private val mediaSessionActions = PlaybackState.ACTION_PLAY or
            PlaybackState.ACTION_PAUSE or
            PlaybackState.ACTION_PLAY_PAUSE or
            PlaybackState.ACTION_SKIP_TO_NEXT or
            PlaybackState.ACTION_SKIP_TO_PREVIOUS or
            PlaybackState.ACTION_STOP or
            PlaybackState.ACTION_SEEK_TO
    private val mediaSession = MediaSession(this.context, "PlayMediaSession")
    private val mediaSessionCallback = object : MediaSession.Callback() {

        override fun onPause() {
            this@PlayMediaSession.mediaController.pause()
        }

        override fun onPlay() {
            this@PlayMediaSession.mediaController.playOrResume()
        }

        override fun onSkipToNext() {
            this@PlayMediaSession.mediaController.skipToNext()
        }

        override fun onSkipToPrevious() {
            this@PlayMediaSession.mediaController.skipToPrevious()
        }

        override fun onStop() {
            this@PlayMediaSession.mediaController.stop()
        }

        override fun onSeekTo(pos: Long) {
            this@PlayMediaSession.mediaController.seekToPosition(position = pos)
        }
    }

    val mediaSessionToken: MediaSession.Token
        get() = this.mediaSession.sessionToken

    init {
        this.mediaSession.setCallback(this.mediaSessionCallback)
        this.mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)
        this.mediaSession.isActive = true

        this.playInfo.information
            .map { information ->
                MediaSessionMetadata(
                    numberOfMediaInfo = information.playList?.mediaInfoList?.size ?: 0,
                    mediaInfo = information.mediaInfo,
                    isPlaying = information.playerMetadata.isPlaying,
                    contentProgress = information.playerMetadata.contentProgress,
                    contentDuration = information.playerMetadata.contentDuration
                )
            }
            .distinctUntilChanged()
            .onEach(::updateMediaSessionInfo)
            .collectWithScope(this.coroutineScope)
    }

    private suspend fun updateMediaSessionInfo(metadata: MediaSessionMetadata) {
        val musicInfo = metadata.mediaInfo
        if (musicInfo != null) {
            val numOfPlayList = metadata.numberOfMediaInfo
            this.updateMediaMetadata(
                mediaInfo = musicInfo,
                duration = metadata.contentDuration,
                numOfMusicInfoInPlayList = numOfPlayList.toLong()
            )
            this.updateMediaSessionPlaybackState(
                isPlaying = metadata.isPlaying,
                playProgress = metadata.contentProgress,
                playingSpeed = 1f
            )
        } else {
            this.mediaSession.setMetadata(MediaMetadata.Builder().build())
            this.mediaSession.setPlaybackState(PlaybackState.Builder().build())
        }
    }

    private suspend fun updateMediaMetadata(
        mediaInfo: MediaInfo,
        duration: Long,
        numOfMusicInfoInPlayList: Long
    ) {
        val mediaMetadata = this.buildMediaSessionMetadata(
            mediaInfo = mediaInfo,
            duration = duration,
            numOfPlayList = numOfMusicInfoInPlayList
        )
        this.mediaSession.setMetadata(mediaMetadata)
    }

    private suspend fun updateMediaSessionPlaybackState(
        isPlaying: Boolean,
        playProgress: Long,
        playingSpeed: Float = 1f
    ) {
        val mediaSessionPlaybackState = this.buildMediaSessionPlaybackState(
            isPlaying = isPlaying,
            playProgress = playProgress,
            playbackSpeed = playingSpeed
        )
        this.mediaSession.setPlaybackState(mediaSessionPlaybackState)
    }

    private suspend fun buildMediaSessionPlaybackState(
        isPlaying: Boolean,
        playProgress: Long,
        playbackSpeed: Float = 1f
    ): PlaybackState {
        return PlaybackState.Builder()
            .setActions(this.mediaSessionActions)
            .setState(
                if (isPlaying) PlaybackState.STATE_PLAYING else PlaybackState.STATE_PAUSED,
                playProgress,
                playbackSpeed
            )
            .build()
    }

    private suspend fun buildMediaSessionMetadata(
        mediaInfo: MediaInfo,
        duration: Long,
        numOfPlayList: Long
    ): MediaMetadata {
        val bitmapArt = this.loadCoverBitmap(mediaInfo = mediaInfo)
        return MediaMetadata.Builder()
            .putString(MediaMetadata.METADATA_KEY_TITLE, mediaInfo.mediaTitle)
            .putString(
                MediaMetadata.METADATA_KEY_ARTIST,
                mediaInfo.artists.joinToString("/") { it.name })
            .putString(MediaMetadata.METADATA_KEY_ALBUM, mediaInfo.album.albumName)
            .putBitmap(MediaMetadata.METADATA_KEY_ART, bitmapArt)
            .putBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART, bitmapArt)
            .putLong(MediaMetadata.METADATA_KEY_DURATION, duration)
            .putLong(MediaMetadata.METADATA_KEY_TRACK_NUMBER, numOfPlayList)
            .build()
    }

    private suspend fun loadCoverBitmap(mediaInfo: MediaInfo): Bitmap? = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            val imageResourceUrl = mediaInfo.coverUri
            if (imageResourceUrl.isNullOrEmpty()) {
                return@runCatching null
            }
            fetchImageBitmap(imageResourceUrl = imageResourceUrl)
        }.onFailure { it.printStackTrace() }.getOrNull()
    }

    private data class MediaSessionMetadata(
        val numberOfMediaInfo: Int,
        val mediaInfo: MediaInfo?,
        val isPlaying: Boolean,
        val contentProgress: Long,
        val contentDuration: Long,
    )
}