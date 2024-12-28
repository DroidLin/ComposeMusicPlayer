package com.music.android.lin.application

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import com.music.android.lin.application.framework.NiaApp
import com.music.android.lin.application.framework.isNightModeOnCompat
import com.music.android.lin.application.framework.rememberNiaAppState
import com.music.android.lin.application.repositories.AppSetupRepository
import com.music.android.lin.application.util.ActivityProvider
import com.music.android.lin.application.util.LocalWindow
import com.music.android.lin.application.util.SystemBarStyleComponent
import com.music.android.lin.application.util.setTransparentBackground
import com.music.android.lin.modules.AppKoin

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { false }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val appSetupRepository: AppSetupRepository = AppKoin.get()
        window.setTransparentBackground()
        setContent {
            val appState = rememberNiaAppState(
                appSetupRepository = appSetupRepository
            )
            ActivityProvider {
                CompositionLocalProvider(LocalWindow provides window) {
                    SystemBarStyleComponent(!LocalConfiguration.current.isNightModeOnCompat)
                    NiaApp(
                        appState = appState,
                        modifier = Modifier.fillMaxSize(),
                        windowAdaptiveInfo = currentWindowAdaptiveInfo()
                    )
                }
            }
        }
    }
}
