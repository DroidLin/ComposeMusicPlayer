package com.harvest.musicplayer.service

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.harvest.musicplayer.service.metadata.MediaConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @author liuzhongao
 * @since 2023/10/11 16:39
 */
internal class MusicConfigurationHelper constructor(context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val musicConfigurationFile by lazy {
        val musicConfigurationDir = File(context.filesDir, "Player")
        if (!musicConfigurationDir.exists() && !musicConfigurationDir.mkdirs()) {
            throw UnsupportedOperationException("创建文件夹失败")
        }
        File(musicConfigurationDir, "musicConfiguration.dat")
    }

    private val repeatableConfigurationCollectRunnable = object : Runnable {
        override fun run() {
            this@MusicConfigurationHelper.coroutineScope.launch {
                val collectMusicConfiguration = this@MusicConfigurationHelper.configurationCollector?.collect() ?: return@launch
                val configurationChanged = this@MusicConfigurationHelper.getConfiguration() != collectMusicConfiguration
                if (configurationChanged) {
                    this@MusicConfigurationHelper._mediaConfigurationSnapshot = collectMusicConfiguration
                    withContext(Dispatchers.IO) {
                        writeObject(musicConfigurationFile, collectMusicConfiguration)
                    }
                }
            }
            mainHandler.postDelayed(this, 10_000L)
        }
    }

    private var _mediaConfigurationSnapshot: MediaConfiguration? = null
    private var configurationCollector: MusicConfigurationCollector? = null

    fun setConfigurationCollector(musicConfigurationCollector: MusicConfigurationCollector) {
        this.mainHandler.removeCallbacks(this.repeatableConfigurationCollectRunnable)
        this.mainHandler.postDelayed(this.repeatableConfigurationCollectRunnable, 1_000L)
        this.configurationCollector = musicConfigurationCollector
    }

    suspend fun getConfiguration(): MediaConfiguration {
        return this._mediaConfigurationSnapshot ?: withContext(Dispatchers.IO) {
            (readObject(file = this@MusicConfigurationHelper.musicConfigurationFile) ?: MediaConfiguration.Default)
        }.also { configuration -> this._mediaConfigurationSnapshot = configuration }
    }

    fun interface MusicConfigurationCollector {

        fun collect(): MediaConfiguration
    }
}