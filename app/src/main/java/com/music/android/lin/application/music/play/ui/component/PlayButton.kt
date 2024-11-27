package com.music.android.lin.application.music.play.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier.clickable(
            enabled = true,
            interactionSource = null,
            indication = null,
            onClick = onClick
        ),
        shape = CircleShape,
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier,
            content = content,
            contentAlignment = Alignment.Center
        )
    }
}