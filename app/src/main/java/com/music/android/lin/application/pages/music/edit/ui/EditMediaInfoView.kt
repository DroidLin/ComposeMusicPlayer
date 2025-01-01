package com.music.android.lin.application.pages.music.edit.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.music.android.lin.R
import com.music.android.lin.application.common.ui.BackButton
import com.music.android.lin.application.common.ui.component.NiaImage
import com.music.android.lin.application.common.ui.component.TopAppBarLayout
import com.music.android.lin.application.common.ui.component.loadingOrFailure
import com.music.android.lin.application.common.ui.state.dataOrNull
import com.music.android.lin.application.pages.music.edit.ui.component.BackConfirmDialog
import com.music.android.lin.application.pages.music.edit.ui.component.rememberActivityResultOperation
import com.music.android.lin.application.pages.music.edit.ui.vm.EditMediaInfoState
import com.music.android.lin.application.pages.music.edit.ui.vm.EditMediaInfoViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Stable
@Serializable
data class EditMediaInfo(val mediaInfoId: String)

fun NavController.navigateToEditMediaInfo(mediaInfoId: String) {
    navigate(route = EditMediaInfo(mediaInfoId))
}

fun NavGraphBuilder.editMediaInfoView(
    backPress: () -> Unit,
) {
    composable<EditMediaInfo> {
        EditMediaInfoView(
            backPress = backPress,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun EditMediaInfoView(
    backPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<EditMediaInfoViewModel>()
    val loadState by viewModel.loadState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    var showBackConfirmDialog by remember { mutableStateOf(false) }

    val backPressDispatcher = LocalOnBackPressedDispatcherOwner.current
    val backPressIntercept: () -> Unit = backPressDispatcher?.onBackPressedDispatcher?.let { it::onBackPressed } ?: backPress

    val backIntercept by remember {
        derivedStateOf { loadState.dataOrNull?.isModified ?: false }
    }
    BackHandler(enabled = backIntercept) { showBackConfirmDialog = true }
    Column(
        modifier = modifier
    ) {
        TopHeader(
            backPress = backPressIntercept,
            modifier = Modifier.fillMaxWidth(),
            saveModifications = {
                viewModel.postToDatabase(onComplete = backPress)
            },
            saveEnabled = backIntercept
        )
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            loadingOrFailure(
                loadState = loadState,
                content = LazyListScope::listView
            )
        }
    }
    BackConfirmDialog(
        show = showBackConfirmDialog,
        modifier = Modifier,
        onDismissRequest = { showBackConfirmDialog = false },
        confirmExit = { showBackConfirmDialog = false; backPress() }
    )
}

private fun LazyListScope.listView(data: EditMediaInfoState) {
    item(
        key = "media_info_cover"
    ) {
        EditCoverImage(
            modifier = Modifier.fillParentMaxWidth(),
            imageUri = data.coverUri,
            onImageUrlChange = { data.updateCoverUrl(it.toString()) }
        )
    }
}

@Composable
fun EditCoverImage(
    imageUri: String?,
    onImageUrlChange: (Uri) -> Unit,
    modifier: Modifier = Modifier,
) {
    val requirePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else Manifest.permission.READ_EXTERNAL_STORAGE
    val context = LocalContext.current
    val activityResultOperation = rememberActivityResultOperation(
        onPermissionResult = { grantResult ->
            val isPermissionGranted = grantResult[requirePermission] ?: false
            if (isPermissionGranted) {
                this.pickPhoto()
            }
        },
        onPickResult = { data ->
            if (data != null) {
                context.contentResolver.takePersistableUriPermission(data, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                onImageUrlChange(data)
            }
        }
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .aspectRatio(1f),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.large,
            onClick = {
                if (ContextCompat.checkSelfPermission(context, requirePermission) != PackageManager.PERMISSION_GRANTED) {
                    activityResultOperation.requestPermission(requirePermission)
                    return@Surface
                }
                activityResultOperation.pickPhoto()
            }
        ) {
            NiaImage(
                url = imageUri,
                contentDescription = "edit_cover_image",
                contentScale = ContentScale.Crop
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopHeader(
    modifier: Modifier = Modifier,
    saveEnabled: Boolean = false,
    backPress: () -> Unit = {},
    saveModifications: () -> Unit = {}
) {
    TopAppBarLayout(
        modifier = modifier,
        title = {
            Text(text = stringResource(R.string.string_edit_view_title))
        },
        navigationIcon = {
            BackButton(onClick = backPress)
        },
        actions = {
            Button(
                onClick = saveModifications,
                enabled = saveEnabled
            ) {
                Text(text = stringResource(R.string.string_save_modifications))
            }
        }
    )
}