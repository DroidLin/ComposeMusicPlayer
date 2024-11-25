package com.music.android.lin.application.music.play.model

import java.util.Locale

fun formatAudioTimestamp(timestamp: Long): String {
    val stringBuilder = StringBuilder()
    val hours = (timestamp / 1000 / 60 / 60)
    if (hours > 0) {
        stringBuilder.append(String.format(Locale.getDefault(), "%02d", hours))
            .append(":")
    }
    val minutes = (timestamp / 1_000L / 60) % 60
    stringBuilder.append(String.format(Locale.getDefault(), "%02d", minutes))
        .append(":")
    val seconds = (timestamp / 1_000L) % 60
    stringBuilder.append(String.format(Locale.getDefault(), "%02d", seconds))
    return stringBuilder.toString()
}