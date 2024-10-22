package com.music.android.lin.player.interfaces

/**
 * @author liuzhongao
 * @since 2023/10/10 11:08
 */
enum class MediaPlayerEvent : PlayerEvent {

    /**
     * 初始化完成，此处包括一些特殊行为：启动完成后同步最新状态等操作
     */
    InitializeReady,

    /**
     * 开始缓冲
     */
    BufferingStart,

    /**
     * 缓冲结束
     */
    BufferingEnd,

    /**
     * 媒体播放结束，表示播放到尾部自动结束
     */
    MediaPlayEnd;
}