package com.music.android.lin.application.pages.settings.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockPerson
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.music.android.lin.application.framework.AppMaterialTheme

@Composable
fun SettingsNormalInformationItem(
    title: String,
    subTitle: String?,
    icons: ImageVector? = null,
    onClick: () -> Unit,
    extraContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    SettingsNormalInformationItem(
        title = {
            Text(text = title)
        },
        subTitle = subTitle.takeIf { !it.isNullOrEmpty() }?.let {
            {
                Text(text = it, style = MaterialTheme.typography.bodySmall)
            }
        },
        icons = icons?.let {
            {
                Icon(imageVector = it, contentDescription = null)
            }
        },
        onClick = onClick,
        extraContent = extraContent,
        modifier = modifier,
    )
}

@Composable
fun SettingsNormalInformationItem(
    title: String,
    subTitle: String?,
    @DrawableRes iconId: Int,
    onClick: () -> Unit,
    extraContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    SettingsNormalInformationItem(
        title = {
            Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        subTitle = subTitle.takeIf { !it.isNullOrEmpty() }?.let {
            {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        icons = {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null
            )
        },
        onClick = onClick,
        extraContent = extraContent,
        modifier = modifier,
    )
}

private val icon_divider = 24.dp

@Composable
fun SettingsNormalInformationItem(
    title: @Composable () -> Unit,
    subTitle: (@Composable () -> Unit)? = null,
    icons: (@Composable () -> Unit)? = null,
    extraContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        onClick = onClick,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(56.dp)
        ) {
            val iconRef = createRef()
            val descriptionRef = createRef()
            val extraContentRef = createRef()
            if (icons != null) {
                Box(
                    modifier = Modifier
                        .constrainAs(iconRef) {
                            linkTo(
                                top = descriptionRef.top,
                                bottom = descriptionRef.bottom,
                                start = parent.start,
                                end = descriptionRef.start,
                                startMargin = icon_divider,
                                horizontalBias = 0f,
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    icons()
                }
            }
            Column(
                modifier = Modifier
                    .constrainAs(descriptionRef) {
                        linkTo(
                            top = parent.top,
                            bottom = if (extraContent != null) {
                                extraContentRef.bottom
                            } else parent.bottom,
                            bias = 0.5f
                        )
                        start.linkTo(
                            anchor = if (icons != null) {
                                iconRef.end
                            } else parent.start,
                            margin = icon_divider
                        )
                    }
            ) {
                title()
                if (subTitle != null) {
                    subTitle()
                }
            }
            if (extraContent != null) {
                Box(
                    modifier = Modifier.constrainAs(extraContentRef) {
                        linkTo(
                            top = descriptionRef.bottom,
                            start = descriptionRef.start,
                            end = descriptionRef.end,
                            bottom = parent.bottom,
                            horizontalBias = 0f,
                            verticalBias = 1f
                        )
                    }
                ) {
                    extraContent()
                }
            }
            if (extraContent != null) {
                createVerticalChain(descriptionRef, extraContentRef, chainStyle = ChainStyle.Packed)
            } else createVerticalChain(descriptionRef, chainStyle = ChainStyle.Packed)
        }
    }
}

@Preview
@Composable
private fun SettingNormalItemPreview() {
    AppMaterialTheme {
        Column {
            SettingsNormalInformationItem(
                title = "设置",
                subTitle = "设备里有33个应用需要更新",
                icons = Icons.Default.Settings,
                onClick = {},
                extraContent = {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(vertical = 0.dp)
                        ) {
                            Text(text = "现在更新", style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(vertical = 0.dp)
                        ) {
                            Text(text = "全部更新", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            )
            SettingsNormalInformationItem(
                title = "设置",
                subTitle = "可在此处修改你的设置",
                icons = Icons.Default.LockPerson,
                onClick = {},
            )
            SettingsNormalInformationItem(
                title = "设置",
                subTitle = "可在此处修改你的设置",
                icons = Icons.Default.LockPerson,
                onClick = {},
            )
            SettingsNormalInformationItem(
                title = "设置",
                subTitle = "",
                icons = Icons.Default.LockPerson,
                onClick = {},
                extraContent = {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(vertical = 0.dp)
                        ) {
                            Text(text = "现在更新", style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(
                            onClick = {},
                            contentPadding = PaddingValues(vertical = 0.dp)
                        ) {
                            Text(text = "全部更新", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            )
        }
    }
}