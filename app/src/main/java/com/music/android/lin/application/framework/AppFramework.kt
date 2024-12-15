package com.music.android.lin.application.framework

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.music.android.lin.application.framework.vm.AppFrameworkViewModel
import com.music.android.lin.application.guide.ui.AppFirstGuide
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * @author: liuzhongao
 * @since: 2024/10/24 23:46
 */
@Composable
fun AppFramework(content: @Composable () -> Unit) {
    val viewModel = koinViewModel<AppFrameworkViewModel>()
    val firstEnterGuideComplete by viewModel.firstGuideCompleted.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val firstGuideComplete = firstEnterGuideComplete
    when {
        firstGuideComplete == null -> Box(modifier = Modifier.fillMaxSize())
        firstGuideComplete -> content()
        else -> {
            AppFirstGuide(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .navigationBarsPadding(),
                onComplete = {
                    coroutineScope.launch {
                        viewModel.operateFirstGuideComplete()
                    }
                }
            )
        }
    }
}