package com.music.android.lin.player.metadata

/**
 * @author liuzhongao
 * @since 2023/12/11 11:37 PM
 */
enum class MediaType(val code: Int) {
    Unsupported(-1),
    Audio(1),
    Video(2);

    companion object {
        @JvmStatic
        fun fromCode(code: Int): MediaType {
            return when (code) {
                Audio.code -> Audio
                Video.code -> Video
                else -> Unsupported
            }
        }
    }
}