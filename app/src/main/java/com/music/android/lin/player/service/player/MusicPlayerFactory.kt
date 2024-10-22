package com.harvest.musicplayer.service.player

import com.harvest.musicplayer.PlayerType
import com.harvest.musicplayer.service.state.IMutablePlayerCenter

/**
 * @author liuzhongao
 * @since 2023/10/9 11:46 PM
 */
internal object MusicPlayerFactory {

    @JvmStatic
    fun create(playerType: PlayerType, mutablePlayerCenter: IMutablePlayerCenter): AbstractMusicPlayer {
        return when (playerType) {
            PlayerType.System -> SystemMediaPlayer(mutablePlayerCenter = mutablePlayerCenter)
            else -> SystemMediaPlayer(mutablePlayerCenter = mutablePlayerCenter)
        }
    }
}