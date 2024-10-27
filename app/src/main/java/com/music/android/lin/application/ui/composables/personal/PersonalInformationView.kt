package com.music.android.lin.application.ui.composables.personal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.music.android.lin.R
import com.music.android.lin.application.ui.composables.framework.AppMaterialTheme

@Composable
fun PersonalInformationView(
    modifier: Modifier = Modifier,
    onSettingsIconPressed: () -> Unit = {}
) {
    Column {
        PersonalTopHeader(
            modifier = Modifier,
            onSettingsIconPressed = onSettingsIconPressed
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonalTopHeader(
    modifier: Modifier = Modifier,
    onSettingsIconPressed: () -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(id = R.string.string_top_header_title),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium
            )
        },
        actions = {
            IconButton(onClick = onSettingsIconPressed) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.string_top_header_settings_icon_description),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

@Preview
@Composable
private fun PersonalInformationPreview() {
    AppMaterialTheme {
        PersonalInformationView()
    }
}

@Preview
@Composable
private fun PersonalTopHeaderPreview() {
    AppMaterialTheme {
        PersonalTopHeader(modifier = Modifier.wrapContentSize())
    }
}