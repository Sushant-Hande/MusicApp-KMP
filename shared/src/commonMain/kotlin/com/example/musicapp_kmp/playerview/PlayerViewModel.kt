package com.example.musicapp_kmp.playerview

import com.example.musicapp_kmp.decompose.PlayerComponent
import com.example.musicapp_kmp.player.MediaPlayerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Created by abdulbasit on 26/02/2023.
 */
class PlayerViewModel(
    mediaPlayerController: MediaPlayerController,
    playerInputs: SharedFlow<PlayerComponent.Input>
) {
    private val viewModelScope = CoroutineScope(Dispatchers.Unconfined)
    val chartDetailsViewState =
        MutableStateFlow(PlayerViewState(trackList = emptyList(), mediaPlayerController = mediaPlayerController))

    init {
        viewModelScope.launch {
            playerInputs.collectLatest {
                when (it) {
                    is PlayerComponent.Input.PlayTrack ->
                        chartDetailsViewState.value = chartDetailsViewState.value.copy(playingTrackId = it.trackId)

                    is PlayerComponent.Input.UpdateTracks ->
                        chartDetailsViewState.value = chartDetailsViewState.value.copy(trackList = it.tracksList)
                }
            }
        }
    }

    companion object {
        private lateinit var playerViewModel: PlayerViewModel
        fun getPlayerViewModel(
            mediaPlayerController: MediaPlayerController,
            playerInputs: SharedFlow<PlayerComponent.Input>
        ): PlayerViewModel {
            if (!::playerViewModel.isInitialized) {
                playerViewModel = PlayerViewModel(mediaPlayerController, playerInputs)
            }
            return playerViewModel
        }
    }
}