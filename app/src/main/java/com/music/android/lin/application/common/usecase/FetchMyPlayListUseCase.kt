package com.music.android.lin.application.common.usecase

import com.music.android.lin.player.database.MediaRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class FetchMyPlayListUseCase(
    private val mediaRepository: MediaRepository
) {
    val mediaInfoPlayListFlow = mediaRepository.observablePlayList()
        .debounce(200.milliseconds)
}