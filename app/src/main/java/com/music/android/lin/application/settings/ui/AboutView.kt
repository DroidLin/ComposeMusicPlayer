package com.music.android.lin.application.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.music.android.lin.R
import com.music.android.lin.application.framework.AppMaterialTheme

@Composable
fun AboutView(
    modifier: Modifier = Modifier,
    backPress: () -> Unit = {}
) {
    AboutContentView(
        modifier = modifier,
        backPress = backPress,
    )
}

@Composable
private fun AboutContentView(
    modifier: Modifier = Modifier,
    backPress: () -> Unit = {}
) {
    Column(
        modifier = modifier,
    ) {
        TopHeader(
            modifier = Modifier,
            backPress = backPress,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    modifier: Modifier = Modifier,
    backPress: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.string_about_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        navigationIcon = {
            IconButton(
                onClick = backPress
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Preview
@Composable
private fun HeaderPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        TopHeader()
    }
}

@Preview
@Composable
private fun AboutPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        AboutContentView()
    }
}