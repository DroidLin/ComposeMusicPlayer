package com.music.android.lin.player.service.playlist

import com.music.android.lin.player.metadata.MediaInfo

internal sealed interface MediaList {
    fun tryGetPreviousMediaInfo(fromUser: Boolean): MediaInfo?
    fun tryGetNextMediaInfo(fromUser: Boolean): MediaInfo?
}

internal fun Shuffle(mediaList: MediaPlayingList): MediaList = ShuffleMediaList(mediaList)

internal fun Single(mediaList: MediaPlayingList): MediaList = SingleMediaList(mediaList)

internal fun ListLoop(mediaList: MediaPlayingList): MediaList = ListLoopMediaList(mediaList)

internal fun SingleLoop(mediaList: MediaPlayingList): MediaList = SingleLoopMediaList(mediaList)

private class ListLoopMediaList(mediaList: MediaPlayingList) : MediaListParent(mediaList) {

    override fun tryGetPreviousMediaInfo(fromUser: Boolean): MediaInfo? {
        return this.commonSkipNumbersOf(-1)
    }

    override fun tryGetNextMediaInfo(fromUser: Boolean): MediaInfo? {
        return this.commonSkipNumbersOf(1)
    }
}

private class SingleLoopMediaList(mediaList: MediaPlayingList) : MediaListParent(mediaList) {

    override fun tryGetPreviousMediaInfo(fromUser: Boolean): MediaInfo? {
        if (!fromUser) return this.mediaList.mediaInfo
        return this.commonSkipNumbersOf(-1)
    }

    override fun tryGetNextMediaInfo(fromUser: Boolean): MediaInfo? {
        if (!fromUser) return this.mediaList.mediaInfo
        return this.commonSkipNumbersOf(1)
    }
}

private class SingleMediaList(mediaList: MediaPlayingList) : MediaListParent(mediaList) {

    override fun tryGetPreviousMediaInfo(fromUser: Boolean): MediaInfo? {
        if (!fromUser) return null
        return this.commonSkipNumbersOf(-1)
    }

    override fun tryGetNextMediaInfo(fromUser: Boolean): MediaInfo? {
        if (!fromUser) return null
        return this.commonSkipNumbersOf(1)
    }
}

private class ShuffleMediaList(mediaList: MediaPlayingList) : MediaListParent(mediaList) {

    override fun tryGetPreviousMediaInfo(fromUser: Boolean): MediaInfo? {
        return handleRandom()
    }

    override fun tryGetNextMediaInfo(fromUser: Boolean): MediaInfo? {
        return handleRandom()
    }

    private fun handleRandom(): MediaInfo? {
        val randomOffset =
            (Math.random() * (mediaList.mediaInfoPlayList?.mediaInfoList?.size ?: 0)).toInt()
        return commonSkipNumbersOf(randomOffset)
    }

}

private abstract class MediaListParent(protected val mediaList: MediaPlayingList) : MediaList {
    protected fun commonSkipNumbersOf(sizeOffset: Int): MediaInfo? {
        val playList = mediaList.mediaInfoPlayList ?: return null
        val currentPosition = mediaList.indexOfCurrentPosition
        if (currentPosition !in playList.mediaInfoList.indices) {
            return null
        }
        var newPosition = currentPosition + sizeOffset
        if (newPosition !in playList.mediaInfoList.indices) {
            newPosition += playList.mediaInfoList.size
        }
        newPosition %= playList.mediaInfoList.size
        mediaList.indexOfCurrentPosition = newPosition
        mediaList.syncCurrentMediaInfo()
        return playList.mediaInfoList[newPosition]
    }
}