package com.music.android.lin

import com.music.android.lin.application.music.play.domain.LRCLyricParser
import com.music.android.lin.application.music.play.model.LyricInformation
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.model.SimpleLineLyricOutput
import com.music.android.lin.application.music.play.ui.util.binarySearchLyricLine
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.measureNanoTime

class LyricTest {

    private fun parseLrcLyrics(): LyricOutput? {
        val resourceURL = LyricTest::class.java.classLoader?.getResource("LyricResource.lrc")
            ?: throw NullPointerException("no resource exist.")
        val file = File(resourceURL.file)
        if (!file.exists()) {
            throw FileNotFoundException("file: ${file.absolutePath} not found.")
        }
        val lyricResourceString = resourceURL
            .openStream()
            .buffered()
            .readAllBytes()
            .decodeToString()

        val information = LyricInformation(
            extension = file.extension,
            data = lyricResourceString,
            dataSize = file.length()
        )
        return LRCLyricParser.parse(information)
    }

    @Test
    fun lyricTest() {
        val parseLyricCost = measureNanoTime {
            val output = parseLrcLyrics()
            Assert.assertTrue(output is SimpleLineLyricOutput && output.lyricEntities.isNotEmpty())
        }
        println("parseLyricCost: ${parseLyricCost / 1000_000.0}ms")
    }

    @Test
    fun lyricAnchor() {
        val output = parseLrcLyrics()
        require(output is SimpleLineLyricOutput && output.lyricEntities.isNotEmpty())

        val binarySearchCost = measureNanoTime {
            Assert.assertEquals(output.lyricEntities.binarySearchLyricLine(0), 0)
            Assert.assertEquals(output.lyricEntities.binarySearchLyricLine(900), 0)
            Assert.assertEquals(output.lyricEntities.binarySearchLyricLine(1200), 1)
            Assert.assertEquals(output.lyricEntities.binarySearchLyricLine(19320), 3)
            Assert.assertEquals(output.lyricEntities.binarySearchLyricLine(90000), 8)
        }
        println("binarySearchCost: ${binarySearchCost / 1000_000.0}ms")
    }
}