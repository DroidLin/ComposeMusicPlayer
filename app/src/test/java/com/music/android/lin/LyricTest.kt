package com.music.android.lin

import com.music.android.lin.application.music.play.domain.LRCLyricParser
import com.music.android.lin.application.music.play.model.LyricInformation
import com.music.android.lin.application.music.play.model.LyricOutput
import com.music.android.lin.application.music.play.model.SimpleLineLyricOutput
import com.music.android.lin.application.music.play.ui.util.binarySearchLine
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
            Assert.assertTrue(output is SimpleLineLyricOutput && output.entries.isNotEmpty())
        }
        println("parseLyricCost: ${parseLyricCost / 1000_000.0}ms")
    }

    @Test
    fun lyricAnchor() {
        val output = parseLrcLyrics()
        require(output is SimpleLineLyricOutput && output.entries.isNotEmpty())

        val binarySearchCost = measureNanoTime {
            Assert.assertEquals(0, output.entries.binarySearchLine(0))
            Assert.assertEquals(0, output.entries.binarySearchLine(900))
            Assert.assertEquals(1, output.entries.binarySearchLine(1200))
            Assert.assertEquals(3, output.entries.binarySearchLine(19320))
            Assert.assertEquals(7, output.entries.binarySearchLine(40230))
            Assert.assertEquals(8, output.entries.binarySearchLine(90000))
        }
        println("binarySearchCost: ${binarySearchCost / 1000_000.0}ms")
    }
}