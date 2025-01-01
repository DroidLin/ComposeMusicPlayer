package com.music.android.lin.application.common.ui.state

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Immutable
sealed interface LoadState<T> {

    @Immutable
    data object Loading : LoadState<Any>

    @Immutable
    data class Data<T>(val data: T) : LoadState<T>

    @Immutable
    data class Failure(val throwable: Throwable?) : LoadState<Any>
}

inline val <T> LoadState<T>.dataOrNull: T?
    get() = if (this is LoadState.Data) this.data else null

inline val <T> LoadState<T>.isLoading: Boolean
    get() = this is LoadState.Loading

inline val <T> LoadState<T>.isFailure: Boolean
    get() = this is LoadState.Failure

inline val <T> LoadState<T>.isSuccessful: Boolean
    get() = this is LoadState.Data

fun <T> Flow<T>.withLoadState(coroutineScope: CoroutineScope): StateFlow<LoadState<T>> {
   return this.map<T, LoadState<T>> { LoadState.Data(it) }
        .catch { emit(LoadState.Failure(it) as LoadState<T>) }
        .stateIn(coroutineScope, SharingStarted.Lazily, LoadState.Loading as LoadState<T>)
}