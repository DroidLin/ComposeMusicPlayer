package com.music.android.lin.application.ui.composables.guide

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.music.android.lin.application.ui.composables.PageDeepLinks
import com.music.android.lin.application.ui.composables.PageDeepLinks.PATH_GUIDE_PERMISSION_AND_ACQUIRE
import com.music.android.lin.application.ui.composables.PageDefinition
import com.music.android.lin.application.ui.composables.framework.AppMaterialTheme

@Composable
fun AppFirstGuide(modifier: Modifier = Modifier, onComplete: () -> Unit) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = PageDefinition.Welcome
    ) {
        composable<PageDefinition.Welcome>(
            deepLinks = listOf(
                navDeepLink<PageDefinition.Welcome>(basePath = PageDeepLinks.PATH_GUIDE_WELCOME)
            )
        ) {
            WelcomeGuideView(
                skipToNext = {
                    navController.navigate(PageDefinition.PermissionTestAndAcquire)
                }
            )
        }
        composable<PageDefinition.PermissionTestAndAcquire>(
            deepLinks = listOf(
                navDeepLink<PageDefinition.PermissionTestAndAcquire>(basePath = PATH_GUIDE_PERMISSION_AND_ACQUIRE)
            )
        ) {
            PermissionTestAndAcquireView(
                backPress = navController::popBackStack,
                onComplete = onComplete
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.R)
@Composable
@Preview(
    name = "FirstGuideFullScreenGuide",
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
)
fun FirstGuidePreview() {
    AppMaterialTheme {
        Surface {
            AppFirstGuide {}
        }
    }
}
