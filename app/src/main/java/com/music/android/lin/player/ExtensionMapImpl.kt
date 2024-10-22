package com.music.android.lin.player

import com.harvest.musicplayer.ExtensionMap
import com.harvest.musicplayer.repositories.toMap
import org.json.JSONObject

/**
 * @author liuzhongao
 * @since 2023/10/6 13:56
 */
internal class ExtensionMapImpl : ExtensionMap {

    private val innerExtensionMap: MutableMap<String, Any>

    constructor() {
        this.innerExtensionMap = HashMap()
    }

    constructor(extensionsStr: String) {
        this.innerExtensionMap = (kotlin.runCatching {
            JSONObject(extensionsStr)
        }.getOrNull() ?: JSONObject()).toMap().toMutableMap()
    }

    constructor(extensionMap: Map<String, Any>) {
        this.innerExtensionMap = extensionMap.toMutableMap()
    }

    override fun putInt(key: String, value: Int): Boolean {
        this.innerExtensionMap[key] = value
        return this.innerExtensionMap.containsKey(key) && this.innerExtensionMap.containsValue(key)
    }

    override fun putFloat(key: String, value: Float): Boolean {
        this.innerExtensionMap[key] = value
        return this.innerExtensionMap.containsKey(key) && this.innerExtensionMap.containsValue(key)
    }

    override fun putDouble(key: String, value: Double): Boolean {
        this.innerExtensionMap[key] = value
        return this.innerExtensionMap.containsKey(key) && this.innerExtensionMap.containsValue(key)
    }

    override fun putBoolean(key: String, value: Boolean): Boolean {
        this.innerExtensionMap[key] = value
        return this.innerExtensionMap.containsKey(key) && this.innerExtensionMap.containsValue(key)
    }

    override fun putString(key: String, value: String): Boolean {
        this.innerExtensionMap[key] = value
        return this.innerExtensionMap.containsKey(key) && this.innerExtensionMap.containsValue(key)
    }

    override fun getInt(key: String, default: Int): Int {
        return this.innerExtensionMap[key] as? Int ?: default
    }

    override fun getFloat(key: String, default: Float): Float {
        return this.innerExtensionMap[key] as? Float ?: default
    }

    override fun getDouble(key: String, default: Double): Double {
        return this.innerExtensionMap[key] as? Double ?: default
    }

    override fun getBoolean(key: String, default: Boolean): Boolean {
        return this.innerExtensionMap[key] as? Boolean ?: default
    }

    override fun getString(key: String, default: String): String {
        return this.innerExtensionMap[key] as? String ?: default
    }

    override fun getAll(): Map<String, Any> {
        return this.innerExtensionMap.toMap()
    }

    override fun clear() {
        this.innerExtensionMap.clear()
    }

    override fun remove(key: String): Boolean {
        this.innerExtensionMap.remove(key)
        return !this.innerExtensionMap.containsKey(key) && !this.innerExtensionMap.containsValue(key)
    }

    override fun toJson(): String {
        val tmpMap = this.innerExtensionMap.toMap()
        return JSONObject(tmpMap).toString()
    }

    companion object {
        private const val serialVersionUID: Long = -4072686560762069781L
    }
}