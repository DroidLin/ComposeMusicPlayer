package com.music.android.lin.application.framework.vm

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import com.music.android.lin.application.util.dataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * @author: liuzhongao
 * @since: 2024/10/25 00:29
 */
internal class AppFrameworkViewModel(private val applicationContext: Context) : ViewModel() {

    private val dataStore = this.applicationContext.dataStore

    val firstGuideCompleted = this.dataStore.data
        .map { it[firstEnterGuideCompletedKey] ?: false }
        .distinctUntilChanged()

    suspend fun operateFirstGuideComplete() {
        dataStore.edit {
            it[firstEnterGuideCompletedKey] = true
        }
    }

    companion object {
        private val firstEnterGuideCompletedKey = booleanPreferencesKey("firstEnterGuideCompleted")
    }
}