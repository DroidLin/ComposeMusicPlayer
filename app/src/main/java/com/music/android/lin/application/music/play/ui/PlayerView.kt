package com.music.android.lin.application.music.play.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.application.common.ui.state.PlayState
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.music.play.ui.state.PlayerState
import com.music.android.lin.application.music.play.ui.vm.PlayerPageViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlayerView(
    backPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playViewModel = koinViewModel<PlayViewModel>()
    val playState = playViewModel.playState.collectAsStateWithLifecycle()

    val playerPageViewModel = koinViewModel<PlayerPageViewModel>()
    val playerState = playerPageViewModel.playerState.collectAsState()

    PlayerContentView(
        modifier = modifier,
        playerState = playerState,
        playState = playState,
        skipToPrevButtonPressed = playViewModel::skipToPrev,
        playOrPauseButtonPressed = playViewModel::playButtonPressed,
        skipToNextButtonPressed = playViewModel::skipToNext,
        backPressed = backPressed,
        seekToPosition = playViewModel::seekToPosition
    )
}

@Composable
private fun PlayerContentView(
    playState: State<PlayState>,
    playerState: State<PlayerState>,
    skipToPrevButtonPressed: () -> Unit,
    playOrPauseButtonPressed: () -> Unit,
    skipToNextButtonPressed: () -> Unit,
    backPressed: () -> Unit, seekToPosition: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaCover = remember { derivedStateOf { playState.value.musicItem?.musicCover } }
    Box(
        modifier = modifier
    ) {
        PlayerBackground(
            playCover = mediaCover,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .matchParentSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            PlayerHeader(
                modifier = Modifier,
                backPressed = backPressed,
            )
            PlayerCover(
                modifier = Modifier,
                playCover = mediaCover,
            )
            PlayerProgressView(
                modifier = Modifier,
                progress = remember { derivedStateOf { playerState.value.progress } },
                currentProgress = remember { derivedStateOf { playerState.value.currentProgress } },
                currentDuration = remember { derivedStateOf { playerState.value.currentDuration } },
                seekToPosition = seekToPosition,
            )
            PlayControlPanel(
                modifier = Modifier,
                isPlaying = remember { derivedStateOf { playState.value.isPlaying } },
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
fun PlayerProgressView(
    progress: State<Float>,
    currentProgress: State<Long>,
    currentDuration: State<Long>,
    seekToPosition: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentPosition = remember {
        mutableStateOf<Float?>(null)
    }
    val uiProgress = remember {
        derivedStateOf {
            currentPosition.value ?: progress.value
        }
    }
    Column(
        modifier = modifier
    ) {
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = uiProgress.value,
            onValueChange = { currentPosition.value = it },
            onValueChangeFinished = {
                seekToPosition((uiProgress.value * currentDuration.value).toLong())
                currentPosition.value = null
            }
        )
    }
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