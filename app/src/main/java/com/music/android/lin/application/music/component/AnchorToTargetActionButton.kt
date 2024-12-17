package com.music.android.lin.application.music.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun AnchorToTargetActionButton(
    showAnchorButton: State<Boolean>,
    shouldHideContent: State<Boolean>,
    anchorToTarget: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val anchorViewAlpha = animateFloatAsState(
        targetValue = if (shouldHideContent.value) 0.4f else 1f,
        label = ""
    )
    Column(
        modifier = modifier
            .graphicsLayer { alpha = anchorViewAlpha.value },
    ) {
        if (showAnchorButton.value) {
            SmallFloatingActionButton(
                onClick = anchorToTarget,
                modifier = Modifier,
                elevation = FloatingActionButtonDefaults.loweredElevation()
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "anchor to target media info."
                )
            }
        }
    }
}
