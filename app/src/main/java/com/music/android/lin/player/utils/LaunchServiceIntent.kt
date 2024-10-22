package com.harvest.musicplayer.utils

import android.content.Intent

/**
 * @author liuzhongao
 * @since 2024/1/27 20:19
 */

var Intent.userToken: String
    set(value) { putExtra("userToken", value) }
    get() = getStringExtra("userToken") ?: ""