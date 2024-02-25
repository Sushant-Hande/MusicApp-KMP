package com.example.musicapp_kmp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.musicapp_kmp.chartdetails.ChartDetailsScreen
import com.example.musicapp_kmp.chartdetails.ChartDetailsScreenLarge
import com.example.musicapp_kmp.chartdetails.ChartDetailsViewModel
import com.example.musicapp_kmp.dashboard.DashboardScreen
import com.example.musicapp_kmp.dashboard.DashboardScreenLarge
import com.example.musicapp_kmp.dashboard.DashboardViewModel
import com.example.musicapp_kmp.decompose.ChartDetailsComponent
import com.example.musicapp_kmp.decompose.PlayerComponent
import com.example.musicapp_kmp.network.SpotifyApi
import com.example.musicapp_kmp.player.MediaPlayerController
import com.example.musicapp_kmp.playerview.PlayerView
import com.example.musicapp_kmp.playerview.PlayerViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
internal fun MainCommon(
    spotifyApi: SpotifyApi, mediaPlayerController: MediaPlayerController, isLargeScreen: Boolean
) {

    val scope = rememberCoroutineScope()

    val currentScreen = remember { mutableStateOf<Screen>(Screen.Dashboard) }
    val chartInput = MutableSharedFlow<ChartDetailsComponent.Input>()
    val currentPlaySong = remember { mutableStateOf("") }
    val playerInput = MutableSharedFlow<PlayerComponent.Input>()

    MusicAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (val current = currentScreen.value) {
                    Screen.Dashboard -> {
                        if (isLargeScreen) DashboardScreenLarge(viewModel = DashboardViewModel.getDashboardViewModel(
                            spotifyApi
                        ),
                            navigateToDetails = { currentScreen.value = Screen.Details(it) })
                        else DashboardScreen(viewModel = DashboardViewModel.getDashboardViewModel(spotifyApi),
                            navigateToDetails = { currentScreen.value = Screen.Details(it) })
                    }

                    is Screen.Details -> {
                        if (isLargeScreen) ChartDetailsScreenLarge(viewModel = ChartDetailsViewModel.getChartDetailsViewModel(
                            spotifyApi = spotifyApi,
                            playListId = current.playlistId,
                            playingTrackId = currentPlaySong.value,
                            chatDetailsInput = chartInput,
                        ), onPlayAllClicked = {
                            scope.launch {
                                playerInput.emit(PlayerComponent.Input.UpdateTracks(it))
                            }

                        }, onPlayTrack = {
                            scope.launch {
                                playerInput.emit(PlayerComponent.Input.PlayTrack(it))
                            }
                        }, goBack = {
                            currentScreen.value = Screen.Dashboard
                        })
                        else ChartDetailsScreen(viewModel = ChartDetailsViewModel.getChartDetailsViewModel(
                            spotifyApi = spotifyApi,
                            playListId = current.playlistId,
                            playingTrackId = currentPlaySong.value,
                            chatDetailsInput = chartInput,
                        ), onPlayAllClicked = {
                            playerInput.tryEmit(PlayerComponent.Input.UpdateTracks(it))
                        }, onPlayTrack = {
                            playerInput.tryEmit(PlayerComponent.Input.PlayTrack(it))
                        }, goBack = {
                            currentScreen.value = Screen.Dashboard
                        })
                    }
                }
            }
            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                PlayerView(viewModel = PlayerViewModel.getPlayerViewModel(
                    mediaPlayerController = mediaPlayerController,
                    playerInputs = playerInput,
                ), onTrackUpdated = {
                    scope.launch {
                        chartInput.emit(ChartDetailsComponent.Input.TrackUpdated(it))
                    }
                })
            }
        }
    }
}


sealed interface Screen {
    data object Dashboard : Screen
    data class Details(val playlistId: String) : Screen
}
