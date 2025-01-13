package com.music.android.lin.application.framework

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.window.core.layout.WindowWidthSizeClass
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.framework.model.TopLevelDestination
import com.music.android.lin.application.pages.guide.ui.mediaInformationScannerView
import com.music.android.lin.application.pages.guide.ui.navigateToMediaInformationScanner
import com.music.android.lin.application.pages.guide.ui.navigateToPermissionTestAndAcquire
import com.music.android.lin.application.pages.guide.ui.permissionTestAndAcquireView
import com.music.android.lin.application.pages.guide.ui.welcomeGuideView
import com.music.android.lin.application.pages.minibar.ui.Minibar
import com.music.android.lin.application.pages.minibar.ui.MinibarSizeContainer
import com.music.android.lin.application.pages.minibar.ui.TabletMinibar
import com.music.android.lin.application.pages.music.album.ui.albumView
import com.music.android.lin.application.pages.music.edit.ui.editMediaInfoView
import com.music.android.lin.application.pages.music.edit.ui.navigateToEditMediaInfo
import com.music.android.lin.application.pages.music.store.ui.mediaStoreScreen
import com.music.android.lin.application.pages.music.store.ui.navigateToMediaStore
import com.music.android.lin.application.pages.music.play.ui.PlayerView
import com.music.android.lin.application.pages.music.playlist.ui.navigateToPlayListDetail
import com.music.android.lin.application.pages.music.playlist.ui.playListDetail
import com.music.android.lin.application.pages.music.playlist.ui.playListView
import com.music.android.lin.application.pages.music.single.ui.SimpleMusicView
import com.music.android.lin.application.pages.music.single.ui.simpleMusicScreen
import com.music.android.lin.application.pages.settings.ui.aboutScreen
import com.music.android.lin.application.pages.settings.ui.navigateToAboutScreen
import com.music.android.lin.application.pages.settings.ui.scanMediaContentView
import com.music.android.lin.application.pages.settings.ui.settingsView
import org.koin.androidx.compose.KoinAndroidContext

@Stable
enum class NavigationDrawerType {
    PhoneDrawer,
    TabletDrawer
}

internal fun calculateNavigationDrawerType(windowAdaptiveInfo: WindowAdaptiveInfo): NavigationDrawerType {
    with(windowAdaptiveInfo) {
        if (windowPosture.isTabletop) {
            return NavigationDrawerType.PhoneDrawer
        }

        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
            return NavigationDrawerType.PhoneDrawer
        }

        return NavigationDrawerType.TabletDrawer
    }
}

@Composable
fun NiaApp(
    appState: NiaAppState,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier = Modifier,
) {
    KoinAndroidContext {
        AppMaterialTheme {
            GlobalPopupScaffold(appState = appState, modifier = modifier) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NiaApp(
                        modifier = Modifier
                            .matchParentSize(),
                        appState = appState,
                        windowAdaptiveInfo = windowAdaptiveInfo,
                        openMusicPlayerScreen = appState::openAudioPlayerScreen,
                        showAppGuideMessage = appState::showAppGuideMessage
                    )
                    SnackbarHost(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding(),
                        hostState = appState.snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun GlobalPopupScaffold(
    appState: NiaAppState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        content()
        PlayerView(
            show = appState.audioPlayerScreenOn,
            backPressed = appState::closeAudioPlayerScreen,
            modifier = Modifier.matchParentSize()
        )
    }
}

private const val durationMills = 700
private val enterTransition = slideInHorizontally(tween(durationMills)) { it / 4 } + fadeIn(tween(durationMills))
private val exitTransition = slideOutHorizontally(tween(durationMills)) { -it / 4 } + fadeOut(tween(durationMills))
private val popEnterTransition = slideInHorizontally(tween(durationMills)) { -it / 4 } + fadeIn(tween(durationMills))
private val popExitTransition = slideOutHorizontally(tween(durationMills)) { it / 4 } + fadeOut(tween(durationMills))

@Composable
private fun NiaApp(
    appState: NiaAppState,
    windowAdaptiveInfo: WindowAdaptiveInfo,
    modifier: Modifier = Modifier,
    showAppGuideMessage: () -> Unit = {},
    openMusicPlayerScreen: () -> Unit = {},
    openVideoPlayerScreen: () -> Unit = {},
) {
    val drawerType = remember(windowAdaptiveInfo) {
        calculateNavigationDrawerType(windowAdaptiveInfo)
    }
    val shouldShowAppGuide by appState.shouldShowSetupGuide.collectAsState(initial = false)
    LaunchedEffect(shouldShowAppGuide) {
        if (shouldShowAppGuide) {
            showAppGuideMessage()
        }
    }
    NiaApplicationScaffold(
        drawerState = appState.drawerState,
        modifier = modifier,
        drawerType = drawerType,
        drawerContent = {
            NiaAppNavigationDrawer(
                appState = appState,
                drawerType = drawerType,
                openMusicPlayerScreen = openMusicPlayerScreen,
                openVideoPlayerScreen = openVideoPlayerScreen,
            )
        }
    ) {
        val navController = appState.navController
        MinibarSizeContainer {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                NavHost(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { clip = true },
                    navController = navController,
                    startDestination = SimpleMusicView,
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition },
                    popEnterTransition = { popEnterTransition },
                    popExitTransition = { popExitTransition },
                ) {
                    welcomeGuideView(
                        goNext = navController::navigateToPermissionTestAndAcquire
                    ) {
                        permissionTestAndAcquireView(
                            goNext = navController::navigateToMediaInformationScanner,
                            backPress = navController::navigateUp
                        )
                        mediaInformationScannerView(
                            backPress = navController::navigateUp,
                            onComplete = appState::onAppSetupComplete
                        )
                    }
                    simpleMusicScreen(
                        backPressed = navController::navigateUp,
                        editMediaInfo = navController::navigateToEditMediaInfo
                    )
                    albumView()
                    aboutScreen(
                        backPressed = navController::navigateUp,
                        showBackButton = true,
                    )
                    settingsView(
                        navigateToAbout = navController::navigateToAboutScreen,
                        backPressed = navController::navigateUp,
                        navigateToMediaScanner = navController::navigateToMediaStore
                    )
                    playListView(
                        backPressed = navController::navigateUp,
                        goToPlayListDetail = navController::navigateToPlayListDetail
                    )
                    playListDetail(
                        backPressed = navController::navigateUp,
                        editMediaInfo = navController::navigateToEditMediaInfo
                    )
                    scanMediaContentView(
                        backPressed = navController::navigateUp
                    )
                    mediaStoreScreen(
                        showBackButton = true,
                        backPressed = navController::navigateUp
                    )
                    editMediaInfoView(
                        backPress = navController::navigateUp
                    )
                }
                Minibar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    shouldShowMinibar = drawerType == NavigationDrawerType.PhoneDrawer && appState.shouldShowMinibar,
                    navigateToPlayView = openMusicPlayerScreen,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NiaAppNavigationDrawer(
    appState: NiaAppState,
    drawerType: NavigationDrawerType = calculateNavigationDrawerType(currentWindowAdaptiveInfo()),
    openMusicPlayerScreen: () -> Unit = {},
    openVideoPlayerScreen: () -> Unit = {},
) {
    TopAppBarLayout(
        title = {
            Text(text = stringResource(R.string.app_name),)
        }
    )
    if (drawerType == NavigationDrawerType.TabletDrawer) {
        Spacer(modifier = Modifier.height(12.dp))
        TabletMinibar(
            modifier = Modifier.fillMaxWidth(),
            openMusicPlayerScreen = openMusicPlayerScreen,
            openVideoPlayerScreen = openVideoPlayerScreen,
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    val currentDestination = appState.currentDestination
    TopLevelDestination.entries.forEach { topLevelDestination ->
        val isSelected = currentDestination.isRouteInHierarchy(topLevelDestination.baseRoute)
        key(topLevelDestination, isSelected) {
            NavigationDrawerItem(
                label = {
                    Text(text = stringResource(topLevelDestination.titleResId))
                },
                selected = isSelected,
                onClick = { appState.navigateTopLevelDestination(topLevelDestination) },
                icon = {
                    val imageVector = if (isSelected) {
                        topLevelDestination.selectedIcon
                    } else {
                        topLevelDestination.unSelectedIcon
                    }
                    Icon(imageVector = imageVector, contentDescription = null)
                }
            )
        }
    }
}
