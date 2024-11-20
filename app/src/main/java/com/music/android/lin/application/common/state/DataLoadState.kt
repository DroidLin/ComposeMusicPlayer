package com.music.android.lin.application.common.state

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DataLoadState {

    @Immutable
    data object Loading: DataLoadState

    @Immutable
    data class Data<T>(val data: T): DataLoadState

    @Immutable
    data class Failure(val message: String, val errorCode: Int): DataLoadState
}