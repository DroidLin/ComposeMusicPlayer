package com.music.android.lin.application.framework.vm

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.util.dataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * @author: liuzhongao
 * @since: 2024/10/25 00:29
 */
@Stable
internal class AppFrameworkViewModel(private val applicationContext: Context) : ViewModel() {

    private val dataStore = this.applicationContext.dataStore

    val firstGuideCompleted = this.dataStore.data
        .map { it[firstEnterGuideCompletedKey] ?: false }
        .distinctUntilChanged()
        .stateIn(this.viewModelScope, SharingStarted.Eagerly, null)

    suspend fun operateFirstGuideComplete() {
        dataStore.edit {
            it[firstEnterGuideCompletedKey] = true
        }
    }

    companion object {
        private val firstEnterGuideCompletedKey = booleanPreferencesKey("firstEnterGuideCompleted")
    }
}