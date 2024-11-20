package com.music.android.lin.application.settings.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import com.music.android.lin.R

@Stable
data class SettingSection(
    val sectionName: String,
    val description: String,
    val sectionItems: List<SettingSectionItem>
)

@Stable
data class SettingSectionItem(
    val itemType: SettingSectionType
)

@Stable
enum class SettingSectionType(
    @DrawableRes val iconResId: Int,
    @StringRes val nameResId: Int
) {
    About(
        iconResId = R.drawable.ic_about,
        nameResId = R.string.string_about_app
    ),
    ThirdPartyDataShareManifest(
        iconResId = R.drawable.ic_third_party_share_manifest,
        nameResId = R.string.string_privacy_third_party_share_manifest
    ),
    PrivacyUsageAndShareManifest(
        iconResId = R.drawable.ic_privacy_usage_and_share_manifest,
        nameResId = R.string.string_privacy_collection_and_usage_manifest
    ),
    PrivacyProtection(
        iconResId = R.drawable.ic_privacy_protection,
        nameResId = R.string.string_privacy_protection
    ),
    ResetAll(iconResId = R.drawable.ic_reset_all, nameResId = R.string.string_reset_all)
}