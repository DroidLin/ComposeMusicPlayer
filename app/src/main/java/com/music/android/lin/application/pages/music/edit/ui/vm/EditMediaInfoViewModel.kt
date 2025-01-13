package com.music.android.lin.application.pages.music.edit.ui.vm

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.music.android.lin.application.common.ui.state.dataOrNull
import com.music.android.lin.application.common.ui.state.withLoadState
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.application.common.usecase.FetchMediaInfoUseCase
import com.music.android.lin.application.pages.music.edit.ui.EditMediaInfo
import com.music.android.lin.player.database.MediaRepository
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.metadata.immutable
import com.music.android.lin.player.metadata.mutable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory

@Factory
internal class EditMediaInfoViewModel(
    savedStateHandle: SavedStateHandle,
    fetchMediaInfoUseCase: FetchMediaInfoUseCase,
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    private val editMediaInfo = savedStateHandle.toRoute<EditMediaInfo>()
    val loadState = fetchMediaInfoUseCase
        .observableMediaInfo(editMediaInfo.mediaInfoId)
        .map { mediaInfo ->
            requireNotNull(mediaInfo) { "can not find media info with id: ${editMediaInfo.mediaInfoId}." }
            EditMediaInfoState(mediaInfo)
        }
        .withLoadState(this.ioViewModelScope)

    fun postToDatabase(onComplete: () -> Unit) {
        viewModelScope.launch {
            val editState = loadState.value.dataOrNull ?: return@launch
            val newMediaInfo = editState.mediaInfo.mutable().also {
                it.mediaTitle = editState.mediaTitle
                it.mediaDescription = editState.mediaDescription
                it.coverUri = editState.coverUri
            }.immutable()
            mediaRepository.updateMediaInfo(newMediaInfo)
            withContext(Dispatchers.Main) { onComplete() }
        }
    }
}

@Stable
data class EditMediaInfoState(val mediaInfo: MediaInfo) {
    var mediaTitle by mutableStateOf(mediaInfo.mediaTitle)
        private set
    var mediaDescription by mutableStateOf(mediaInfo.mediaDescription)
        private set
    var coverUri by mutableStateOf(mediaInfo.coverUri)
        private set

    var isModified: Boolean by mutableStateOf(false)
        private set

    fun updateMediaTitle(title: String) = markModified {
        this.mediaTitle = title
    }

    fun updateDescription(description: String) = markModified {
        this.mediaDescription = description
    }

    fun updateCoverUrl(coverUrl: String) = markModified {
        this.coverUri = coverUrl
    }

    private inline fun markModified(function: () -> Unit) {
        isModified = true
        function()
    }
}