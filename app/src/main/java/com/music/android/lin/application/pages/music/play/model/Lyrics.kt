package com.music.android.lin.application.pages.music.play.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
data class LyricInformation(
    val extension: String,
    val data: String,
    val dataSize: Long,
)

@Stable
sealed interface LyricOutput {

    val entries: List<LyricLine>

    companion object : LyricOutput {
        override val entries: List<LyricLine> = emptyList()
    }
}

@Stable
sealed interface LyricLine {
    val time: Long
    val value: String
}

@Immutable
data class SimpleLineLyricOutput(
    override val entries: List<SimpleLyricLine>,
) : LyricOutput

@Immutable
data class SimpleLyricLine(
    override val time: Long,
    override val value: String,
) : LyricLine
