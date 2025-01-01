package com.music.android.lin.application.pages.music.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.music.android.lin.R
import kotlinx.coroutines.launch

@Stable
data class CreatePlayListParameter(
    val name: String,
    val cover: String,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreatePlayListBottomSheet(
    sheetState: SheetState,
    showBottomSheet: State<Boolean>,
    dismissRequest: () -> Unit,
    doCreatePlayList: (CreatePlayListParameter, onComplete: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (!showBottomSheet.value) return
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = dismissRequest,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
    ) {
        val inputText = remember { mutableStateOf("") }
        val coverUri = remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .imePadding()
                .imeNestedScroll(),
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = stringResource(R.string.string_create_playlist)
            )
            Button(
                modifier = Modifier.align(Alignment.CenterEnd),
                enabled = remember { derivedStateOf { inputText.value.isNotEmpty() } }.value,
                onClick = {
                    val parameter = CreatePlayListParameter(
                        name = inputText.value,
                        cover = coverUri.value
                    )
                    doCreatePlayList(parameter) {
                        coroutineScope.launch {
                            sheetState.hide()
                            dismissRequest()
                        }
                    }
                }
            ) {
                Text(text = stringResource(R.string.string_create_playlist_complete))
            }
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            value = inputText.value,
            onValueChange = { inputText.value = it },
            shape = MaterialTheme.shapes.large,
        )
    }
}