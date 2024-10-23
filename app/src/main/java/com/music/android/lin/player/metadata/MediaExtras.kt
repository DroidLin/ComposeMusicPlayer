package com.music.android.lin.player.metadata

import android.media.MediaFormat
import androidx.annotation.IntDef
import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/11/30 12:12 AM
 */
interface MediaExtras : Serializable {

    /**
     * 歌曲时长(单位: ms)
     */
    val duration: Long

    /**
     * 歌曲作者
     */
    val writer: String

    val mimeType: String

    val containsAudioSource: Boolean

    val containsVideoSource: Boolean

    val videoWidth: Int

    val videoHeight: Int

    val bitRate: Int

    val captureFrameRate: Float

    @ColorRange
    val colorRange: Int

    @IntDef(value = [MediaFormat.COLOR_RANGE_LIMITED, MediaFormat.COLOR_RANGE_FULL])
    annotation class ColorRange

    val sampleRate: String

    val bitPerSample: String

    companion object : MediaExtras {
        private const val serialVersionUID: Long = -5677828040666476677L
        override val duration: Long get() = -1
        override val writer: String get() = ""
        override val mimeType: String get() = ""
        override val containsAudioSource: Boolean get() = false
        override val containsVideoSource: Boolean get() = false
        override val videoWidth: Int get() = 0
        override val videoHeight: Int get() = 0
        override val bitRate: Int get() = 0
        override val captureFrameRate: Float get() = 0f
        override val colorRange: Int get() = 0
        override val sampleRate: String get() = ""
        override val bitPerSample: String get() = ""
    }
}