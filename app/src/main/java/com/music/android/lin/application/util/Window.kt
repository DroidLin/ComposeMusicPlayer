package com.music.android.lin.application.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf

val LocalWindow = compositionLocalOf<Window> { error("Not Provide Window") }

val Window.isLightSystemBar: Boolean
    get() = this.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

fun Window.setTransparentBackground() {
    val colorDrawable = ColorDrawable(Color.TRANSPARENT)
    setBackgroundDrawable(colorDrawable)
}

fun Window.setStatusBarStyle(light: Boolean = false) {
    val systemUiVisibility = this.decorView.systemUiVisibility
    if (light) {
        this.decorView.systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        this.decorView.systemUiVisibility =
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
}

fun Window.setNavigationBarStyle(light: Boolean = false) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return
    }
    val systemUiVisibility = this.decorView.systemUiVisibility
    if (light) {
        this.decorView.systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    } else {
        this.decorView.systemUiVisibility =
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
    }
}

@Composable
fun SystemBarStyleComponent(isLightMode: Boolean) {
    val window = LocalWindow.current
    DisposableEffect(window, isLightMode) {
        val isLightSystemBar = window.isLightSystemBar

        window.setStatusBarStyle(isLightMode)
        window.setNavigationBarStyle(isLightMode)

        onDispose {
            // restore system bar light style
            window.setStatusBarStyle(isLightSystemBar)
            window.setNavigationBarStyle(isLightSystemBar)
        }
    }
}