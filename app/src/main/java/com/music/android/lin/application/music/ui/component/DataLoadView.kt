package com.music.android.lin.application.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.music.android.lin.application.common.state.DataLoadState

@Composable
inline fun <T> DataLoadView(
    state: State<DataLoadState>,
    loading: @Composable () -> Unit,
    data: @Composable (DataLoadState.Data<T>) -> Unit,
    noinline failure: (@Composable (DataLoadState.Failure) -> Unit)? = null
) {
    when (val dataLoadState = state.value) {
        is DataLoadState.Loading -> loading()
        is DataLoadState.Data<*> -> data(dataLoadState as DataLoadState.Data<T>)
        is DataLoadState.Failure -> failure?.invoke(dataLoadState)
        else -> {}
    }
}