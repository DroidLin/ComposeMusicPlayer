package com.music.android.lin.application.music.play.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.music.play.ui.state.PlayerUiState
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
private fun PlayerContentView(
    data: State<PlayerUiState>,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    backPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaCover = remember { derivedStateOf { data.value.musicItem?.musicCover } }
    Box(
        modifier = modifier
    ) {
        PlayerBackground(
            playCover = mediaCover,
            modifier = Modifier.matchParentSize()
        )
        Column {
            PlayerHeader(
                modifier = Modifier,
                backPressed = backPressed,
            )
            PlayerCover(
                modifier = Modifier,
                playCover = mediaCover,
            )
            PlayControlPanel(
                modifier = Modifier,
                isPlaying = remember { derivedStateOf { data.value.isPlaying } },
                skipToPrevButtonPressed = skipToPrevButtonPressed,
                playOrPauseButtonPressed = playOrPauseButtonPressed,
                skipToNextButtonPressed = skipToNextButtonPressed,
            )
        }
    }
}

@Composable
private fun PlayerBackground(
    playCover: State<String?>,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun PlayerHeader(
    backPressed: () -> Unit,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun PlayerCover(
    playCover: State<String?>,
    modifier: Modifier = Modifier
) {

}

@Composable
private fun PlayControlPanel(
    isPlaying: State<Boolean>,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    modifier: Modifier = Modifier
) {

}