package com.music.android.lin.player.interfaces

/**
 * @author liuzhongao
 * @since 2024/1/5 15:25
 */
data class PlayerInfoEvent(
    val code: Int,
    val ext: Map<String, Any?>?
) : PlayerEvent {
    companion object {
        private const val serialVersionUID: Long = 5549008562884001457L
    }
}
