package com.music.android.lin.application.pages.settings.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.music.android.lin.application.pages.settings.model.SettingSectionItem

@Composable
fun SettingItemViewHolder(
    modifier: Modifier,
    onClick: () -> Unit,
    sectionItem: SettingSectionItem,
) {
    when (sectionItem.itemType) {
        else -> SettingCommonView(modifier = modifier, sectionItem = sectionItem, onClick = onClick)
    }
}

@Composable
fun SettingCommonView(
    onClick: () -> Unit,
    sectionItem: SettingSectionItem,
    modifier: Modifier = Modifier,
) {
    SettingsNormalInformationItem(
        title = stringResource(sectionItem.itemType.nameResId),
        subTitle = sectionItem.itemType.descriptionResId?.let { stringResource(it) },
        iconId = sectionItem.itemType.iconResId,
        onClick = onClick,
        modifier = modifier,
    )
}