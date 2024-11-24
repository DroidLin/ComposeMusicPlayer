package com.music.android.lin.application.settings.ui.vm

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.android.lin.application.common.ui.state.DataLoadState
import com.music.android.lin.application.common.ui.state.withDataLoadState
import com.music.android.lin.application.settings.model.SettingSection
import com.music.android.lin.application.settings.model.SettingSectionItem
import com.music.android.lin.application.settings.model.SettingSectionType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.annotations.TestOnly

@get:TestOnly
internal val fakeData: List<SettingSection> get() = prepareSettingSections()

@Stable
internal class AppSettingsViewModel(private val applicationContext: Context) : ViewModel() {

    val settingsList = flowOf(prepareSettingSections())
        .withDataLoadState { it }
        .stateIn(this.viewModelScope, SharingStarted.WhileSubscribed(5000), DataLoadState.Loading)
}

private fun prepareSettingSections(): List<SettingSection> {
    val arraylist = arrayListOf<SettingSection>()
    arraylist += SettingSection(
        sectionName = "",
        description = "application basic announcement",
        sectionItems = persistentListOf(
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
        sectionItems = persistentListOf(
            SettingSectionItem(
                itemType = SettingSectionType.ResetAll
            )
        )
    )
    return arraylist
}