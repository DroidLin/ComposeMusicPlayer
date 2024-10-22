package com.harvest.musicplayer.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.harvest.musicplayer.MediaController
import com.harvest.musicplayer.MediaService
import com.harvest.musicplayer.MusicServicePlaybackState
import com.harvest.musicplayer.service.controller.MediaControllerWrapper
import kotlinx.coroutines.flow.StateFlow

/**
 * @author liuzhongao
 * @since 2023/10/10 17:05
 */
internal class MediaServiceImpl : MediaService {

    private var _mediaController: MediaController? = null

    private val collector = MediaServiceControllerCallbackCollector()
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val mediaController = service as? MediaController ?: return
            this@MediaServiceImpl._mediaController = service
            this@MediaServiceImpl.collector.setPlaybackState(mediaController.playInfo)
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override val musicServicePlaybackState: StateFlow<MusicServicePlaybackState>
        get() = this.collector.flow
    override val mediaController: MediaController = MediaControllerWrapper(this::_mediaController)

    override fun init(context: Context) {
        val bundle = Bundle()
        bundle.putBinder(KEY_GLOBAL_LISTENER, this.collector)
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtras(bundle)
        context.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE)
    }

    companion object {
        const val KEY_GLOBAL_LISTENER = "key_param_global_listener"
    }
}