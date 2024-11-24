package com.music.android.lin.application.music.play.ui.state

import androidx.compose.runtime.Stable
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.player.metadata.PlayMode

@Stable
data class PlayerUiState(
    val musicItem: MusicItem? = null,
    val playMode: PlayMode = PlayMode.PlayListLoop,
    val currentProgress: Long = 0L,
    val currentDuration: Long = 0L
)