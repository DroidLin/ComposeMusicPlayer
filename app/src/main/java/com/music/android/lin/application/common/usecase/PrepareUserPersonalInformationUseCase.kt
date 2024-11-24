package com.music.android.lin.application.common.usecase

import android.content.Context
import androidx.compose.runtime.Stable

internal class PrepareUserPersonalInformationUseCase(private val applicationContext: Context) {

}

@Stable
data class PersonalInfo(
    val name: String,
    val description: String,

)