package com.music.android.lin.application.pages.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.BackButton
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.framework.AppMaterialTheme
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object AboutScreen

fun NavController.navigateToAboutScreen(navOptions: NavOptions? = null) {
    navigate(route = AboutScreen, navOptions = navOptions)
}

fun NavGraphBuilder.aboutScreen(
    showBackButton: Boolean = false,
    backPressed: () -> Unit,
) {
    composable<AboutScreen> {
        AboutView(
            backPressed = backPressed,
            showBackButton = showBackButton,
        )
    }
}

@Composable
fun AboutView(
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
    backPressed: () -> Unit = {},
) {
    AboutContentView(
        modifier = modifier,
        backPressed = backPressed,
        showBackButton = showBackButton,
    )
}

@Composable
private fun AboutContentView(
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
    backPressed: () -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        TopHeader(
            showBackButton = showBackButton,
            modifier = Modifier,
            backPress = backPressed,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    showBackButton: Boolean,
    modifier: Modifier = Modifier,
    backPress: () -> Unit = {},
) {
    TopAppBarLayout(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.string_about_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        navigationIcon = {
            if (showBackButton) {
                BackButton(onClick = backPress)
            }
        }
    )
}

@Preview
@Composable
private fun HeaderPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        TopHeader(showBackButton = false)
    }
}

@Preview
@Composable
private fun AboutPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        AboutContentView(showBackButton = false)
    }
}