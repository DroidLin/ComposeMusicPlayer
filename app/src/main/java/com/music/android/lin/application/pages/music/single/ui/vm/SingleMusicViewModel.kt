package com.music.android.lin.application.pages.music.single.ui.vm

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.withDataLoadState
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.application.common.usecase.MediaResourceGeneratorUseCase
import com.music.android.lin.application.common.usecase.PrepareMusicInfoUseCase
import com.music.android.lin.player.service.controller.MediaController
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@Stable
internal class SingleMusicViewModel(
    private val prepareMusicInfoUseCase: PrepareMusicInfoUseCase,
    private val mediaResourceGeneratorUseCase: MediaResourceGeneratorUseCase,
    private val mediaController: MediaController,
) : ViewModel() {

    val musicInfoList = this.prepareMusicInfoUseCase.mediaItemList
        .withDataLoadState { it }
        .stateIn(this.ioViewModelScope, SharingStarted.Lazily, DataLoadState.Loading)
}
