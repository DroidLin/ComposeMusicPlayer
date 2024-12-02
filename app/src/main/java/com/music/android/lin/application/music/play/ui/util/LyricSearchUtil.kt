package com.music.android.lin.application.music.play.ui.util

import com.music.android.lin.application.music.play.model.LyricLine

internal fun List<LyricLine>.binarySearch(time: Long): Int {
    var left = 0
    var right = this.size - 1
    var mid = (left + right) / 2
    while (left < right) {
        val midLyricLine = this[mid]
        if (midLyricLine.time < time) {
            left = mid + 1
        } else if (midLyricLine.time > time) {
            right = mid - 1
        } else {
            return mid
        }
        mid = (left + right) / 2
    }
    return mid;
}