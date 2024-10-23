package com.music.android.lin.player.metadata

/**
 * @author liuzhongao
 * @since 2023/10/5 19:52
 */
enum class PlayListType(val code: Int) {

    /**
     * 未知类型
     */
    UnknownType(0),

    /**
     * 本地音乐
     */
    LocalMusic(1),

    /**
     * 网络音乐
     */
    NetworkMusic(2),

    /**
     * 自定义歌单临时，通常是本地歌单或者网络歌单添加了自定义歌曲（如下一首播放等情况）
     */
    Custom(3);

    companion object {
        @JvmStatic
        fun fromCode(code: Int): PlayListType {
            return when (code) {
                LocalMusic.code -> LocalMusic
                NetworkMusic.code -> NetworkMusic
                Custom.code -> Custom
                else -> UnknownType
            }
        }
    }
}