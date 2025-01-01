package com.music.android.lin.application.common.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.common.ui.state.LoadState

private const val KEY_LOADING = "key_loading_view"
private const val KEY_FAILURE = "key_failure_view"

fun <T> LazyListScope.loadingOrFailure(
    loadState: LoadState<T>,
    content: LazyListScope.(data: T) -> Unit,
) {
    if (loadState is LoadState.Loading) {
        item(
            key = KEY_LOADING,
            contentType = KEY_LOADING
        ) {
            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .height(3.dp)
            ) {
                LinearProgressIndicator(modifier = Modifier.matchParentSize())
            }
        }
    }
    if (loadState is LoadState.Failure) {
        item(
            key = KEY_FAILURE,
            contentType = KEY_FAILURE
        ) {

        }
    }
    if (loadState is LoadState.Data) {
        content(loadState.data)
    }
}