package com.music.android.lin.application.framework

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NiaApplicationScaffold(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    drawerContent: @Composable ColumnScope.() -> Unit,
    drawerType: NavigationDrawerType = calculateNavigationDrawerType(currentWindowAdaptiveInfo()),
    content: @Composable () -> Unit,
) {
    if (drawerType == NavigationDrawerType.PhoneDrawer) {
        DismissibleNavigationDrawer(
            drawerState = drawerState,
            modifier = modifier,
            drawerContent = {
                DismissibleDrawerSheet(
                    drawerState = drawerState,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(state = rememberScrollState())
                        ) {
                            drawerContent()
                        }
                    }
                )
            },
            content = content
        )
    } else if (drawerType == NavigationDrawerType.TabletDrawer) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(state = rememberScrollState())
                        ) {
                            drawerContent()
                        }
                    }
                )
            },
            content = content
        )
    }
}
