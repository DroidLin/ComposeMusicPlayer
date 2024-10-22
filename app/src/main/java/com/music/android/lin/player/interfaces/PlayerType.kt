package com.music.android.lin.player.interfaces

/**
 * @author liuzhongao
 * @since 2023/10/9 11:47 PM
 */
enum class PlayerType {

    UnInitialized,

    /**
     * 系统自带解码功能播放器
     */
    System,

    /**
     * Google官方提供的流式播放器
     */
    ExoPlayer;
}