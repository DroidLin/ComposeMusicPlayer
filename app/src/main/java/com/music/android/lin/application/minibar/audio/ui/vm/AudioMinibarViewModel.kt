package com.music.android.lin.application.minibar.audio.ui.vm

import androidx.lifecycle.ViewModel
import com.music.android.lin.player.service.MediaService
import com.music.android.lin.player.service.controller.MediaController

class AudioMinibarViewModel(
    private val mediaService: MediaService,
    private val mediaController: MediaController,
) : ViewModel() {


}