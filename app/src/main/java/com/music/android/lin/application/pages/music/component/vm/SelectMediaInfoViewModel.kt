package com.music.android.lin.application.pages.music.component.vm

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.withDataLoadState
import com.music.android.lin.application.common.ui.state.withLoadState
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.common.usecase.MusicItemSnapshot
import com.music.android.lin.application.common.usecase.PrepareMusicInfoUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.selects.whileSelect
import kotlinx.coroutines.withContext


@OptIn(ExperimentalCoroutinesApi::class)
internal class SelectMediaInfoViewModel(
    private val prepareMusicInfoUseCase: PrepareMusicInfoUseCase,
) : ViewModel() {

    private val searchInputChannel = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val musicItemSelectedMapping =
        MutableSharedFlow<SelectionOperation>(extraBufferCapacity = 1)

    val searchInput = this.searchInputChannel
        .stateIn(ioViewModelScope, SharingStarted.Lazily, "")

    val selectableMusicItems = flow<ImmutableList<SelectableMusicItem>> {
        coroutineScope {
            val musicSnapshotChannel = produce<MusicItemSnapshot> {
                prepareMusicInfoUseCase.mediaItemList.collect { send(it) }
            }
            val searchInputChannel = produce {
                searchInputChannel.collectLatest { send(it) }
            }
            val musicItemSelectedMappingChannel = produce {
                musicItemSelectedMapping.collectLatest { send(it) }
            }

            val selectionReceivingMap = HashMap<String, Boolean>()
            var musicItemSnapshot: MusicItemSnapshot? = null
            var keyInput: String? = null

            suspend fun updateState(
                inputKeyword: String? = keyInput,
                snapshot: MusicItemSnapshot? = musicItemSnapshot,
                selectionReceiving: Map<String, Boolean> = selectionReceivingMap,
            ) {
                val filterList = filterAvailableMusicItems(
                    inputKeyword = inputKeyword,
                    snapshot = snapshot,
                    selectionReceivingMap = selectionReceiving
                )
                if (filterList != null) {
                    emit(filterList)
                }
            }

            whileSelect {
                musicItemSelectedMappingChannel.onReceiveCatching { value ->
                    value
                        .onSuccess { operation ->
                            when (operation) {
                                is SelectionOperation.Add -> {
                                    selectionReceivingMap[operation.mediaId] = true
                                }

                                is SelectionOperation.UnAdd -> {
                                    selectionReceivingMap[operation.mediaId] = false
                                }

                                else -> {}
                            }
                            updateState()
                        }
                        .isSuccess
                }
                searchInputChannel.onReceiveCatching { value ->
                    value
                        .onSuccess { input ->
                            updateState(inputKeyword = input)
                            keyInput = input
                        }
                        .onFailure { it?.printStackTrace() }
                        .isSuccess
                }
                musicSnapshotChannel.onReceiveCatching { value ->
                    value
                        .onSuccess { snapshot ->
                            updateState(snapshot = snapshot)
                            musicItemSnapshot = snapshot
                        }
                        .onFailure { it?.printStackTrace() }
                        .isSuccess
                }
            }
        }
    }
        .withLoadState(ioViewModelScope)

    fun onSearchInputValueChange(value: String) {
        var success: Boolean
        do {
            success = this.searchInputChannel.tryEmit(value)
        } while (!success)
    }

    fun onMusicItemPressed(musicItem: SelectableMusicItem) {
        var isSuccess: Boolean
        do {
            val operation = if (musicItem.isSelected) {
                SelectionOperation.UnAdd(musicItem.musicItem.mediaId)
            } else {
                SelectionOperation.Add(musicItem.musicItem.mediaId)
            }
            isSuccess = this.musicItemSelectedMapping.tryEmit(operation)
        } while (!isSuccess)
    }

    suspend fun peekSelectedIdList(): List<String> {
        return withContext(Dispatchers.IO) {
            ((selectableMusicItems.value as? DataLoadState.Data<*>)?.data as? ImmutableList<SelectableMusicItem>)
                ?.filter { it.isSelected }
                ?.map { it.musicItem.mediaId } ?: emptyList()
        }
    }

    private fun filterAvailableMusicItems(
        inputKeyword: String?,
        snapshot: MusicItemSnapshot?,
        selectionReceivingMap: Map<String, Boolean>,
    ): ImmutableList<SelectableMusicItem>? {
        if (snapshot == null) return null
        if (inputKeyword.isNullOrEmpty()) {
            return snapshot.musicItemList
                .map { musicItem ->
                    SelectableMusicItem(
                        musicItem = musicItem,
                        isSelected = selectionReceivingMap[musicItem.mediaId] == true
                    )
                }
                .toImmutableList()
        }
        return snapshot.musicItemList
            .mapNotNull { musicItem ->
                val musicItemMatches = musicItem.musicName.contains(inputKeyword)
                        || musicItem.musicDescription.contains(inputKeyword)
                if (musicItemMatches) {
                    SelectableMusicItem(
                        musicItem = musicItem,
                        isSelected = selectionReceivingMap[musicItem.mediaId] == true
                    )
                } else null
            }
            .toImmutableList()
    }
}

@Stable
data class SelectableMusicItem(
    val musicItem: MusicItem,
    val isSelected: Boolean,
)

@Stable
sealed interface SelectionOperation {

    @Immutable
    data class Add(val mediaId: String) : SelectionOperation

    @Immutable
    data class UnAdd(val mediaId: String) : SelectionOperation
}