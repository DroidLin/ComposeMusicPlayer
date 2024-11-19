package com.music.android.lin.application.minibar.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.android.lin.R
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.minibar.ui.state.MinibarUiState

@Composable
fun Minibar(modifier: Modifier = Modifier) {

}

@Composable
private fun MinibarContent(
    uiState: State<MinibarUiState>,
    playButtonPressed: () -> Unit,
    playListButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(48.dp))
            Spacer(modifier = Modifier.width(8.dp))
            MinibarTitle(
                minibarTitle = remember { derivedStateOf { uiState.value.minibarTitle } },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            MinibarButtons(
                isPlaying = remember { derivedStateOf { uiState.value.isPlaying } },
                playButtonPressed = playButtonPressed
            )
        }
        MinibarCoverImage(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(48.dp),
            imageUrl = remember { derivedStateOf { uiState.value.imageUrl } }
        )
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
    modifier: Modifier = Modifier
) {
    Text(modifier = modifier, text = minibarTitle.value)
}

@Composable
private fun MinibarButtons(
    isPlaying: State<Boolean>,
    playButtonPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
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

@Preview
@Composable
private fun MinibarContentPreview() {
    AppMaterialTheme {
        MinibarContent(
            modifier = Modifier.fillMaxWidth(),
            playButtonPressed = {},
            playListButtonPressed = {},
            uiState = remember {
                derivedStateOf {
                    MinibarUiState(
                        imageUrl = null,
                        minibarTitle = "Hello World.",
                        isPlaying = false
                    )
                }
            }
        )
    }
}