package com.music.android.lin.application.minibar.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.minibar.audio.ui.AudioMinibar
import com.music.android.lin.application.minibar.ui.state.MinibarUiState
import com.music.android.lin.application.minibar.ui.vm.MinibarViewModel
import com.music.android.lin.player.metadata.MediaType
import org.koin.androidx.compose.koinViewModel

private const val minibarTransitionDuration = 500
private const val minibarExitTransitionDelay = 500

private val minibarEnterAnimation =
    expandVertically(tween(minibarTransitionDuration, minibarExitTransitionDelay)) { 0 } +
            slideInVertically(tween(minibarTransitionDuration, minibarExitTransitionDelay)) { it } +
            fadeIn(tween(minibarTransitionDuration, minibarExitTransitionDelay))
private val minibarExitAnimation =
    shrinkVertically(tween(minibarTransitionDuration)) { 0 } +
        slideOutVertically(tween(minibarTransitionDuration)) { it } +
        fadeOut(tween(minibarTransitionDuration))

@Composable
fun Minibar(
    shouldShowMinibar: State<Boolean>,
    navigateToPlayView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val playViewModel = koinViewModel<PlayViewModel>()
    val minibarViewModel = koinViewModel<MinibarViewModel>()
    val uiState = minibarViewModel.uiState.collectAsStateWithLifecycle()
    MinibarContent(
        shouldShowMinibar = shouldShowMinibar,
        uiState = uiState,
        playButtonPressed = playViewModel::playButtonPressed,
        playListButtonPressed = {},
        audioMinibarContentPressed = navigateToPlayView,
        modifier = modifier.anchorMinibarContainer(),
    )
}

@Composable
private fun MinibarContent(
    shouldShowMinibar: State<Boolean>,
    uiState: State<MinibarUiState>,
    playButtonPressed: () -> Unit,
    playListButtonPressed: () -> Unit,
    audioMinibarContentPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showMinibar = remember {
        derivedStateOf {
            uiState.value.mediaType != MediaType.Unsupported && shouldShowMinibar.value
        }
    }
    AnimatedVisibility(
        modifier = modifier,
        visible = showMinibar.value,
        label = "minibar_visibility_animation",
        enter = minibarEnterAnimation,
        exit = minibarExitAnimation
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
                    minibarContentPressed = audioMinibarContentPressed,
                    modifier = Modifier
                )

                else -> {}
            }
        }
    }
}