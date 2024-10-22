package com.harvest.musicplayer.repositories

import com.harvest.musicplayer.ExtensionMap
import com.music.android.lin.player.ExtensionMapImpl
import com.harvest.musicplayer.metadata.local.LocalMusicInfo
import com.harvest.musicplayer.metadata.local.LocalPlayList
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author liuzhongao
 * @since 2023/10/6 11:14
 */


internal fun JSONObject.toMap(): Map<String, Any> {
    return HashMap<String, Any>().also { hashMap ->
        for (key in keys()) {
            when (val value = opt(key)) {
                is JSONObject -> value.toMap()
                is JSONArray -> value.toList()
                else -> value
            }.also { convertResult -> hashMap[key] = convertResult }
        }
    }
}

internal fun JSONArray.toList(): List<Any> {
    return ArrayList<Any>().also { list ->
        for (index in 0 until length()) {
            when (val value = opt(index)) {
                is JSONObject -> value.toMap()
                is JSONArray -> value.toList()
                else -> value
            }.also { convertResult -> list += convertResult }
        }
    }
}

internal val LocalPlayList.musicInfoIdList: List<String>
    get() = musicInfoIdStr.split(",").filter { it.isNotEmpty() && it.isNotBlank() }

internal val LocalPlayList.extensionsMap: ExtensionMap
    get() = ExtensionMapImpl(extensionsStr = extensionsStr)

internal val LocalMusicInfo.artistsIdList: List<String>
    get() = artistIds.split(",")