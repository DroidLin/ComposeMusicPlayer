package com.music.android.lin.application.music.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.component.tintPainter
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.usecase.MediaQuality
import com.music.android.lin.application.usecase.MusicItem

@Composable
fun MusicItemView(
    musicItem: MusicItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small
            ) {
                val picturePainter = painterResource(R.drawable.ic_placeholder_img)
                    .tintPainter(LocalContentColor.current)
                val pictureBrokenPainter = painterResource(R.drawable.ic_broken_img)
                    .tintPainter(LocalContentColor.current)
                AsyncImage(
                    modifier = Modifier
                        .size(56.dp),
                    model = musicItem.musicCover,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = picturePainter,
                    fallback = picturePainter,
                    error = pictureBrokenPainter,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = musicItem.musicName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MediaAudioQualityView(mediaQuality = musicItem.mediaQuality)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = musicItem.musicDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                    )
                }
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
        MusicItemView(
            modifier = Modifier.fillMaxWidth(),
            musicItem = FakeMusicItem,
            onClick = {}
        )
    }
}