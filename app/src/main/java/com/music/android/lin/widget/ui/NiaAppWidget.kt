package com.music.android.lin.widget.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.music.android.lin.R
import com.music.android.lin.widget.ui.vm.GlancePlayState
import com.music.android.lin.widget.ui.vm.GlancePlayerViewModel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.androidx.compose.koinViewModel

class NiaAppWidget : GlanceAppWidget() {

    private val mutex = Mutex()
    private val viewModelStoreOwners = HashMap<GlanceId, ViewModelStoreOwner>()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val viewModelStoreOwner = mutex.withLock {
            viewModelStoreOwners.getOrPut(id, ::AppWidgetViewModelStoreOwner)
        }
        provideContent {
            CompositionLocalProvider(
                LocalViewModelStoreOwner provides viewModelStoreOwner,
                LocalConfiguration provides context.resources.configuration,
                LocalContext provides context
            ) {
                val playViewModel = koinViewModel<GlancePlayerViewModel>()
                val playState by playViewModel.glancePlayState.collectAsState()
                GlanceTheme {
                    NiaAppWidgetContent(
                        playState = playState,
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override suspend fun onDelete(context: Context, glanceId: GlanceId) {
        mutex.withLock {
            viewModelStoreOwners.remove(glanceId)?.viewModelStore?.clear()
        }
    }

    @Composable
    fun NiaAppWidgetContent(
        playState: GlancePlayState,
        modifier: GlanceModifier = GlanceModifier,
    ) {
        Column(
            modifier = modifier
                .background(GlanceTheme.colors.widgetBackground),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            val provider = if (playState.imageBitmap != null) {
                ImageProvider(playState.imageBitmap)
            } else {
                ImageProvider(R.drawable.ic_broken_img)
            }
            Image(
                provider = provider,
                modifier = GlanceModifier.size(56.dp),
                contentDescription = "null"
            )
            Text(
                text = playState.musicItem?.musicName
                    ?: stringResource(R.string.string_unknown_music_name),
                style = TextStyle(),
                maxLines = 1
            )
            Text(
                text = playState.musicItem?.musicDescription
                    ?: stringResource(R.string.string_unknown_music_name),
                style = TextStyle(),
                maxLines = 1
            )
        }
    }

    private class AppWidgetViewModelStoreOwner : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}