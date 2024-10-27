package com.music.android.lin.application.ui.composables.music.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.android.lin.application.ui.composables.framework.AppMaterialTheme
import com.music.android.lin.application.usecase.MediaQuality
import com.music.android.lin.application.usecase.MusicItem

@Composable
fun MusicItemView(
    musicItem: MusicItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = musicItem.musicCover,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = musicItem.musicName,
                style = MaterialTheme.typography.bodyLarge
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                MediaAudioQualityView(mediaQuality = musicItem.mediaQuality)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = musicItem.musicDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }
        }
    }
}

private val FakeMusicItem = MusicItem(
    mediaId = "",
    musicName = "What Do You Mean",
    musicDescription = "Justin Bieber",
    musicCover = "https://lh3.googleusercontent.com/a/ACg8ocIngwLzvYsiKvBma5audz8xkPT3xzOZZcpALXcpwmR2KMcPQ94=s96-c",
    mediaQuality = MediaQuality.HQ
)

@Preview
@Composable
fun MusicItemPreview() {
    AppMaterialTheme {
        MusicItemView(musicItem = FakeMusicItem) {}
    }
}