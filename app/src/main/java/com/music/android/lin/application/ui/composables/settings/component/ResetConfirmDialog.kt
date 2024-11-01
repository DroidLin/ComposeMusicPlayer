package com.music.android.lin.application.ui.composables.settings.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.music.android.lin.R

@Composable
fun ResetConfirmDialog(
    showDialog: Boolean,
    confirmResetAll: () -> Unit = {},
    dismissRequest: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!showDialog) return
    AlertDialog(
        modifier = modifier,
        onDismissRequest = dismissRequest,
        text = {
            Text(text = stringResource(id = R.string.string_sure_to_reset_all_description))
        },
        title = {
            Text(text = stringResource(id = R.string.string_sure_to_reset_all_title))
        },
        confirmButton = {
            TextButton(onClick = confirmResetAll) {
                Text(text = stringResource(id = R.string.string_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = dismissRequest) {
                Text(text = stringResource(id = R.string.string_cancel))
            }
        }
    )
}