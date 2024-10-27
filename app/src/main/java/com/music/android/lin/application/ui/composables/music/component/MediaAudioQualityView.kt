package com.music.android.lin.application.ui.composables.music.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.usecase.MediaQuality

@Composable
fun MediaAudioQualityView(
    modifier: Modifier = Modifier,
    mediaQuality: MediaQuality
) {
    Box(
        modifier = modifier
    ) {
        when (mediaQuality) {
            MediaQuality.HQ -> {
                Card {
                    Text(
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                        text = "HQ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            MediaQuality.SQ -> {
                Card {
                    Text(
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                        text = "SQ",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            else -> {}
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
fun AudioQualityHQPreview() {
    MediaAudioQualityView(mediaQuality = MediaQuality.HQ)
}

@Composable
@Preview
fun AudioQualitySQPreview() {
    MediaAudioQualityView(mediaQuality = MediaQuality.SQ)
}