package com.music.android.lin.application.pages.guide.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.BackButton
import com.music.android.lin.application.framework.AppMaterialTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Stable
@Serializable
data object PermissionTestAndAcquire

fun NavController.navigateToPermissionTestAndAcquire() {
    navigate(PermissionTestAndAcquire) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.permissionTestAndAcquireView(
    goNext: () -> Unit,
    backPress: () -> Unit,
) {
    composable<PermissionTestAndAcquire> {
        PermissionTestAndAcquireView(
            backPress = backPress,
            goNext = goNext,
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        )
    }
}

private val checkPermissionGranted: Context.(String) -> Boolean = { permission ->
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun PermissionTestAndAcquireView(
    modifier: Modifier = Modifier,
    backPress: () -> Unit = {},
    goNext: () -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var readMediaFilePermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }.let { context.checkPermissionGranted(it) }
        )
    }
    var notificationPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.checkPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)
            } else NotificationManagerCompat.from(context).areNotificationsEnabled()
        )
    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        for ((permission, isGranted) in result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (permission == Manifest.permission.READ_MEDIA_AUDIO) {
                    if (!isGranted) {
                        coroutineScope.launch {
                            val message = ContextCompat.getString(
                                context,
                                R.string.string_read_media_permission_denied_toast
                            )
                            snackBarHostState.showSnackbar(message)
                        }
                    }
                    readMediaFilePermissionGranted = isGranted
                }
                if (permission == Manifest.permission.POST_NOTIFICATIONS) {
                    if (!isGranted) {
                        coroutineScope.launch {
                            val message = ContextCompat.getString(
                                context,
                                R.string.string_post_notifications_permission_denied_toast
                            )
                            snackBarHostState.showSnackbar(message)
                        }
                    }
                    notificationPermissionGranted = isGranted
                }
            }

        }
        readMediaFilePermissionGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result[Manifest.permission.READ_MEDIA_AUDIO] ?: readMediaFilePermissionGranted
            } else result[Manifest.permission.READ_EXTERNAL_STORAGE]
                ?: readMediaFilePermissionGranted
        notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            result[Manifest.permission.POST_NOTIFICATIONS] ?: notificationPermissionGranted
        } else notificationPermissionGranted
    }

    val goNextPreConfirm: () -> Unit = {
        coroutineScope.launch {
            if (!readMediaFilePermissionGranted) {
                val result = snackBarHostState.showSnackbar(
                    message = ContextCompat.getString(context, R.string.string_are_you_sure_to_go_next_with_out_read_media_permission),
                    actionLabel = ContextCompat.getString(context, R.string.string_confirm),
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                if (result == SnackbarResult.ActionPerformed) {
                    goNext()
                }
            } else {
                goNext()
            }
        }
    }

    val requireMediaPermission = requireMediaPermission@{
        if (readMediaFilePermissionGranted) {
            return@requireMediaPermission
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mediaPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_AUDIO))
        } else {
            mediaPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    val requireNotificationPermission = requireNotificationPermission@{
        if (notificationPermissionGranted) {
            return@requireNotificationPermission
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mediaPermissionLauncher.launch(arrayOf(Manifest.permission.POST_NOTIFICATIONS))
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.85f)
                .height(this.maxHeight * 0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.ic_permission),
                contentDescription = "icon_permission",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.string_permission_acquire),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = stringResource(id = R.string.string_permission_acquire_description),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier,
                onClick = requireMediaPermission
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.string_read_media_permission),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(id = R.string.string_read_media_permission_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalContentColor.current.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Switch(checked = readMediaFilePermissionGranted, onCheckedChange = { requireMediaPermission() })
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier,
                onClick = requireNotificationPermission
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(id = R.string.string_post_notifications),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(id = R.string.string_post_notifications_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalContentColor.current.copy(alpha = 0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Switch(checked = notificationPermissionGranted, onCheckedChange = { requireNotificationPermission() })
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
        ) {
            BackButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = backPress
            )
            TextButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = goNextPreConfirm
            ) {
                Text(text = stringResource(id = R.string.string_guide_go_next))
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.BottomCenter),
            hostState = snackBarHostState
        )
    }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_UNDEFINED)
fun PermissionPreview() {
    AppMaterialTheme {
        Surface {
            PermissionTestAndAcquireView()
        }
    }
}