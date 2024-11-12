package com.music.android.lin.application.guide.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.music.android.lin.R

@Composable
fun ScannerLoadingDialog(
    show: Boolean,
    cancelScanning: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!show) return
    AlertDialog(
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        ),
        onDismissRequest = {},
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.string_scanner_running))
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = stringResource(R.string.string_loading))
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = cancelScanning) {
                Text(text = stringResource(R.string.string_cancel))
            }
        }
    )
}