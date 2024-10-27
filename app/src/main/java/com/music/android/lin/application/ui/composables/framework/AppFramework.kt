package com.music.android.lin.application.ui.composables.framework

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.android.lin.application.ui.composables.guide.AppFirstGuide
import com.music.android.lin.application.ui.vm.AppFrameworkViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * @author: liuzhongao
 * @since: 2024/10/24 23:46
 */
@Composable
fun AppFramework(content: @Composable () -> Unit) {
    val viewModel = koinViewModel<AppFrameworkViewModel>()
    val firstEnterGuideComplete by viewModel.firstGuideCompleted.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()

    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = firstEnterGuideComplete,
        label = "firstEnterGuideAnimation",
        contentAlignment = Alignment.Center,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) { firstGuideComplete ->
        if (firstGuideComplete) {
            content()
        } else {
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