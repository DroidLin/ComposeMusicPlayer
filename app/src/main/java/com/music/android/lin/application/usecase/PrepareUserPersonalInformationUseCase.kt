package com.music.android.lin.application.usecase

import android.content.Context
import androidx.compose.runtime.Stable

internal class PrepareUserPersonalInformationUseCase(private val applicationContext: Context) {

}

@Stable
data class PersonalInfo(
    val name: String,
    val description: String,

)