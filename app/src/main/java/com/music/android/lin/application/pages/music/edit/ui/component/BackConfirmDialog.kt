package com.music.android.lin.application.pages.music.edit.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.music.android.lin.R

@Composable
fun BackConfirmDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    confirmExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!show) return
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = confirmExit) {
                Text(text = stringResource(R.string.string_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.string_cancel))
            }
        },
        title = {
            Text(text = stringResource(R.string.string_data_may_lost_title))
        },
        text = {
            Text(text = stringResource(R.string.string_data_may_lost_content))
        }
    )
}