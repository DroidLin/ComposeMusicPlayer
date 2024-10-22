package com.harvest.musicplayer.metadata

import com.harvest.musicplayer.MediaExtras
import org.json.JSONObject

/**
 * @author liuzhongao
 * @since 2024/4/11 00:17
 */
internal class MediaExtrasImpl(
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