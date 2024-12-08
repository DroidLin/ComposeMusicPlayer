package com.music.android.lin.application.music.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.music.android.lin.application.music.single.ui.vm.SingleMusicViewModel
import com.music.android.lin.player.metadata.MediaInfo
import org.koin.androidx.compose.koinViewModel

class SelectMediaInfoViewModel(

) : ViewModel() {

}


@Composable
fun SelectMediaInfoBottomSheet(
    completePressed: (List<MediaInfo>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<SingleMusicViewModel>()

}