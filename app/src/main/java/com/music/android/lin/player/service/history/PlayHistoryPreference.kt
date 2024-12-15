package com.music.android.lin.player.service.history

import android.content.Context
import com.music.android.lin.player.service.controller.PlayInfo
import com.music.android.lin.player.service.metadata.PlayHistory
import com.music.android.lin.player.service.readObject
import com.music.android.lin.player.service.writeObject
import com.music.android.lin.player.utils.collectWithScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import java.io.File

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
internal class PlayHistoryPreference(
    private val context: Context,
    private val playInfo: PlayInfo,
    private val coroutineScope: CoroutineScope,
) {

    private val directory = File(this.context.filesDir, "player")
    private val historyFile = File(directory, "playHistory.data")

    var playHistoryInPreference: PlayHistory? = null
        get() {
            if (field != null) return field
            val playHistory = if (directory.exists() || directory.mkdirs()) {
                if (historyFile.exists()) {
                    readObject<PlayHistory>(historyFile)
                } else null
            } else null
            field = playHistory
            return playHistory
        }

    init {
        playInfo.information
            .debounce(1000L)
            .mapLatest { information ->
                PlayHistory(
                    mediaInfoPlayList = information.mediaInfoPlayList,
                    indexOfCurrentPosition = information.indexOfCurrentMediaInfo,
                    playMode = information.playMode,
                    playingProgress = information.playerMetadata.contentProgress
                )
            }
            .onEach { newHistory ->
                if (newHistory != playHistoryInPreference) {
                    writeObject<PlayHistory>(historyFile, newHistory)
                    playHistoryInPreference = newHistory
                }
            }
            .collectWithScope(coroutineScope)
    }


}