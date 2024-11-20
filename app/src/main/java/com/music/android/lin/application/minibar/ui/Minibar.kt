package com.music.android.lin.application.minibar.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.application.common.vm.PlayViewModel
import com.music.android.lin.application.minibar.audio.ui.AudioMinibar
import com.music.android.lin.application.minibar.ui.state.MinibarUiState
import com.music.android.lin.application.minibar.ui.vm.MinibarViewModel
import com.music.android.lin.player.metadata.MediaType
import org.koin.androidx.compose.koinViewModel

private val minibarSwitchTransitionSpec =
    fadeIn(animationSpec = tween(durationMillis = 300)) togetherWith fadeOut(
        animationSpec = tween(
            durationMillis = 300
        )
    )


@Composable
fun Minibar(
    minibarContentPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playViewModel = koinViewModel<PlayViewModel>()
    val minibarViewModel = koinViewModel<MinibarViewModel>()
    val uiState = minibarViewModel.uiState.collectAsStateWithLifecycle()
    MinibarContent(
        uiState = uiState,
        playButtonPressed = playViewModel::playButtonPressed,
        playListButtonPressed = {},
        minibarContentPressed = minibarContentPressed,
        modifier = modifier,
    )
}

@Composable
private fun MinibarContent(
    uiState: State<MinibarUiState>,
    playButtonPressed: () -> Unit,
    playListButtonPressed: () -> Unit,
    minibarContentPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showMinibar = remember { derivedStateOf { uiState.value.mediaType != MediaType.Unsupported } }
    AnimatedVisibility(
        modifier = modifier,
        visible = showMinibar.value,
        label = "minibar_visibility_animation",
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        val mediaType = remember { derivedStateOf { uiState.value.mediaType } }
        AnimatedContent(
            targetState = mediaType.value,
            label = "minibar_content_switch_animation",
            contentAlignment = Alignment.Center,
            transitionSpec = { EnterTransition.None togetherWith ExitTransition.None }
        ) { type ->
            when (type) {
                MediaType.Audio -> AudioMinibar(
                    uiState = uiState,
                    playButtonPressed = playButtonPressed,
                    playListButtonPressed = playListButtonPressed,
                    modifier = Modifier
                )

                else -> {}
            }
        }
    }
}