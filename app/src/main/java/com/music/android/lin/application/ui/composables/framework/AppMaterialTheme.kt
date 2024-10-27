package com.music.android.lin.application.ui.composables.framework

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/**
 * @author: liuzhongao
 * @since: 2024/10/24 23:47
 */
private val Configuration.isNightModeOn: Boolean
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

private fun determineAppColorScheme(context: Context, configuration: Configuration): ColorScheme {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
        return if (configuration.isNightModeActive) {
            dynamicDarkColorScheme(context = context)
        } else {
            dynamicLightColorScheme(context = context)
        }
    }
    return if (configuration.isNightModeOn) {
        lightColorScheme()
    } else {
        darkColorScheme()
    }
}

@Composable
fun AppMaterialTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val colorScheme = remember(context, configuration) {
        determineAppColorScheme(context, configuration)
    }
    MaterialTheme(colorScheme = colorScheme) {
        Surface(modifier = Modifier, content = content)
    }
}