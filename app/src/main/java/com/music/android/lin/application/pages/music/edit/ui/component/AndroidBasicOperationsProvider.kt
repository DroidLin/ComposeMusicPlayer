package com.music.android.lin.application.pages.music.edit.ui.component

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember

@Stable
interface AndroidBasicOperations {
    fun pickPhoto()
    fun requestPermission(permission: String)
    fun requestMultiPermission(multiPermissions: Array<String>)
}

@Stable
private class AndroidBasicOperationsImpl(
) : AndroidBasicOperations {

    var pickVisualMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>? = null
    var requestPermissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>? = null

    override fun pickPhoto() {
        val input = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        pickVisualMediaLauncher?.launch(input = input)
    }

    override fun requestPermission(permission: String) {
        requestPermissionLauncher?.launch(arrayOf(permission))
    }

    override fun requestMultiPermission(multiPermissions: Array<String>) {
        requestPermissionLauncher?.launch(multiPermissions)
    }
}

@Composable
fun rememberActivityResultOperation(
    onPermissionResult: AndroidBasicOperations.(Map<String, Boolean>) -> Unit = {},
    onPickResult: AndroidBasicOperations.(Uri?) -> Unit = {},
): AndroidBasicOperations {
    val basicOperationImpl = remember { AndroidBasicOperationsImpl() }
    val pickVisualMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { basicOperationImpl.onPickResult(it) }
    )
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { basicOperationImpl.onPermissionResult(it) }
    )
    basicOperationImpl.pickVisualMediaLauncher = pickVisualMediaLauncher
    basicOperationImpl.requestPermissionLauncher = requestPermissionLauncher
    return basicOperationImpl
}