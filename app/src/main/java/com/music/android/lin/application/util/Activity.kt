package com.music.android.lin.application.util

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf

val LocalActivity = compositionLocalOf<ComponentActivity> { error("not provided.") }

@Composable
fun ComponentActivity.ActivityProvider(content: @Composable () -> Unit) {
    CompositionLocalProvider(value = LocalActivity provides this, content = content)
}

@Composable
fun OnNewIntent(
    onNewIntent: (Intent) -> Unit,
) {
    val activity = LocalActivity.current
    DisposableEffect(onNewIntent) {
        activity.addOnNewIntentListener(onNewIntent)
        onDispose { activity.removeOnNewIntentListener(onNewIntent) }
    }
}