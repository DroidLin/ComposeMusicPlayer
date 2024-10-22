package com.harvest.musicplayer

/**
 * @author liuzhongao
 * @since 2023/10/9 17:36
 */
enum class PlayMode {
    /**
     * 单曲播放
     */
    Single,

    /**
     * 单曲循环
     */
    SingleLoop,

    /**
     * 非循环列表播放
     */
    PlayListNoLoop,

    /**
     * 列表循环播放
     */
    PlayListLoop,

    /**
     * 随机播放
     */
    Shuffle;
}