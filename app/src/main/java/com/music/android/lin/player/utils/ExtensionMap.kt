package com.music.android.lin.player.utils

import java.io.Serializable

/**
 * @author liuzhongao
 * @since 2023/10/6 11:21
 */
interface ExtensionMap : Serializable {
    fun putInt(key: String, value: Int): Boolean
    fun putFloat(key: String, value: Float): Boolean
    fun putDouble(key: String, value: Double): Boolean
    fun putBoolean(key: String, value: Boolean): Boolean
    fun putString(key: String, value: String): Boolean

    fun getInt(key: String, default: Int = 0): Int
    fun getFloat(key: String, default: Float = 0f): Float
    fun getDouble(key: String, default: Double = 0.0): Double
    fun getBoolean(key: String, default: Boolean = false): Boolean
    fun getString(key: String, default: String = ""): String

    fun getAll(): Map<String, Any>
    fun clear()
    fun remove(key: String): Boolean
    fun toJson(): String
}