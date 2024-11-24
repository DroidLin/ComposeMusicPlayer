package com.music.android.lin.application.common.model

import androidx.compose.runtime.Stable
import com.music.android.lin.application.common.usecase.MusicItem
import kotlinx.collections.immutable.ImmutableList

@Stable
data class PlayListItem(
    val id: String,
    val name: String,
    val description: String,
    val playListCover: String
)

@Stable
data class MediaInfoPlayListItem(
    val id: String,
    val name: String,
    val description: String,
    val playListCover: String,
    val musicInfoList: ImmutableList<MusicItem>
)