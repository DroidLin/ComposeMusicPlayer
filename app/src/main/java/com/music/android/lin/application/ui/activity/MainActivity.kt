package com.music.android.lin.application.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.music.android.lin.application.ui.composables.framework.AppFramework
import com.music.android.lin.application.ui.composables.framework.AppMaterialTheme
import com.music.android.lin.application.util.applyWindowBackgroundSettings

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.applyWindowBackgroundSettings()
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            AppMaterialTheme {
                AppFramework {

                }
            }
        }
    }
}
