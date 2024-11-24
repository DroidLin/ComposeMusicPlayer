package com.music.android.lin.application.common.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.music.android.lin.application.common.ui.LoadingProgress
import com.music.android.lin.application.common.ui.state.DataLoadState

@Composable
inline fun <T> DataLoadingView(
    state: State<DataLoadState>,
    modifier: Modifier = Modifier,
    noinline failure: (BoxScope.(DataLoadState.Failure) -> Unit)? = null,
    data: BoxScope.(DataLoadState.Data<T>) -> Unit,
) {
    Box(modifier = modifier) {
        DataLoadView<T>(
            state = state,
            failure = failure?.let { failureBlock ->
                {
                    failureBlock(it)
                }
            },
            data = {
                data(it)
            },
            loading = {
                LoadingProgress(modifier = Modifier.matchParentSize())
            }
        )
    }
}

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