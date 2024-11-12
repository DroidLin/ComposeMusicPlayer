package com.music.android.lin.application.guide.ui.state

import androidx.compose.runtime.Stable

@Stable
data class ScannerUiState(
    val useAndroidScanner: Boolean = true,
    val scanningState: ScanningState = ScanningState.Init
)
