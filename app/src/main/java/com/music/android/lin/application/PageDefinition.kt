package com.music.android.lin.application

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

/**
 * @author: liuzhongao
 * @since: 2024/10/24 23:44
 */
internal object PageDefinition {

    @Stable
    @Serializable
    data object Welcome

    @Stable
    @Serializable
    data object PermissionTestAndAcquire

    @Stable
    @Serializable
    data object MediaInformationScanner

    @Stable
    @Serializable
    data object PlayerView
}