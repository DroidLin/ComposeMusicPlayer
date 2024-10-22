package com.music.android.lin.player.metadata

import com.harvest.musicplayer.PlayRecord

/**
 * @author liuzhongao
 * @since 2024/4/6 01:38
 */
internal data class PlayRecordImpl(
    override val id: Long = 0,
    override val recordTimeStamp: Long,
    override val mediaResourceId: String,
    override val mediaResourceType: PlayRecord.PlayRecordResourceType
) : PlayRecord