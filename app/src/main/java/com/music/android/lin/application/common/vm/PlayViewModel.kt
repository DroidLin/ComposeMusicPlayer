package com.music.android.lin.application.common.vm

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController

@SuppressLint("StaticFieldLeak")
class PlayViewModel(
    private val context: Context,
    private val mediaService: MediaService,
    private val mediaController: MediaController
) : ViewModel() {

    fun playButtonPressed() {
        val isPlaying = this.mediaService.information.value.playerMetadata.isPlaying
        if (isPlaying) {
            this.mediaController.pause()
        } else {
            this.mediaController.playOrResume()
        }
    }

}