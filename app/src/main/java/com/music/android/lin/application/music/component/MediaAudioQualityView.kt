package com.music.android.lin.application.music.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.common.usecase.MediaQuality

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
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = CircleShape
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        text = "HQ",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            MediaQuality.SQ -> {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        text = "SQ",
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