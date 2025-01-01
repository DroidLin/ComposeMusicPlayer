package com.music.android.lin.application.pages.music.single.ui.vm

import com.music.android.lin.application.common.usecase.MediaQuality
import com.music.android.lin.application.common.usecase.MusicItem

val FakeMusicItemData = (0..20).map { index ->
    MusicItem(
        mediaId = index.toString(),
        musicName = "SongName_$index",
        musicDescription = "SongDescription_$index",
        musicCover = "",
        mediaQuality = MediaQuality.NQ
    )
}