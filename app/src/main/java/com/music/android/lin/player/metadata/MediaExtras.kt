package com.music.android.lin.player.metadata

import android.media.MediaFormat
import androidx.annotation.IntDef
import org.json.JSONObject
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

fun MediaExtras(
    duration: Long,
    writer: String,
    mimeType: String,
    containsAudioSource: Boolean,
    containsVideoSource: Boolean,
    videoWidth: Int,
    videoHeight: Int,
    bitRate: Int,
    captureFrameRate: Float,
    colorRange: Int,
    sampleRate: String,
    bitPerSample: String
): MediaExtras {
    return MediaExtrasImpl(
        duration = duration,
        writer = writer,
        mimeType = mimeType,
        containsAudioSource = containsAudioSource,
        containsVideoSource = containsVideoSource,
        videoWidth = videoWidth,
        videoHeight = videoHeight,
        bitRate = bitRate,
        captureFrameRate = captureFrameRate,
        colorRange = colorRange,
        sampleRate = sampleRate,
        bitPerSample = bitPerSample,
    )
}

fun MediaExtras.toJson(): String {
    return JSONObject().also { jsonObject ->
        jsonObject.put("duration", this.duration)
        jsonObject.put("writer", this.writer)
        jsonObject.put("mimeType", this.mimeType)
        jsonObject.put("containsAudioSource", this.containsAudioSource)
        jsonObject.put("containsVideoSource", this.containsVideoSource)
        jsonObject.put("videoWidth", this.videoWidth)
        jsonObject.put("videoHeight", this.videoHeight)
        jsonObject.put("bitRate", this.bitRate)
        jsonObject.put("captureFrameRate", this.captureFrameRate)
        jsonObject.put("colorRange", this.colorRange)
        jsonObject.put("sampleRate", this.sampleRate)
        jsonObject.put("bitPerSample", this.bitPerSample)
    }.toString()
}

fun MediaExtras(jsonString: String): MediaExtras {
    val jsonObject = kotlin.runCatching {
        JSONObject(jsonString)
    }.onFailure { it.printStackTrace() }.getOrNull() ?: return MediaExtras

    val duration: Long = jsonObject.optLong("duration", 0)
    val writer: String = jsonObject.optString("writer", "")
    val mimeType: String = jsonObject.optString("mimeType", "")
    val containsAudioSource: Boolean = jsonObject.optBoolean("containsAudioSource", false)
    val containsVideoSource: Boolean = jsonObject.optBoolean("containsVideoSource", false)
    val videoWidth: Int = jsonObject.optInt("videoWidth", 0)
    val videoHeight: Int = jsonObject.optInt("videoHeight", 0)
    val bitRate: Int = jsonObject.optInt("bitRate", 0)
    val captureFrameRate: Float = jsonObject.optDouble("captureFrameRate", 0.0).toFloat()
    val colorRange: Int = jsonObject.optInt("colorRange", 0)
    val sampleRate: String = jsonObject.optString("sampleRate", "")
    val bitPerSample: String = jsonObject.optString("bitPerSample", "")

    return MediaExtras(
        duration = duration,
        writer = writer,
        mimeType = mimeType,
        containsAudioSource = containsAudioSource,
        containsVideoSource = containsVideoSource,
        videoWidth = videoWidth,
        videoHeight = videoHeight,
        bitRate = bitRate,
        captureFrameRate = captureFrameRate,
        colorRange = colorRange,
        sampleRate = sampleRate,
        bitPerSample = bitPerSample,
    )
}

private class MediaExtrasImpl(
    override val duration: Long,
    override val writer: String,
    override val mimeType: String,
    override val containsAudioSource: Boolean,
    override val containsVideoSource: Boolean,
    override val videoWidth: Int,
    override val videoHeight: Int,
    override val bitRate: Int,
    override val captureFrameRate: Float,
    override val colorRange: Int,
    override val sampleRate: String,
    override val bitPerSample: String
) : MediaExtras {

    companion object {
        private const val serialVersionUID: Long = 3435197442666990682L

        @JvmStatic
        fun MediaExtras.toJson(): String {
            return JSONObject().also { jsonObject ->
                jsonObject.put("duration", this.duration)
                jsonObject.put("writer", this.writer)
                jsonObject.put("mimeType", this.mimeType)
                jsonObject.put("containsAudioSource", this.containsAudioSource)
                jsonObject.put("containsVideoSource", this.containsVideoSource)
                jsonObject.put("videoWidth", this.videoWidth)
                jsonObject.put("videoHeight", this.videoHeight)
                jsonObject.put("bitRate", this.bitRate)
                jsonObject.put("captureFrameRate", this.captureFrameRate)
                jsonObject.put("colorRange", this.colorRange)
                jsonObject.put("sampleRate", this.sampleRate)
                jsonObject.put("bitPerSample", this.bitPerSample)
            }.toString()
        }
    }
}