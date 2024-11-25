package com.music.android.lin.application.music.play.ui.state

import androidx.compose.runtime.Stable

@Stable
data class PlayerState(
    val progress: Float = 0f,
    val currentProgress: Long = 0L,
    val currentDuration: Long = 0L
)