package com.music.android.lin.application.guide.ui

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.android.lin.R
import com.music.android.lin.application.framework.AppMaterialTheme
import com.music.android.lin.application.guide.ui.component.ScannerLoadingDialog
import com.music.android.lin.application.guide.ui.component.ScanningResultDialog
import com.music.android.lin.application.guide.ui.state.ScannerUiState
import com.music.android.lin.application.guide.ui.state.ScanningState
import com.music.android.lin.application.guide.ui.vm.MediaInformationScannerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MediaInformationScannerView(
    modifier: Modifier = Modifier,
    backPress: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    val viewModel = koinViewModel<MediaInformationScannerViewModel>()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    MediaInformationContentScanner(
        modifier = modifier,
        backPress = backPress,
        onComplete = onComplete,
        state = uiState,
        switchUseAndroidScanner = viewModel::switchUseAndroidScanner,
        launchScanner = viewModel::scannerLauncher
    )
    val showLoadingDialog = remember {
        derivedStateOf {
            uiState.value.scanningState is ScanningState.Loading
        }
    }
    ScannerLoadingDialog(
        modifier = Modifier,
        show = showLoadingDialog.value,
        cancelScanning = viewModel::cancelScanning
    )
    val showResultDialog = remember {
        derivedStateOf {
            uiState.value.scanningState is ScanningState.Data
        }
    }
    val scanningData = remember {
        derivedStateOf {
            uiState.value.scanningState as? ScanningState.Data
        }
    }
    ScanningResultDialog(
        modifier = Modifier,
        scanningData = scanningData.value,
        show = showResultDialog.value,
        confirmSave = viewModel::confirmSave,
        onDismissRequest = viewModel::cancelScanning
    )
}

@Composable
private fun MediaInformationContentScanner(
    state: State<ScannerUiState>,
    switchUseAndroidScanner: (Boolean) -> Unit,
    backPress: () -> Unit = {},
    onComplete: () -> Unit = {},
    launchScanner: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CenterContent(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.85f)
                .height(this.maxHeight * 0.8f),
            switchUseAndroidScanner = switchUseAndroidScanner,
            useAndroidScannerState = remember { derivedStateOf { state.value.useAndroidScanner } },
            launchScanner = launchScanner,
            scanningState = remember { derivedStateOf { state.value.scanningState} }
        )
        BottomFooter(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp),
            backPress = backPress,
            onComplete = onComplete
        )
    }
}

@Composable
private fun CenterContent(
    useAndroidScannerState: State<Boolean>,
    scanningState: State<ScanningState>,
    switchUseAndroidScanner: (Boolean) -> Unit,
    launchScanner: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(120.dp),
            painter = painterResource(id = R.drawable.ic_media_indicator),
            contentDescription = "icon_permission",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.string_scan_local_media_content),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = stringResource(id = R.string.string_scan_local_media_content_description),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                switchUseAndroidScanner(!useAndroidScannerState.value)
            }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.string_scan_media_through_android_content_provider),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.string_scan_media_through_android_content_provider_description),
                        color = LocalContentColor.current.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = useAndroidScannerState.value,
                    onCheckedChange = switchUseAndroidScanner
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedContent(
            targetState = scanningState.value,
            label = "button_animation",
             contentAlignment = Alignment.Center
        ) { state ->
            when (state) {
                is ScanningState.Complete -> {
                    Button(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }

                is ScanningState.Loading -> {
                    IconButton(onClick = {}) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                else -> {
                    Button(
                        onClick = launchScanner
                    ) {
                        Text(text = stringResource(R.string.string_launch_scanner))
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomFooter(
    modifier: Modifier = Modifier,
    backPress: () -> Unit = {},
    onComplete: () -> Unit = {}
) {
    Box(
        modifier = modifier
    ) {
        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = backPress
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Button(
            modifier = Modifier.align(Alignment.CenterEnd),
            onClick = onComplete
        ) {
            Text(
                text = stringResource(R.string.string_guide_complete),
            )
        }
    }
}

@Preview
@Composable
private fun BottomFooterPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        BottomFooter(
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ScannerInformationPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        MediaInformationContentScanner(
            state = remember { derivedStateOf { ScannerUiState() } },
            switchUseAndroidScanner = {}
        )
    }
}