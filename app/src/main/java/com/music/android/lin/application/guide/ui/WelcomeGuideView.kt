package com.music.android.lin.application.guide.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.music.android.lin.R
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object WelcomeGuide

@Stable
@Serializable
data object WelcomeFirstGuide

fun NavController.navigateToWelcomeSetup() {
    navigate(WelcomeFirstGuide) {
        launchSingleTop = true
    }
}

fun NavController.completeSetupAndReturn() {
    val startDestination = graph.findStartDestination()
    navigate(route = requireNotNull(startDestination.route)) {
        popUpTo(graph.findStartDestination().id)
        launchSingleTop = true
    }
}

fun NavGraphBuilder.welcomeGuideView(
    goNext: () -> Unit,
    subNavGraphBuilder: NavGraphBuilder.() -> Unit,
) {
    navigation<WelcomeGuide>(
        startDestination = WelcomeFirstGuide
    ) {
        composable<WelcomeFirstGuide> {
            WelcomeGuideView(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding(),
                skipToNext = goNext
            )
        }
        subNavGraphBuilder()
    }
}

/**
 * @author: liuzhongao
 * @since: 2024/10/25 21:05
 */
@Composable
fun WelcomeGuideView(
    modifier: Modifier = Modifier,
    skipToNext: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.ic_media_indicator),
                    contentDescription = "launcher",
                    colorFilter = ColorFilter.tint(color = colorResource(id = R.color.ic_launcher_foreground))
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(
                    id = R.string.string_welcome,
                    stringResource(id = R.string.app_name)
                ),
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                onClick = skipToNext
            ) {
                Text(text = stringResource(id = R.string.string_guide_go_next))
            }
        }
    }
}

@Preview
@Composable
fun WelcomePreview() {
    MaterialTheme {
        Surface {
            WelcomeGuideView(
                modifier = Modifier.fillMaxSize(),
                skipToNext = {}
            )
        }
    }
}