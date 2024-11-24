package com.music.android.lin.application.music.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.android.lin.R
import com.music.android.lin.application.common.model.PlayListItem
import com.music.android.lin.application.framework.AppMaterialTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun CommonPlayListItemView(
    playList: ImmutableList<PlayListItem>,
    modifier: Modifier = Modifier,
    onPlayListPressed: (PlayListItem) -> Unit,
    goToCreatePlayList: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
    ) {
        if (playList.isEmpty()) {
            item(
                key = "empty_screen",
                contentType = "empty_screen"
            ) {
                Box(
                    modifier = Modifier.fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.string_no_play_list_exist),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = goToCreatePlayList
                        ) {
                            Text(
                                text = stringResource(R.string.string_create_playlist),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            return@LazyColumn
        }
        items(
            items = playList,
            key = { it.id },
            contentType = { it::class.qualifiedName }
        ) { itemList ->
            PlayListItemView(
                playList = itemList,
                modifier = Modifier.fillParentMaxWidth(),
                onClick = { onPlayListPressed(itemList) },
            )
        }
    }
}

@Composable
@Preview
private fun PlayListItemViewPreview(modifier: Modifier = Modifier) {
    AppMaterialTheme {
        CommonPlayListItemView(
            playList = persistentListOf(),
            onPlayListPressed = {},
            goToCreatePlayList = {},
            modifier = modifier,
        )
    }
}