package com.music.android.lin.application.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window

fun Window.applyWindowBackgroundSettings() {
    val colorDrawable = ColorDrawable(Color.TRANSPARENT)
    setBackgroundDrawable(colorDrawable)
}