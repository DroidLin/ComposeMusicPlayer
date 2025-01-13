package com.music.android.lin.application.framework

import android.content.Intent
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.music.android.lin.application.framework.model.TopLevelDestination
import com.music.android.lin.application.framework.router.Router
import com.music.android.lin.application.pages.guide.ui.WelcomeGuide
import com.music.android.lin.application.pages.guide.ui.completeSetupAndReturn
import com.music.android.lin.application.pages.guide.ui.navigateToWelcomeSetup
import com.music.android.lin.application.pages.music.playlist.ui.navigateToPlayList
import com.music.android.lin.application.pages.music.single.ui.navigateToSimpleMusicView
import com.music.android.lin.application.pages.settings.ui.navigateToSettings
import com.music.android.lin.application.repositories.AppSetupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Composable
fun rememberNiaAppState(
    appSetupRepository: AppSetupRepository,
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): NiaAppState {
    return remember(navController, coroutineScope) {
        NiaAppState(
            appSetupRepository = appSetupRepository,
            navController = navController,
            drawerState = drawerState,
            coroutineScope = coroutineScope,
            snackbarHostState = snackbarHostState,
        )
    }
}

@Stable
class NiaAppState(
    val navController: NavHostController,
    val drawerState: DrawerState,
    val snackbarHostState: SnackbarHostState,
    val coroutineScope: CoroutineScope,
    appSetupRepository: AppSetupRepository,
) {

    private val previousNavDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow.collectAsState(null)
            return currentEntry.value?.destination?.also {
                previousNavDestination.value = it
            } ?: previousNavDestination.value
        }

    val shouldShowSetupGuide: Flow<Boolean> = appSetupRepository
        .isInitialized
        .map(Boolean::not)

    val setAppGuideInitialized = appSetupRepository::setInitialized

    val shouldShowMinibar: Boolean
        @Composable
        get() = !currentDestination.isRouteInHierarchy(WelcomeGuide::class)

    var audioPlayerScreenOn by mutableStateOf(false)
        private set

    fun openAudioPlayerScreen() {
        audioPlayerScreenOn = true
    }

    fun closeAudioPlayerScreen() {
        audioPlayerScreenOn = false
    }

    fun handleNewIntent(intent: Intent) {
        coroutineScope.launch { Router.handleIntent(intent, this@NiaAppState) }
            .invokeOnCompletion { navController.handleDeepLink(intent) }
    }

    fun navigateTopLevelDestination(destination: TopLevelDestination) = closeDrawer {
        if (navController.currentDestination.isRouteInHierarchy(destination.baseRoute)) {
            return@closeDrawer
        }
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when (destination) {
            TopLevelDestination.Music -> navController.navigateToSimpleMusicView(navOptions)
//            TopLevelDestination.Musician -> navController.navigateToSimpleMusicView(navOptions)
            TopLevelDestination.PlayList -> navController.navigateToPlayList(navOptions)
//            TopLevelDestination.Scan -> navController.navigateToScanMediaContent(navOptions)
            TopLevelDestination.Setting -> navController.navigateToSettings(navOptions)
//            TopLevelDestination.About -> navController.navigateToAboutScreen(navOptions)
            else -> {}
        }
    }

    fun openDrawer(onComplete: () -> Unit) {
        coroutineScope.launch { openDrawer() }.invokeOnCompletion { onComplete() }
    }

    fun closeDrawer(onComplete: () -> Unit) {
        coroutineScope.launch { closeDrawer() }.invokeOnCompletion { onComplete() }
    }

    suspend fun openDrawer() {
        this.drawerState.open()
    }

    suspend fun closeDrawer() {
        this.drawerState.close()
    }
}

internal fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean {
    return this?.hierarchy?.any { it.hasRoute(route) } ?: false
}

internal fun NiaAppState.showAppGuideMessage() {
    coroutineScope.launch {
        val interactResult = snackbarHostState.showSnackbar(
            message = "xxxxxx",
            actionLabel = "OK",
            withDismissAction = true,
        )
        if (interactResult == SnackbarResult.Dismissed) {
            setAppGuideInitialized(true)
            return@launch
        }
        navController.navigateToWelcomeSetup()
    }
}

internal fun NiaAppState.onAppSetupComplete() {
    coroutineScope.launch { setAppGuideInitialized(true) }
        .invokeOnCompletion { navController.completeSetupAndReturn() }
}