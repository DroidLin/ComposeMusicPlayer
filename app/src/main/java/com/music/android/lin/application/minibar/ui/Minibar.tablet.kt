package com.music.android.lin.application.minibar.ui

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.music.android.lin.application.common.ui.vm.PlayViewModel
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.minibar.ui.state.MinibarUiState
import com.music.android.lin.application.minibar.ui.vm.MinibarViewModel
import com.music.android.lin.player.metadata.MediaType
import org.koin.androidx.compose.koinViewModel

@Composable
fun TabletMinibar(
    modifier: Modifier = Modifier,
    playViewModel: PlayViewModel = koinViewModel(),
    minibarViewModel: MinibarViewModel = koinViewModel(),
    openMusicPlayerScreen: () -> Unit = {},
    openVideoPlayerScreen: () -> Unit = {},
) {
    val uiState by minibarViewModel.uiState.collectAsStateWithLifecycle()
    TabletMinibarContent(
        modifier = modifier,
        minibarUiState = uiState,
        skipToNext = playViewModel::skipToNext,
        skipToPrev = playViewModel::skipToPrev,
        playOrPause = playViewModel::playButtonPressed,
        onMinibarPressed = {
            if (uiState.mediaType == MediaType.Audio) {
                openMusicPlayerScreen()
            } else {
                openVideoPlayerScreen()
            }
        },
    )
}

@Composable
fun TabletMinibarContent(
    minibarUiState: MinibarUiState,
    modifier: Modifier = Modifier,
    skipToPrev: () -> Unit = {},
    skipToNext: () -> Unit = {},
    playOrPause: () -> Unit = {},
    onMinibarPressed: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        onClick = onMinibarPressed,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TabletMinibarImage(
                modifier = Modifier.size(100.dp),
                imageUri = minibarUiState.imageUrl
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TabletMinibarMediaInfo(
                    title = minibarUiState.minibarTitle,
                    description = minibarUiState.minibarDescription,
                    modifier = Modifier,
                )
                TabletMinibarControls(
                    modifier = Modifier,
                    isPlaying = minibarUiState.isPlaying,
                    skipToPrev = skipToPrev,
                    skipToNext = skipToNext,
                    playOrPause = playOrPause,
                )
            }
        }
    }
}

@Composable
private fun TabletMinibarImage(
    imageUri: String?,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.inversePrimary
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun TabletMinibarMediaInfo(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee()
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = LocalContentColor.current.copy(alpha = 0.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.basicMarquee()
        )
    }
}

@Composable
fun TabletMinibarControls(
    isPlaying: Boolean,
    skipToPrev: () -> Unit,
    skipToNext: () -> Unit,
    playOrPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        key(skipToPrev) {
            IconButton(
                onClick = skipToPrev
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = null
                )
            }
        }
        IconButton(
            onClick = playOrPause
        ) {
            if (isPlaying) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = null
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null
                )
            }
        }
        key(skipToNext) {
            IconButton(
                onClick = skipToNext
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun TabletMinibarPreview() {
    AppMaterialTheme {
        TabletMinibarContent(
            minibarUiState = MinibarUiState(
                mediaType = MediaType.Audio,
                imageUrl = "",
                minibarTitle = "周杰伦 - 青花瓷",
                minibarDescription = "某个不知名的专辑",
                isPlaying = true
            ),
            modifier = Modifier.padding(all = 16.dp)
        )
    }
}