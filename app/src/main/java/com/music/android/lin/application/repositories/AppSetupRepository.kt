package com.music.android.lin.application.repositories

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.music.android.lin.application.util.dataStore
import com.music.android.lin.modules.AppIdentifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Qualifier

interface AppSetupRepository {

    val isInitialized: Flow<Boolean>

    suspend fun setInitialized(initialized: Boolean)
}

@Factory(binds = [AppSetupRepository::class])
internal class AppSetupRepositoryImpl constructor(
    @Qualifier(name = AppIdentifier.applicationContext)
    private val context: Context,
) : AppSetupRepository {

    override val isInitialized: Flow<Boolean> = context.dataStore.data
        .map { it[appGuidePreferenceKey] ?: false }

    override suspend fun setInitialized(initialized: Boolean) {
        context.dataStore.edit { it[appGuidePreferenceKey] = initialized }
    }

    companion object {
        private val appGuidePreferenceKey = booleanPreferencesKey("firstEnterGuideCompleted")
    }
}