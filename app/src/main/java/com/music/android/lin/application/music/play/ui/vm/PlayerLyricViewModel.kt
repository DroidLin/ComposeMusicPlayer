package com.music.android.lin.application.music.play.ui.vm

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.ui.vm.ioViewModelScope
import com.music.android.lin.application.music.play.domain.LRCLyricParser
import com.music.android.lin.application.music.play.domain.LyricParser
import com.music.android.lin.application.music.play.model.LyricInformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
@Stable
class PlayerLyricViewModel constructor(
    private val context: Context,
    private val lyricParser: LyricParser
) : ViewModel() {

    val lyricOutput = flow {
        val lyricBytes = withContext(Dispatchers.IO) {
            context.assets.open("LyricResource.lrc").readBytes()
        }
        val lyricContent = withContext(Dispatchers.Default) {
            lyricBytes.decodeToString()
        }
        val information = LyricInformation(
            extension = "lrc",
            data = lyricContent,
            dataSize = lyricBytes.size.toLong()
        )
        val lyricOutput = withContext(Dispatchers.Default) {
            this@PlayerLyricViewModel.lyricParser.parse(information)
        }
        emit(lyricOutput)
    }
        .stateIn(this.ioViewModelScope, SharingStarted.Lazily, null)
}