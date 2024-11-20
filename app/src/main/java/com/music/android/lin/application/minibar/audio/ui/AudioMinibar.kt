package com.music.android.lin.application.minibar.audio.ui

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.android.lin.R
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.minibar.ui.state.MinibarUiState
import com.music.android.lin.player.metadata.MediaType

private val CoverSize by lazy { 56.dp }

@Composable
fun AudioMinibar(
    uiState: State<MinibarUiState>,
    playButtonPressed: () -> Unit,
    playListButtonPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .matchParentSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(CoverSize))
                Spacer(modifier = Modifier.width(8.dp))
                MinibarTitle(
                    minibarTitle = remember { derivedStateOf { uiState.value.minibarTitle } },
                    minibarDescription = remember { derivedStateOf { uiState.value.minibarDescription } },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                MinibarButtons(
                    isPlaying = remember { derivedStateOf { uiState.value.isPlaying } },
                    playButtonPressed = playButtonPressed,
                    playListButtonPressed = playListButtonPressed
                )
            }
            MinibarCoverImage(
                modifier = Modifier
                    .size(CoverSize),
                imageUrl = remember { derivedStateOf { uiState.value.imageUrl } }
            )
        }
    }
}

@Composable
private fun MinibarCoverImage(
    imageUrl: State<String?>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        AsyncImage(
            model = imageUrl.value,
            contentDescription = "",
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun MinibarTitle(
    minibarTitle: State<String>,
    minibarDescription: State<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            modifier = Modifier.basicMarquee(iterations = 3),
            text = minibarTitle.value,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            modifier = Modifier.basicMarquee(iterations = 3),
            text = minibarDescription.value,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = LocalContentColor.current.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun MinibarButtons(
    isPlaying: State<Boolean>,
    playButtonPressed: () -> Unit,
    playListButtonPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        modifier = modifier,
        onClick = playButtonPressed
    ) {
        if (isPlaying.value) {
            Icon(
                painter = painterResource(R.drawable.ic_pause),
                contentDescription = ""
            )
        } else {
            Icon(
                painter = painterResource(R.drawable.ic_play),
                contentDescription = ""
            )
        }
    }
}

private val PreviewFakeUiState by lazy {
    MinibarUiState(
        imageUrl = null,
        minibarTitle = "Hello World.",
        minibarDescription = "Hello World",
        isPlaying = false,
        mediaType = MediaType.Audio
    )
}

@Preview
@Composable
private fun MinibarContentPreview() {
    AppMaterialTheme {
        AudioMinibar(
            modifier = Modifier.fillMaxWidth(),
            playButtonPressed = {},
            playListButtonPressed = {},
            uiState = remember {
                derivedStateOf {
                    PreviewFakeUiState
                }
            }
        )
    }
}