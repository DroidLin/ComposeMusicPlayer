package com.music.android.lin.application.common.ui.state

import androidx.compose.runtime.Stable
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.player.metadata.PlayMode

@Stable
data class PlayState(
    val musicItem: MusicItem? = null,
    val playMode: PlayMode = PlayMode.PlayListLoop,
    val isPlaying: Boolean = false,
)