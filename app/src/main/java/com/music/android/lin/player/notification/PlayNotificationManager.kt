package com.music.android.lin.player.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.media.session.MediaSession
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import com.music.android.lin.R
import com.music.android.lin.player.metadata.MediaInfo
import com.music.android.lin.player.service.controller.MediaController
import com.music.android.lin.player.service.controller.PlayInfo
import com.music.android.lin.player.utils.collectWithScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

/**
 * @author liuzhongao
 * @since 2023/10/12 17:01
 */
internal class PlayNotificationManager constructor(
    private val service: Service,
    private val mediaController: MediaController,
    private val playInfo: PlayInfo,
    private val coroutineScope: CoroutineScope,
) {
    private val context: Context get() = this.service

    private val notificationManagerCompat = NotificationManagerCompat.from(this.context)

    private val skipToPreviousPendingIntent = PendingIntent.getBroadcast(
        this.context,
        REQUEST_CODE_PENDING_INTENT,
        withActionAndEncryptCategory(ACTION_MUSIC_CONTROLLER_SKIP_TO_PREVIOUS),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private val pausePendingIntent = PendingIntent.getBroadcast(
        this.context,
        REQUEST_CODE_PENDING_INTENT,
        withActionAndEncryptCategory(ACTION_MUSIC_CONTROLLER_PAUSE),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private val playPendingIntent = PendingIntent.getBroadcast(
        this.context,
        REQUEST_CODE_PENDING_INTENT,
        withActionAndEncryptCategory(ACTION_MUSIC_CONTROLLER_PLAY),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private val skipToNextPendingIntent = PendingIntent.getBroadcast(
        this.context,
        REQUEST_CODE_PENDING_INTENT,
        withActionAndEncryptCategory(ACTION_MUSIC_CONTROLLER_SKIP_TO_NEXT),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private val notificationControllerBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            val verificationId = intent.getStringExtra(KEY_BROADCAST_NOTIFICATION_INTENT_VERIFICATION) ?: return
            val isIdValid = verificationId.startsWith(this@PlayNotificationManager.context.packageName)
                    && verificationId.endsWith(MEDIA_NOTIFICATION_ID.toString())
            if (!isIdValid) {
                return
            }
            when (action) {
                ACTION_MUSIC_CONTROLLER_SKIP_TO_PREVIOUS -> this@PlayNotificationManager.mediaController.skipToPrevious()
                ACTION_MUSIC_CONTROLLER_PAUSE -> this@PlayNotificationManager.mediaController.pause()
                ACTION_MUSIC_CONTROLLER_PLAY -> this@PlayNotificationManager.mediaController.playOrResume()
                ACTION_MUSIC_CONTROLLER_SKIP_TO_NEXT -> this@PlayNotificationManager.mediaController.skipToNext()
            }
        }
    }

    private var mediaSessionToken: MediaSession.Token? = null

    init {
        initialize(this.service)
        this.playInfo.information
            .map { information ->
                NotificationMetadata(
                    mediaInfo = information.mediaInfo,
                    isPlaying = information.playerMetadata.isPlaying
                )
            }
            .debounce(200L)
            .distinctUntilChanged()
            .onEach(::updateMediaNotification)
            .collectWithScope(this.coroutineScope)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initialize(service: Service) {
        val notificationChannelCompat = NotificationChannelCompat.Builder(
            NOTIFICATION_CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_LOW
        )
            .setName(context.getString(R.string.string_play_service_notification_channel_name))
            .setDescription(context.getString(R.string.string_play_service_notification_channel_description))
            .setVibrationEnabled(false)
            .setLightsEnabled(false)
            .build()
        this.notificationManagerCompat.createNotificationChannel(notificationChannelCompat)

        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_MUSIC_CONTROLLER_SKIP_TO_PREVIOUS)
        intentFilter.addAction(ACTION_MUSIC_CONTROLLER_PAUSE)
        intentFilter.addAction(ACTION_MUSIC_CONTROLLER_PLAY)
        intentFilter.addAction(ACTION_MUSIC_CONTROLLER_SKIP_TO_NEXT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.context.registerReceiver(this.notificationControllerBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            this.context.registerReceiver(this.notificationControllerBroadcastReceiver, intentFilter)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            service.startForeground(MEDIA_NOTIFICATION_ID, buildEmptyNotification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            service.startForeground(MEDIA_NOTIFICATION_ID, buildEmptyNotification())
        }
    }

    fun release() {
        this.context.unregisterReceiver(this.notificationControllerBroadcastReceiver)
    }

    fun setMediaSessionToken(token: MediaSession.Token) {
        this.mediaSessionToken = token
    }

    @SuppressLint("MissingPermission")
    private suspend fun updateMediaNotification(metadata: NotificationMetadata) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(
                this@PlayNotificationManager.context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val musicInfo = metadata.mediaInfo
        if (musicInfo != null) {
            val notification = buildMediaNotification(
                mediaInfo = musicInfo,
                isPlaying = metadata.isPlaying
            )
            withContext(Dispatchers.Main) {
                notificationManagerCompat.notify(MEDIA_NOTIFICATION_ID, notification)
            }
        } else {
            withContext(Dispatchers.Main) {
                notificationManagerCompat.notify(MEDIA_NOTIFICATION_ID, buildEmptyNotification())
            }
        }
    }

    private fun buildEmptyNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this.context, NOTIFICATION_CHANNEL_ID)
        } else {
            Notification.Builder(this.context)
        }
        return builder.setContentTitle(this.context.getString(R.string.string_play_service_foreground_notification_default_empty_title))
            .setContentText(this.context.getString(R.string.string_play_service_foreground_notification_default_empty_description))
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_media_indicator)
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .build()
    }

    private suspend fun buildMediaNotification(
        mediaInfo: MediaInfo,
        isPlaying: Boolean,
    ): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this.context, NOTIFICATION_CHANNEL_ID)
        } else {
            Notification.Builder(this.context)
        }
        builder.setContentTitle(mediaInfo.mediaTitle)
            .setContentText(
                "${mediaInfo.album.albumName} - ${mediaInfo.artists.joinToString(separator = "/") { it.name }}"
            )
            .setAutoCancel(false)
            .withPreviousAction()
            .apply {
                if (isPlaying) {
                    withPauseAction()
                } else {
                    withPlayAction()
                }
            }
            .withNextAction()
            .setStyle(
                Notification.MediaStyle()
                    .setMediaSession(this.mediaSessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setSmallIcon(R.drawable.ic_media_indicator)
            .setLargeIcon(loadLargeIcon(mediaInfo = mediaInfo))
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setBadgeIconType(Notification.BADGE_ICON_LARGE)
        }
        return builder.build()
    }

    private suspend fun loadLargeIcon(mediaInfo: MediaInfo): Icon {
        val coverBitmap = loadCoverBitmap(mediaInfo = mediaInfo)
        return if (coverBitmap == null) {
            Icon.createWithResource(
                this.context,
                R.drawable.ic_resource_notification_large_icon
            )
        } else {
            Icon.createWithBitmap(coverBitmap)
        }
    }

    private suspend fun loadCoverBitmap(mediaInfo: MediaInfo): Bitmap? = withContext(Dispatchers.IO) {
        kotlin.runCatching {
            fetchImageAsBitmap(imageResourceUrl = mediaInfo.coverUri)
        }.onFailure { it.printStackTrace() }.getOrNull()
    }

    private fun Notification.Builder.withPreviousAction() = apply {
        addAction(
            Notification.Action.Builder(
                Icon.createWithResource(this@PlayNotificationManager.context, R.drawable.ic_skip_previous),
                "previous",
                this@PlayNotificationManager.skipToPreviousPendingIntent
            ).build()
        )
    }

    private fun Notification.Builder.withPauseAction() = apply {
        addAction(
            Notification.Action.Builder(
                Icon.createWithResource(this@PlayNotificationManager.context, R.drawable.ic_pause),
                "pause",
                this@PlayNotificationManager.pausePendingIntent
            ).build()
        )
    }

    private fun Notification.Builder.withPlayAction() = apply {
        addAction(
            Notification.Action.Builder(
                Icon.createWithResource(this@PlayNotificationManager.context, R.drawable.ic_play),
                "play",
                this@PlayNotificationManager.playPendingIntent
            ).build()
        )
    }

    private fun Notification.Builder.withNextAction() = apply {
        addAction(
            Notification.Action.Builder(
                Icon.createWithResource(this@PlayNotificationManager.context, R.drawable.ic_skip_next),
                "next",
                this@PlayNotificationManager.skipToNextPendingIntent
            ).build()
        )
    }

    private fun withActionAndEncryptCategory(action: String): Intent {
        return withActionIntent(action = action) {
            putExtra(KEY_BROADCAST_NOTIFICATION_INTENT_VERIFICATION, "${context.packageName}.$MEDIA_NOTIFICATION_ID")
        }
    }

    private fun withActionIntent(action: String, block: Intent.() -> Unit): Intent {
        return Intent(action).apply(block)
    }

    private data class NotificationMetadata(
        val isPlaying: Boolean,
        val mediaInfo: MediaInfo?
    )

    companion object {
        private const val TAG = "PlayNotificationManager"
        private const val KEY_BROADCAST_NOTIFICATION_INTENT_VERIFICATION = "package_name_with_notification_id"

        private const val NOTIFICATION_CHANNEL_ID = "music_player_control_channel_id"
        private const val MEDIA_NOTIFICATION_ID = 0x325

        private const val ACTION_MUSIC_CONTROLLER_SKIP_TO_PREVIOUS =
            "action_music_controller_skip_to_previous"
        private const val ACTION_MUSIC_CONTROLLER_PAUSE = "action_music_controller_pause"
        private const val ACTION_MUSIC_CONTROLLER_PLAY = "action_music_controller_play"
        private const val ACTION_MUSIC_CONTROLLER_SKIP_TO_NEXT =
            "action_music_controller_skip_to_next"

        private const val REQUEST_CODE_PENDING_INTENT = 0x325
    }
}