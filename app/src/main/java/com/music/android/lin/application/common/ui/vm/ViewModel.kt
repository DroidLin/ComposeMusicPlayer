package com.music.android.lin.application.common.ui.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

private const val KEY_IO_COROUTINE_SCOPE = "com.music.android.lin.coroutine.scope.io.ViewModelScope"
private val IO_VIEW_MODEL_SCOPE_LOCK = Any()

val ViewModel.ioViewModelScope: CoroutineScope
    get() = synchronized(IO_VIEW_MODEL_SCOPE_LOCK) {
        getCloseable(KEY_IO_COROUTINE_SCOPE)
            ?: createIOViewModelScope().also { addCloseable(KEY_IO_COROUTINE_SCOPE, it) }
    }

private fun createIOViewModelScope(): ClosableCoroutineScope {
    val dispatcher = Dispatchers.IO
    return ClosableCoroutineScope(dispatcher)
}

private class ClosableCoroutineScope(override val coroutineContext: CoroutineContext) : CoroutineScope, AutoCloseable {
    override fun close() = coroutineContext.cancel()
}