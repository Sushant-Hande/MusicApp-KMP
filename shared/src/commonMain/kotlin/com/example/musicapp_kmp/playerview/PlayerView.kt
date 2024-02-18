package com.example.musicapp_kmp.playerview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.musicapp_kmp.decompose.PlayerComponent
import com.example.musicapp_kmp.network.models.topfiftycharts.Item
import com.example.musicapp_kmp.player.MediaPlayerController
import com.example.musicapp_kmp.player.MediaPlayerListener
import com.seiko.imageloader.rememberAsyncImagePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
internal fun PlayerView(playerComponent: PlayerComponent) {
    val state = playerComponent.viewModel.chartDetailsViewState.collectAsState()
    val mediaPlayerController = remember { state.value.mediaPlayerController }
    val selectedTrackPlaying = state.value.playingTrackId
    val trackList = state.value.trackList
    val scope = rememberCoroutineScope()

    val selectedIndex = remember { mutableStateOf(0) }
    val isLoading = remember { mutableStateOf(true) }
    val progress = remember { mutableStateOf(0f) }
    val selectedTrack = trackList[selectedIndex.value]

    scope.launch {
        delay(1000)
        progress.value += 1
    }

    //the index was not getting reset
    LaunchedEffect(trackList) { selectedIndex.value = 0 }

    LaunchedEffect(selectedTrackPlaying) {
        if (selectedTrackPlaying.isEmpty().not()) selectedIndex.value =
            trackList.indexOfFirst { item -> item.track?.id.orEmpty() == selectedTrackPlaying }
    }

    LaunchedEffect(selectedTrack) {
        playerComponent.onOutPut(PlayerComponent.Output.OnTrackUpdated(selectedTrack.track?.id.orEmpty()))
    }


    playTrack(
        selectedTrack = selectedTrack,
        mediaPlayerController = mediaPlayerController,
        isLoading = { isLoading.value = it },
        currentIndex = selectedIndex.value,
        updateSelectedIndex = { selectedIndex.value = it },
        onProgressUpdated = {/* progress.value = it*/ },
        trackList = trackList
    )

    Player(
        selectedTrack = selectedTrack,
        isLoading = isLoading.value,
        progress = progress.value,
        selectedIndex = selectedIndex.value,
        onSelectedIndexUpdated = { selectedIndex.value = it },
        mediaPlayerController = mediaPlayerController,
        trackList = trackList
    )
}

@Composable
private fun Player(
    selectedTrack: Item,
    isLoading: Boolean,
    selectedIndex: Int,
    onSelectedIndexUpdated: (index: Int) -> Unit,
    mediaPlayerController: MediaPlayerController,
    trackList: List<Item>,
    progress: Float
) {
    Box(
        modifier = Modifier.fillMaxWidth().background(Color(0xCC101010))
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 56.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.wrapContentSize()) {
                val painter = rememberAsyncImagePainter(
                    selectedTrack.track?.album?.images?.first()?.url.orEmpty()
                )
                Box(modifier = Modifier.clip(RoundedCornerShape(5.dp)).width(49.dp).height(49.dp)) {
                    Image(
                        painter,
                        selectedTrack.track?.album?.images?.first()?.url.orEmpty(),
                        modifier = Modifier.clip(RoundedCornerShape(5.dp)).width(49.dp).height(49.dp),
                        contentScale = ContentScale.Crop
                    )
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize().background(Color(0x80000000))) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center).padding(8.dp),
                                color = Color(0xFFFACD66),
                            )
                        }
                    }
                }
                Column(Modifier.padding(start = 8.dp).align(Alignment.Top)) {
                    Text(
                        text = selectedTrack.track?.name ?: "", style = MaterialTheme.typography.caption.copy(
                            color = Color(
                                0XFFEFEEE0
                            )
                        )
                    )
                    Text(
                        text = selectedTrack.track?.artists?.map { it.name }?.joinToString(",") ?: "",
                        style = MaterialTheme.typography.caption.copy(
                            color = Color(
                                0XFFEFEEE0
                            )
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Box(modifier = Modifier.background(Color.Red).height(30.dp).width(100.dp)) {
                Text("Update Time is $progress")
            }
            Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    tint = Color(0xFFFACD66),
                    contentDescription = "Back",
                    modifier = Modifier.padding(end = 8.dp).size(32.dp).align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            if (selectedIndex - 1 >= 0) {
                                onSelectedIndexUpdated(selectedIndex - 1)
                            }
                        })
                )
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    tint = Color(0xFFFACD66),
                    contentDescription = "Play",
                    modifier = Modifier.padding(end = 8.dp).size(32.dp).align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            if (mediaPlayerController.isPlaying()) {
                                mediaPlayerController.pause()
                            } else {
                                mediaPlayerController.start()
                            }
                        })
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    tint = Color(0xFFFACD66),
                    contentDescription = "Forward",
                    modifier = Modifier.padding(end = 8.dp).size(32.dp).align(Alignment.CenterVertically)
                        .clickable(onClick = {
                            if (selectedIndex < trackList.size - 1) {
                                onSelectedIndexUpdated(selectedIndex + 1)
                            }
                        })
                )
            }
        }
    }
}

private fun playTrack(
    selectedTrack: Item,
    mediaPlayerController: MediaPlayerController,
    currentIndex: Int,
    trackList: List<Item>,
    isLoading: (isLoading: Boolean) -> Unit,
    updateSelectedIndex: (index: Int) -> Unit,
    onProgressUpdated: (progress: Float) -> Unit,
) {
    selectedTrack.track?.previewUrl?.let {
        mediaPlayerController.prepare(it, listener = object : MediaPlayerListener {
            override fun onReady() {
                mediaPlayerController.start()
                isLoading(false)
            }

            override fun onVideoCompleted() {
                if (currentIndex < trackList.size - 1) {
                    updateSelectedIndex(currentIndex + 1)
                }
            }

            override fun onError() {
                if (currentIndex < trackList.size - 1) {
                    updateSelectedIndex(currentIndex + 1)
                }
            }

            override fun onProgress(value: Float) {
                onProgressUpdated(value)
            }
        })
    } ?: run {
        if (currentIndex < trackList.size - 1) {
            updateSelectedIndex(currentIndex + 1)
        } else {
            // selectedIndex.value = 0
        }
    }
}