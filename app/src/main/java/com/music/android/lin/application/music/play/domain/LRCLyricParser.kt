package com.music.android.lin.application.music.play.domain

import com.music.android.lin.application.music.play.model.LyricInformation
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.model.SimpleLineLyricOutput
import com.music.android.lin.application.music.play.model.SimpleLyricLine
import java.util.LinkedList
import java.util.regex.Pattern

internal object LRCLyricParser : LyricParser {

    private const val LRC_MATCH_PATTERN = "(\\[([0-9]+):([0-9]+)\\.([0-9]+)\\])(.*)"

    override fun parse(info: LyricInformation): LyricOutput? {
        if (info.extension != "lrc") return null
        val pattern = Pattern.compile(LRC_MATCH_PATTERN)

        val simpleLyricLines = LinkedList<SimpleLyricLine>()
        val matcher = pattern.matcher(info.data)
        while (matcher.find()) {
            val minutes = matcher.group(2)?.toLongOrNull() ?: continue
            val seconds = matcher.group(3)?.toLongOrNull() ?: continue
            val milliSeconds = matcher.group(4)?.let { milliSecondString ->
                val doubleNumber = "0.$milliSecondString".toDoubleOrNull() ?: return@let 0L
                (doubleNumber * 1000).toLong()
            } ?: continue
            val value = matcher.group(5)?.trim() ?: continue

            simpleLyricLines += SimpleLyricLine(
                time = minutes * 60 * 1000 + seconds * 1000 + milliSeconds,
                value = value
            )
        }
        return SimpleLineLyricOutput(entries = simpleLyricLines)
    }

}