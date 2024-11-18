package com.music.android.lin.application.music.ui.vm

import com.music.android.lin.application.usecase.MediaQuality
import com.music.android.lin.application.usecase.MusicItem

val FakeMusicItemData = (0..20).map { index ->
    MusicItem(
        mediaId = index.toString(),
        musicName = "SongName_$index",
        musicDescription = "SongDescription_$index",
        musicCover = "",
        mediaQuality = MediaQuality.NQ
    )
}