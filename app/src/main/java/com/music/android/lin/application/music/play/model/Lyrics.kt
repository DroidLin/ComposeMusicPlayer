package com.music.android.lin.application.music.play.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class LyricInformation(
    val extension: String,
    val data: String,
    val dataSize: Long,
)

@Stable
sealed interface LyricOutput

@Stable
sealed interface LyricLine {

    val time: Long
}

@Immutable
data class SimpleLineLyricOutput(
    val lyricEntities: List<SimpleLyricLine>,
) : LyricOutput

@Immutable
data class SimpleLyricLine(
    override val time: Long,
    val lyricString: String,
) : LyricLine
