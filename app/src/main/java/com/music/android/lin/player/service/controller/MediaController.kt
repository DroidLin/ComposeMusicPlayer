package com.music.android.lin.player.service.controller

import android.content.Context

interface MediaController {

    fun playOrResume()
}

private class MediaControllerInstance(private val context: Context) : MediaController {

}