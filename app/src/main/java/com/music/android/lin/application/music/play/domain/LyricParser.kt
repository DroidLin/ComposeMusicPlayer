package com.music.android.lin.application.music.play.domain

import com.music.android.lin.application.music.play.model.LyricInformation
import com.music.android.lin.application.music.play.model.LyricOutput
import java.util.LinkedList

interface LyricParser {

    fun parse(info: LyricInformation): LyricOutput?
}

fun LyricParser(): LyricParser {
    return object : LyricParser {
        private val parsers = LinkedList<LyricParser>()

        init {
            this.parsers += LRCLyricParser
        }

        override fun parse(info: LyricInformation): LyricOutput? {
            var lyricOutput: LyricOutput? = null
            for (parser in this.parsers) {
                lyricOutput = parser.parse(info)
                if (lyricOutput != null) break
            }
            return lyricOutput
        }
    }
}