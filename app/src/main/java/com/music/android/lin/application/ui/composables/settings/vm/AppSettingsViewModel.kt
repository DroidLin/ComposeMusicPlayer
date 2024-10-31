package com.music.android.lin.application.ui.composables.settings.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.model.DataLoadState
import com.music.android.lin.application.ui.composables.settings.model.SettingSection
import com.music.android.lin.application.ui.composables.settings.model.SettingSectionItem
import com.music.android.lin.application.ui.composables.settings.model.SettingSectionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

internal class AppSettingsViewModel(private val applicationContext: Context) : ViewModel() {

    val settingsList = flow<DataLoadState> {
        val arraylist = prepareSettingSections()
        emit(DataLoadState.Data(arraylist))
    }.onStart {
        emit(DataLoadState.Loading)
    }.catch {
        emit(DataLoadState.Failure(message = it.message ?: "", errorCode = -1))
    }.stateIn(this.viewModelScope, SharingStarted.WhileSubscribed(5000), DataLoadState.Loading)
}

private fun prepareSettingSections(): List<SettingSection> {
    val arraylist = arrayListOf<SettingSection>()
    arraylist += SettingSection(
        sectionName = "",
        description = "application basic announcement",
        sectionItems = listOf(
            SettingSectionItem(
                itemType = SettingSectionType.PrivacyProtection
            ),
            SettingSectionItem(
                itemType = SettingSectionType.ThirdPartyDataShareManifest
            ),
            SettingSectionItem(
                itemType = SettingSectionType.PrivacyUsageAndShareManifest
            ),
            SettingSectionItem(
                itemType = SettingSectionType.About
            ),
        )
    )
    arraylist += SettingSection(
        sectionName = "",
        description = "quick operations",
        sectionItems = listOf(
            SettingSectionItem(
                itemType = SettingSectionType.ResetAll
            )
        )
    )
    return arraylist
}