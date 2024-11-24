package com.music.android.lin.application.common.ui.state

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

@Immutable
sealed interface DataLoadState {

    @Immutable
    data object Loading: DataLoadState

    @Immutable
    data class Data<T>(val data: T): DataLoadState

    @Immutable
    data class Failure(val message: String, val errorCode: Int): DataLoadState
}

fun <T, R> Flow<T>.withDataLoadState(transform: (T) -> R): Flow<DataLoadState> {
    return flow {
        onStart { emit(DataLoadState.Loading) }
            .catch { it.printStackTrace(); emit(DataLoadState.Failure(it.message ?: "", -1)) }
            .collect { emit(DataLoadState.Data(transform(it))) }
    }
}