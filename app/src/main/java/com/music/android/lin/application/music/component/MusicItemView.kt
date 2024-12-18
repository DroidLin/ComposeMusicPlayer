package com.music.android.lin.application.music.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.android.lin.application.common.usecase.MediaQuality
import com.music.android.lin.application.common.usecase.MusicItem
import com.music.android.lin.application.framework.AppMaterialTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MusicItemView(
    musicItem: MusicItem,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .graphicsLayer(shape = MaterialTheme.shapes.medium, clip = true)
            .combinedClickable(
                enabled = true,
                onClickLabel = "music_item_click",
                onLongClickLabel = "music_item_long_click",
                onLongClick = onLongPress,
                onDoubleClick = null,
                onClick = onClick,
                role = Role.Button,
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() }
            ),
        color = if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = Color.Transparent
            ) {
                if (musicItem.musicCover.isNullOrEmpty()) {
                    Surface(
                        modifier = Modifier
                            .size(56.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                    ) {}
                } else {
                    val contentColor = MaterialTheme.colorScheme.primaryContainer
                    val fallbackPainter = remember(contentColor) { ColorPainter(contentColor) }
                    AsyncImage(
                        modifier = Modifier
                            .size(56.dp),
                        model = musicItem.musicCover,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        error = fallbackPainter,
                        fallback = fallbackPainter,
                        placeholder = fallbackPainter
                    )
                }
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