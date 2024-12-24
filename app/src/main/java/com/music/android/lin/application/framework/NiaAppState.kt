package com.music.android.lin.application.framework

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.music.android.lin.application.framework.vm.TopLevelDestination
import com.music.android.lin.application.music.playlist.ui.navigateToPlayList
import com.music.android.lin.application.music.single.ui.navigateToSimpleMusicView
import com.music.android.lin.application.settings.ui.navigateToAboutScreen
import com.music.android.lin.application.settings.ui.navigateToSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

@Composable
fun rememberNiaAppState(
    navController: NavHostController = rememberNavController(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
): NiaAppState {
    return remember(navController, coroutineScope) {
        NiaAppState(
            navController = navController,
            drawerState = drawerState,
            coroutineScope = coroutineScope
        )
    }
}

@Stable
class NiaAppState(
    val navController: NavHostController,
    val drawerState: DrawerState,
    val coroutineScope: CoroutineScope,
) {

    private val previousNavDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow.collectAsState(null)
            return currentEntry.value?.destination?.also {
                previousNavDestination.value = it
            } ?: previousNavDestination.value
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
//            TopLevelDestination.Scan -> navController.navigateToSimpleMusicView(navOptions)
            TopLevelDestination.Setting -> navController.navigateToSettings(navOptions)
            TopLevelDestination.About -> navController.navigateToAboutScreen(navOptions)
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