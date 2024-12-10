package com.music.android.lin.application.common.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

private class ScopedViewModelStoreOwner : ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore by lazy { ViewModelStore() }
}

@Composable
fun ViewModelStoreProvider(
    content: @Composable () -> Unit,
) {
    val viewModelStoreOwner = remember { ScopedViewModelStoreOwner() }
    CompositionLocalProvider(
        value = LocalViewModelStoreOwner provides viewModelStoreOwner,
        content = content
    )
}