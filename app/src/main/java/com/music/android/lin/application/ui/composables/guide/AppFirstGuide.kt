package com.music.android.lin.application.ui.composables.guide

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.android.lin.R

@Composable
fun AppFirstGuide(modifier: Modifier = Modifier, onComplete: () -> Unit) {
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
        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            onClick = onComplete
        ) {
            Text(text = "Complete")
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
    val configuration = LocalConfiguration.current
    val colorScheme = remember {
        if (configuration.isNightModeActive) {
            darkColorScheme()
        } else lightColorScheme()
    }
    MaterialTheme(colorScheme) {
        Surface {
            AppFirstGuide(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 100.dp)
            ) {}
        }
    }
}
