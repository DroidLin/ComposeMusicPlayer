package com.music.android.lin.application.pages.guide.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.music.android.lin.R
import com.music.android.lin.application.pages.guide.ui.state.ScanningState

@Composable
fun ScanningResultDialog(
    show: Boolean,
    scanningData: ScanningState.Data?,
    confirmSave: (ScanningState.Data) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!show || scanningData == null) return
    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        text = {
            val mediaInfoList = scanningData.mediaInfoList
            if (mediaInfoList.isEmpty()) {
                Text(text = stringResource(R.string.string_empty))
            } else {
                LazyColumn {

                    items(
                        items = mediaInfoList,
                        key = { it.id },
                        contentType = { it.mediaType }
                    ) {
                        Text(
                            modifier = Modifier.fillParentMaxWidth(),
                            text = it.mediaTitle
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    confirmSave(scanningData)
                }
            ) {
                Text(text = stringResource(R.string.string_guide_complete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(R.string.string_cancel))
            }
        }
    )
}