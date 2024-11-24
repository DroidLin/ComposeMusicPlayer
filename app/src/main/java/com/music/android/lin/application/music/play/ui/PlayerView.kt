package com.music.android.lin.application.music.play.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.music.play.ui.vm.PlayerPageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerView(
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playViewModel = koinViewModel<PlayViewModel>()

    val playerPageViewModel = koinViewModel<PlayerPageViewModel>()
    val uiState = playerPageViewModel.playerState.collectAsState()
    Box(modifier = modifier.fillMaxSize())
}

@Composable
private fun PlayerContentView(modifier: Modifier = Modifier) {

}

@Composable
private fun PlayerHeader(modifier: Modifier = Modifier) {

}

@Composable
private fun PlayerBackground(modifier: Modifier = Modifier) {

}

@Composable
private fun PlayControlPanel(
    playOrPauseBtnPressed: () -> Unit,
    modifier: Modifier = Modifier
) {

}