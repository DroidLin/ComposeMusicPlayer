package com.music.android.lin.application.ui.composables.framework

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.music.android.lin.application.ui.composables.PageDeepLinks
import com.music.android.lin.application.ui.composables.PageDefinition
import com.music.android.lin.application.ui.composables.music.AlbumView
import com.music.android.lin.application.ui.composables.music.SingleMusicView
import com.music.android.lin.application.ui.composables.settings.AppSettingsHomeView
import com.music.android.lin.application.ui.composables.framework.vm.NavigationDrawerType
import com.music.android.lin.application.ui.composables.framework.vm.NavigationDrawerTypes
import kotlinx.coroutines.launch

@Composable
fun AppMusicFramework(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val onDrawerIconPressed: suspend () -> Unit = {
        if (drawerState.isOpen) {
            drawerState.animateTo(DrawerValue.Closed, tween(300))
        } else if (drawerState.isClosed) {
            drawerState.animateTo(DrawerValue.Open, tween(300))
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            val currentBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute by remember {
                derivedStateOf {
                    currentBackStackEntry?.destination?.route
                }
            }
            val drawerItemClick: (NavigationDrawerType) -> Unit = onClick@{ type ->
                coroutineScope.launch {
                    onDrawerIconPressed()
                    val route = when (type) {
                        NavigationDrawerType.SingleMusic -> PageDefinition.SingleMusicView
                        NavigationDrawerType.Album -> PageDefinition.AlbumView
                        else -> null
                    } ?: return@launch
                    if (currentRoute == route::class.qualifiedName) {
                        return@launch
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }

            DismissibleDrawerSheet(drawerState) {
                var selectedItem by remember { mutableStateOf(NavigationDrawerTypes[0]) }
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    NavigationDrawerTypes.forEach { drawerType ->
                        val isSelect = when (drawerType) {
                            NavigationDrawerType.SingleMusic -> PageDefinition.SingleMusicView::class.qualifiedName
                            NavigationDrawerType.Album -> PageDefinition.AlbumView::class.qualifiedName
                            else -> null
                        } == currentRoute
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    modifier = Modifier.size(36.dp),
                                    painter = painterResource(id = drawerType.drawableResId),
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = drawerType.nameResId)
                                )
                            },
                            selected = isSelect,
                            onClick = { selectedItem = drawerType; drawerItemClick(drawerType) }
                        )
                    }
                }
            }
        }
    ) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            NavHost(
                modifier = Modifier.matchParentSize(),
                navController = navController,
                startDestination = PageDefinition.SingleMusicView
            ) {
                composable<PageDefinition.SingleMusicView>(
                    deepLinks = listOf(
                        navDeepLink<PageDefinition.SingleMusicView>(
                            basePath = PageDeepLinks.PATH_SINGLE_MUSIC
                        )
                    )
                ) {
                    SingleMusicView(
                        modifier = Modifier,
                        onDrawerIconPressed = {
                            coroutineScope.launch {
                                onDrawerIconPressed()
                            }
                        }
                    )
                }
                composable<PageDefinition.AlbumView>(
                    deepLinks = listOf(
                        navDeepLink<PageDefinition.AlbumView>(
                            basePath = PageDeepLinks.PATH_ALBUM
                        )
                    )
                ) {
                    AlbumView(modifier = Modifier)
                }
                composable<PageDefinition.SettingsView>(
                    deepLinks = listOf(
                        navDeepLink<PageDefinition.SettingsView>(
                            basePath = PageDeepLinks.PATH_SETTINGS
                        )
                    )
                ) {
                    AppSettingsHomeView(
                        backPress = navController::popBackStack,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}