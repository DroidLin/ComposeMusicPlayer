package com.music.android.lin.player.metadata

/**
 * @author liuzhongao
 * @since 2024/4/11 00:08
 */
class MutableMediaExtras : MediaExtras {
    override var duration: Long = 0L
    override var writer: String = ""
    override var mimeType: String = ""
    override var containsAudioSource: Boolean = false
    override var containsVideoSource: Boolean = false
    override var videoWidth: Int = 0
    override var videoHeight: Int = 0
    override var bitRate: Int = 0
    override var captureFrameRate: Float = 0f
    override var colorRange: Int = 0
    override var sampleRate: String = ""
    override var bitPerSample: String = ""

    companion object {
        private const val serialVersionUID: Long = 3518964065814516282L
    }
}