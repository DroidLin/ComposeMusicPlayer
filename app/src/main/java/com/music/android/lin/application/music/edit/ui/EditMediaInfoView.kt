package com.music.android.lin.application.music.edit.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.component.TopAppBarLayout

@Composable
fun EditMediaInfoView(
    mediaInfoId: String,
    backPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        TopHeader(
            backPress = backPress,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    modifier: Modifier = Modifier,
    backPress: () -> Unit = {},
) {
    TopAppBarLayout(
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.string_edit_view_title))
        },
        navigationIcon = {
            IconButton(
                onClick = backPress
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null
                )
            }
        }
    )
}