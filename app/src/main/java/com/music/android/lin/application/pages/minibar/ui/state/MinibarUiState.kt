package com.music.android.lin.application.pages.minibar.ui.state

import androidx.compose.runtime.Immutable
import com.music.android.lin.player.metadata.MediaType

@Immutable
data class MinibarUiState(
    val mediaType: MediaType = MediaType.Unsupported,
    val imageUrl: String? = "",
    val minibarTitle: String = "",
    val minibarDescription: String = "",
    val isPlaying: Boolean = false,
)