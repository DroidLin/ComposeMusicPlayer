package com.music.android.lin.player.audiofocus

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import com.music.android.lin.player.service.controller.PlayerControl
import com.music.android.lin.player.service.player.Player
import com.music.android.lin.player.utils.collectWithScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * @author liuzhongao
 * @since 2023/10/23 12:05 AM
 */
internal class PlayerAudioFocusManager(
    private val context: Context,
    private val playerControl: PlayerControl,
    private val coroutineScope: CoroutineScope
) {

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                this@PlayerAudioFocusManager.playerControl.playOrResume()
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                this@PlayerAudioFocusManager.playerControl.pause()
            }
        }
    }
    private val audioManager = this.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    init {
        this.playerControl.information
            .map { it.playerMetadata.isPlaying }
            .distinctUntilChanged()
            .onEach { isPlaying ->
                if (isPlaying) {
                    tryLockAudioFocus()
                } else {
                    tryReleaseAudioFocus()
                }
            }
            .collectWithScope(this.coroutineScope)
    }

    private fun tryLockAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.audioManager.requestAudioFocus(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setOnAudioFocusChangeListener(this.audioFocusChangeListener)
                    .build().also { audioFocusRequest ->
                        this.audioFocusRequest = audioFocusRequest
                    }
            )
        } else {
            this.audioManager.requestAudioFocus(
                this.audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        } == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun tryReleaseAudioFocus(): Boolean {
        val audioFocusRequest = this.audioFocusRequest
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (audioFocusRequest != null) {
                this.audioManager.abandonAudioFocusRequest(audioFocusRequest)
            } else {
                this.audioManager.abandonAudioFocus(this.audioFocusChangeListener)
            }
        } else {
            this.audioManager.abandonAudioFocus(this.audioFocusChangeListener)
        } == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun release() {
        this.tryReleaseAudioFocus()
    }
}