package com.example.musicapp_kmp.chartdetails

import com.example.musicapp_kmp.decompose.ChartDetailsComponent
import com.example.musicapp_kmp.network.SpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * Created by abdulbasit on 26/02/2023.
 */
class ChartDetailsViewModel(
    api: SpotifyApi,
    playlistId: String,
    playingTrackId: String,
    chatDetailsInput: SharedFlow<ChartDetailsComponent.Input>
) {

    private val viewModelScope = CoroutineScope(Dispatchers.Unconfined)
    val chartDetailsViewState = MutableStateFlow<ChartDetailsViewState>(ChartDetailsViewState.Loading)

    init {
        viewModelScope.launch {
            launch {
                try {
                    val playlist = api.getPlayList(playlistId)
                    chartDetailsViewState.value = ChartDetailsViewState.Success(
                        chartDetails = playlist, playingTrackId = playingTrackId
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    chartDetailsViewState.value = ChartDetailsViewState.Failure(e.message.toString())
                }
            }
            launch {
                chatDetailsInput.collectLatest {
                    when (it) {
                        is ChartDetailsComponent.Input.TrackUpdated -> when (val state = chartDetailsViewState.value) {
                            is ChartDetailsViewState.Success -> {
                                chartDetailsViewState.emit(state.copy(playingTrackId = it.trackId))
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    companion object {
        private lateinit var chartDetailsViewModel: ChartDetailsViewModel
        fun getChartDetailsViewModel(
            spotifyApi: SpotifyApi,
            playListId: String,
            playingTrackId: String,
            chatDetailsInput: SharedFlow<ChartDetailsComponent.Input>,
        ): ChartDetailsViewModel {
            return ChartDetailsViewModel(
                api = spotifyApi,
                playlistId = playListId,
                playingTrackId = playingTrackId,
                chatDetailsInput = chatDetailsInput
            )
        }
    }
}