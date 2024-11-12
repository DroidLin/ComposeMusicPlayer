package com.music.android.lin.player.repositories

import com.music.android.lin.player.utils.ExtensionMapImpl
import com.music.android.lin.player.utils.ExtensionMap
import com.music.android.lin.player.database.metadata.LocalMusicInfo
import com.music.android.lin.player.database.metadata.LocalPlayList
import org.json.JSONArray
import org.json.JSONObject

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

internal val LocalPlayList.extensionsMap: ExtensionMap
    get() = ExtensionMapImpl(extensionsStr = extensionsStr)

internal val LocalMusicInfo.artistsIdList: List<String>
    get() = artistIds.split(",")