package com.music.android.lin.player.metadata

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.view.Surface
import java.io.Serializable

private const val KEY_OPERATION = "key_operation"
private const val KEY_DATA = "key_data"
private const val KEY_SURFACE = "key_surface"

var PlayMessage.command: Int?
    set(value) {
        this[KEY_OPERATION] = value
    }
    get() = this[KEY_OPERATION]

var PlayMessage.data: Any?
    set(value) {
        this[KEY_DATA] = value
    }
    get() = this[KEY_DATA]

var PlayMessage.surface: Surface?
    set(value) {
        this[KEY_SURFACE] = value
    }
    get() = this[KEY_SURFACE]

class PlayMessage() : Parcelable {
    private val innerParameters = HashMap<String, Any?>()

    constructor(parcel: Parcel) : this() {
        val hashMap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readHashMap(
                PlayMessage::class.java.classLoader,
                String::class.java,
                Any::class.java
            ) as Map<String, Any?>
        } else {
            parcel.readHashMap(PlayMessage::class.java.classLoader) as Map<String, Any?>
        }
        this.innerParameters.putAll(hashMap)
    }

    operator fun set(key: String, value: Any?) {
        if (value !is Serializable || value !is Parcelable) {
            throw IllegalArgumentException("illegal value: ${value}.")
        }
        this.innerParameters[key] = value
    }

    operator fun <T> get(key: String): T? {
        return this.innerParameters[key] as? T
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeMap(this.innerParameters)
    }

    companion object {
        @JvmStatic
        fun ofCommand(command: Int): PlayMessage {
            val playMessage = PlayMessage()
            playMessage.command = command
            return playMessage
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<PlayMessage> {
            override fun createFromParcel(parcel: Parcel): PlayMessage {
                return PlayMessage(parcel)
            }

            override fun newArray(size: Int): Array<PlayMessage?> {
                return arrayOfNulls(size)
            }
        }
    }
}