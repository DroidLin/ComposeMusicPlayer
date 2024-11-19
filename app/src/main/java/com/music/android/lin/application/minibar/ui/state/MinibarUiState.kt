package com.music.android.lin.application.minibar.ui.state

import androidx.compose.runtime.Immutable

@Immutable
data class MinibarUiState(
    val imageUrl: String?,
    val minibarTitle: String,
    val isPlaying: Boolean,
)