package com.music.android.lin.application.util

import androidx.compose.foundation.lazy.LazyListState

const val offset = 10

suspend fun LazyListState.fastScrollToItem(index: Int) {
    val indexOfFirstItem = this.layoutInfo.visibleItemsInfo.firstOrNull()?.index
    val indexOfLastItem = this.layoutInfo.visibleItemsInfo.lastOrNull()?.index
    if (indexOfFirstItem == null || indexOfLastItem == null) {
        return
    }

    if (index in (indexOfFirstItem - offset)..(indexOfLastItem + offset)) {
        animateScrollToItem(index, 0)
    } else if (index < (indexOfFirstItem - offset)) {
        scrollToItem((index + offset), 0)
        animateScrollToItem(index, 0)
    } else if (index > (indexOfLastItem + offset)) {
        scrollToItem((index - offset), 0)
        animateScrollToItem(index, 0)
    }
}