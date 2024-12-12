package com.music.android.lin.player.metadata

private const val KEY_SORT_TYPE = "sort_type"

var PlayList.sortType: PlayList.SortType
    set(value) {
        this.extensions.putInt(KEY_SORT_TYPE, value.value)
    }
    get() = PlayList.SortType.fromValue(this.extensions.getInt(KEY_SORT_TYPE, -1))

fun MediaInfoPlayList.sortBySortType(sortType: PlayList.SortType): MediaInfoPlayList {
    val mediaInfoList = this.mediaInfoList.toMutableList()
    when (sortType) {
        PlayList.SortType.Title -> mediaInfoList.sortBy { it.mediaTitle }
        PlayList.SortType.Artist -> mediaInfoList.sortBy { it.artists.getOrNull(0)?.name }
        PlayList.SortType.AddOrder -> mediaInfoList.sortBy { it.updateTimeStamp }
        PlayList.SortType.AddOrderDesc -> mediaInfoList.sortByDescending { it.updateTimeStamp }
        else -> mediaInfoList.sortBy { it.updateTimeStamp }
    }
    return MediaInfoPlayList(
        id = id,
        type = type,
        name = name,
        description = description,
        playListCover = playListCover,
        mediaInfoList = mediaInfoList,
        extensions = extensions,
        updateTimeStamp = updateTimeStamp,
        countOfPlayable = countOfPlayable
    )
}