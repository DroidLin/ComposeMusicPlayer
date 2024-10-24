package com.music.android.lin.player.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectWithScope(coroutineScope: CoroutineScope) {
    coroutineScope.launch { collect() }
}