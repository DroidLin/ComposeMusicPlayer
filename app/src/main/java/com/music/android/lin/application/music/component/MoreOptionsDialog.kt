package com.music.android.lin.application.music.component

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import com.music.android.lin.player.metadata.MediaInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsDialog(
    show: Boolean,
    mediaInfo: State<MediaInfo>,
    dismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!show) return
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        onDismissRequest = dismissRequest,
        sheetState = sheetState,
    ) {

    }
}