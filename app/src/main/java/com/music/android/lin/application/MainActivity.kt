package com.music.android.lin.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.music.android.lin.application.framework.AppFramework
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.framework.AppMusicFramework
import com.music.android.lin.application.framework.isNightModeOnCompat
import com.music.android.lin.application.framework.vm.AppFrameworkViewModel
import com.music.android.lin.application.util.ActivityProvider
import com.music.android.lin.application.util.LocalWindow
import com.music.android.lin.application.util.SystemBarStyleComponent
import com.music.android.lin.application.util.applyWindowBackgroundSettings
import com.music.android.lin.modules.AppKoin
import com.music.android.lin.modules.mediaService
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.applyWindowBackgroundSettings()

        super.onCreate(savedInstanceState)

        val frameworkViewModel = getViewModel<AppFrameworkViewModel>()
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            frameworkViewModel.firstGuideCompleted.value == null
        }

        setContent {
            ActivityProvider {
                CompositionLocalProvider(
                    LocalWindow provides window
                ) {
                    SystemBarStyleComponent(!LocalConfiguration.current.isNightModeOnCompat)
                    KoinAndroidContext {
                        AppMaterialTheme {
                            AppFramework {
                                AppMusicFramework(modifier = Modifier)
                            }
                        }
                    }
                }
            }
        }
    }
}
