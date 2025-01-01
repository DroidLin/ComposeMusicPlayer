package com.music.android.lin.application.framework.router.impl

import android.content.Context
import android.content.Intent
import com.music.android.lin.application.common.usecase.MediaResourceGeneratorUseCase
import com.music.android.lin.application.framework.router.UriContext
import com.music.android.lin.application.framework.router.UriHandler
import com.music.android.lin.application.pages.settings.usecase.scanner.fetchMediaInfoWithUri
import com.music.android.lin.modules.AppIdentifier
import com.music.android.lin.player.metadata.MediaInfoPlayList
import com.music.android.lin.player.metadata.PlayListType
import com.music.android.lin.player.service.controller.MediaController
import com.music.android.lin.player.utils.ExtensionMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Qualifier
import java.util.logging.Logger

@Factory
internal class AudioContentUriHandler(
    private val logger: Logger,
    @Qualifier(name = AppIdentifier.applicationContext)
    private val applicationContext: Context,
    private val mediaController: MediaController,
    private val mediaResourceGeneratorUseCase: MediaResourceGeneratorUseCase,
) : UriHandler {

    override suspend fun handle(context: UriContext): Boolean {
        val intent = context.intent
        val uri = intent.data ?: return false
        val mimeType = intent.getStringExtra(Intent.EXTRA_MIME_TYPES)
            ?.takeIf { it.isNotEmpty() && it.startsWith("audio") }
            ?: withContext(Dispatchers.IO) { applicationContext.contentResolver.getType(uri) }
            ?: return false
        logger.info("find matched uri: ${uri}, mimeType: $mimeType")

        val mediaInfoList = applicationContext.fetchMediaInfoWithUri(uri)
        if (mediaInfoList.isNotEmpty()) {
            logger.info("find media info: ${mediaInfoList.size}\n${mediaInfoList.joinToString(separator = "\n") { "${it.id}_${it.mediaTitle}" }}")
            val generateTimestamp = System.currentTimeMillis()
            val tmpPlayList = MediaInfoPlayList(
                id = "tmp_playlist_from_external_${generateTimestamp}",
                type = PlayListType.LocalMusic,
                name = "临时歌单",
                playListCover = null,
                description = "创建时间：${generateTimestamp}",
                mediaInfoList = mediaInfoList,
                extensions = ExtensionMap.EmptyExtension,
                updateTimeStamp = generateTimestamp,
            )
            val mediaResource =
                mediaResourceGeneratorUseCase.startPlayResource(playList = tmpPlayList, index = 0)
            if (mediaResource != null) {
                mediaController.playMediaResource(mediaResource)
            }
        }

        return true
    }
}