package com.music.android.lin.application.util

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring


fun <T> applicationAnimationSpec(): FiniteAnimationSpec<T> = spring(stiffness = Spring.StiffnessLow)