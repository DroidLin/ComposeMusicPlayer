package com.music.android.lin.application.model

import androidx.compose.runtime.Stable

@Stable
sealed interface DataLoadState {

    @Stable
    data object Loading: DataLoadState

    @Stable
    data class Data<T>(val data: T): DataLoadState

    @Stable
    data class Failure(val message: String, val errorCode: Int): DataLoadState
}