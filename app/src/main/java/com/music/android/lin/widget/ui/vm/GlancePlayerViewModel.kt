package com.music.android.lin.widget.ui.vm

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.common.usecase.util.toMusicItem
import com.music.android.lin.player.metadata.PlayMode
import com.music.android.lin.player.notification.fetchImageAsBitmap
import com.music.android.lin.player.service.MediaService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@SuppressLint("StaticFieldLeak")
@Stable
class GlancePlayerViewModel(
    private val context: Context,
    private val mediaService: MediaService,
) : ViewModel() {

    private val languageContext by lazy { ContextCompat.getContextForLanguage(context) }

    val glancePlayState = mediaService.information
        .map { information ->
            val musicItem = information.mediaInfo?.toMusicItem(languageContext)
            GlancePlayState(
                imageBitmap = fetchImageAsBitmap(musicItem?.musicCover),
                musicItem = musicItem,
                playMode = information.playMode,
                isPlaying = information.playerMetadata.isPlaying,
            )
        }
        .stateIn(this.ioViewModelScope, SharingStarted.Lazily, GlancePlayState())

}

@Immutable
data class GlancePlayState(
    val imageBitmap: Bitmap? = null,
    val musicItem: MusicItem? = null,
    val playMode: PlayMode = PlayMode.PlayListLoop,
    val isPlaying: Boolean = false,
)