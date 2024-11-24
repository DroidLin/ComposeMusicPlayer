package com.music.android.lin.application.framework

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.navigation.toRoute
import com.music.android.lin.application.PageDeepLinks
import com.music.android.lin.application.PageDefinition
import com.music.android.lin.application.framework.vm.NavigationDrawerType
import com.music.android.lin.application.framework.vm.NavigationDrawerTypes
import com.music.android.lin.application.framework.vm.pageDefinitionName
import com.music.android.lin.application.minibar.ui.Minibar
import com.music.android.lin.application.minibar.ui.MinibarSizeContainer
import com.music.android.lin.application.minibar.ui.minibarHeightPadding
import com.music.android.lin.application.music.album.ui.AlbumView
import com.music.android.lin.application.music.play.ui.PlayerView
import com.music.android.lin.application.music.playlist.ui.PlayListDetailView
import com.music.android.lin.application.music.playlist.ui.PlayListView
import com.music.android.lin.application.music.single.ui.SingleMusicView
import com.music.android.lin.application.settings.ui.AboutView
import com.music.android.lin.application.settings.ui.AppSettingsHomeView
import kotlinx.coroutines.launch

@Composable
fun AppMusicFramework(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val onDrawerIconPressed: suspend () -> Unit = {
        if (drawerState.isOpen) {
            drawerState.close()
        } else if (drawerState.isClosed) {
            drawerState.open()
        }
    }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute by remember {
        derivedStateOf {
            currentBackStackEntry?.destination?.route
        }
    }

    // there is an issue in NavController#popBackStack while the function is called more than once at the same time,
    // so we try to use BackPressDispatcher instead.
    val backPressDispatcher = LocalOnBackPressedDispatcherOwner.current
    val backPressed: () -> Unit = {
        backPressDispatcher?.onBackPressedDispatcher?.onBackPressed()
    }
    val navigateToPage: (String) -> Unit = navigateToPage@{ route: String ->
        if (currentRoute == route) {
            return@navigateToPage
        }
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            val drawerItemClick: (NavigationDrawerType) -> Unit = onClick@{ type ->
                coroutineScope.launch {
                    onDrawerIconPressed()
                    val route = type.pageDefinitionName ?: return@launch
                    if (currentRoute == route::class.qualifiedName) {
                        return@launch
                    }
                    navigateToPage(route)
                }
            }
            DismissibleDrawerSheet(drawerState) {
                var selectedItem by remember { mutableStateOf(NavigationDrawerTypes[0]) }
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    NavigationDrawerTypes.forEach { drawerType ->
                        val isSelect = drawerType.pageDefinitionName == currentRoute
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
        MinibarSizeContainer {
            Box(
                modifier = modifier
                    .fillMaxSize(),
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
                            modifier = Modifier
                                .fillMaxSize()
                                .minibarHeightPadding()
                                .navigationBarsPadding(),
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
                        AlbumView(
                            modifier = Modifier
                                .fillMaxSize()
                                .minibarHeightPadding()
                                .navigationBarsPadding()
                        )
                    }
                    composable<PageDefinition.SettingsView>(
                        deepLinks = listOf(
                            navDeepLink<PageDefinition.SettingsView>(
                                basePath = PageDeepLinks.PATH_SETTINGS
                            )
                        )
                    ) {
                        AppSettingsHomeView(
                            modifier = Modifier
                                .fillMaxSize()
                                .minibarHeightPadding()
                                .navigationBarsPadding(),
                            backPress = navController::popBackStack,
                            goToAboutView = {
                                navigateToPage(requireNotNull(PageDefinition.AboutView::class.qualifiedName))
                            }
                        )
                    }
                    composable<PageDefinition.AboutView>(
                        deepLinks = listOf(
                            navDeepLink<PageDefinition.AboutView>(
                                basePath = PageDeepLinks.PATH_ABOUT
                            )
                        )
                    ) {
                        AboutView(
                            modifier = Modifier
                                .fillMaxSize()
                                .minibarHeightPadding()
                                .navigationBarsPadding(),
                            backPress = navController::popBackStack
                        )
                    }
                    composable<PageDefinition.PlayerView>(
                        deepLinks = listOf(
                            navDeepLink<PageDefinition.PlayerView>(basePath = PageDeepLinks.PATH_PLAYER)
                        )
                    ) {
                        PlayerView(
                            modifier = Modifier.fillMaxSize(),
                            backPressed = backPressed,
                        )
                    }
                    composable<PageDefinition.PlayList> {
                        PlayListView(
                            modifier = Modifier
                                .fillMaxSize()
                                .minibarHeightPadding()
                                .navigationBarsPadding(),
                            backPressed = backPressed,
                            goToPlayListDetail = { playListItem ->
                                navController.navigate(PageDefinition.PlayListDetail(playListItem.id)) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable<PageDefinition.PlayListDetail> {
                        val playListDetail = it.toRoute<PageDefinition.PlayListDetail>()
                        PlayListDetailView(
                            modifier = Modifier
                                .minibarHeightPadding()
                                .navigationBarsPadding(),
                            playListId = playListDetail.id,
                            backPressed = backPressed
                        )
                    }
                }
                Minibar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    shouldShowMinibar = remember {
                        derivedStateOf {
                            currentRoute != PageDefinition.PlayerView::class.qualifiedName
                        }
                    },
                    navigateToPlayView = {
                        navController.navigate(PageDefinition.PlayerView) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}