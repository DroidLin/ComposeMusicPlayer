package com.music.android.lin.application.music.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.music.android.lin.R
import com.music.android.lin.application.common.model.PlayListItem
import com.music.android.lin.application.common.ui.component.NiaImage
import com.music.android.lin.application.common.ui.component.tintPainter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayListItemView(
    playList: PlayListItem,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
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
                NiaImage(
                    modifier = Modifier
                        .size(56.dp),
                    url = playList.playListCover,
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
                    text = playList.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = playList.description,
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