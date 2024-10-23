package com.music.android.lin.player.metadata

/**
 * @author liuzhongao
 * @since 2024/4/6 01:31
 */
interface PlayRecord {

    val id: Long

    val mediaResourceId: String

    val mediaResourceType: PlayRecordResourceType

    val recordTimeStamp: Long

    enum class PlayRecordResourceType(val code: Int) {
        Unknown(-1),
        Audio(1),
        Video(2);

        companion object {

            @JvmStatic
            fun fromCode(code: Int): PlayRecordResourceType {
                return when (code) {
                    Audio.code -> Audio
                    Video.code -> Video
                    else -> Unknown
                }
            }

            @JvmStatic
            val MediaType.playRecordResourceType: PlayRecordResourceType
                get() = when (this) {
                    MediaType.Audio -> Audio
                    MediaType.Video -> Video
                    else -> Unknown
                }
        }
    }
}

fun PlayRecord(
    id: Long = 0,
    recordTimeStamp: Long,
    mediaResourceId: String,
    mediaResourceType: PlayRecord.PlayRecordResourceType
): PlayRecord {
    return PlayRecordImpl(id, recordTimeStamp, mediaResourceId, mediaResourceType)
}

private data class PlayRecordImpl(
    override val id: Long = 0,
    override val recordTimeStamp: Long,
    override val mediaResourceId: String,
    override val mediaResourceType: PlayRecord.PlayRecordResourceType
) : PlayRecord