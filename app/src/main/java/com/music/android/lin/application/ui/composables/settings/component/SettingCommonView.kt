package com.music.android.lin.application.ui.composables.settings.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.ui.composables.settings.model.SettingSectionItem

@Composable
fun SettingItemViewHolder(
    modifier: Modifier,
    sectionItem: SettingSectionItem
) {
    when (sectionItem.itemType) {
        else -> SettingCommonView(modifier, sectionItem)
    }
}

@Composable
fun SettingCommonView(
    modifier: Modifier = Modifier,
    sectionItem: SettingSectionItem
) {
    Row(
        modifier = modifier.padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = sectionItem.itemType.iconResId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = sectionItem.itemType.nameResId),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}