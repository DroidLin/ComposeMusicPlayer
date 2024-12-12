package com.music.android.lin.application.music.playlist.ui.vm

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.withDataLoadState
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.application.common.usecase.FetchPlayListUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@SuppressLint("StaticFieldLeak")
class PlayListDetailViewModel(
    private val context: Context,
    fetchPlayListUseCase: FetchPlayListUseCase,
) : ViewModel() {

    val playList = fetchPlayListUseCase.playList
        .withDataLoadState {
            if (it == null) {
                val message = ContextCompat.getString(this.context, R.string.string_empty)
                throw RuntimeException(message)
            } else it
        }
        .stateIn(this.ioViewModelScope, SharingStarted.Lazily, DataLoadState.Loading)
}