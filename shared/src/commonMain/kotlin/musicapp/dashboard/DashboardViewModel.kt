package musicapp.dashboard

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import musicapp.cache.Database
import musicapp.cache.FavoritePlayList
import musicapp.network.SpotifyApi


/**
 * Created by abdulbasit on 26/02/2023.
 */
class DashboardViewModel(api: SpotifyApi, val database: Database? = null) :
    InstanceKeeper.Instance {
    val dashboardState = MutableStateFlow<DashboardViewState>(DashboardViewState.Loading)

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
        dashboardState.value = DashboardViewState.Failure(exception.message.toString())
    }

    private val job = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + coroutineExceptionHandler + job)

    init {
        viewModelScope.launch {
            try {
                val topFiftyCharts = async { api.getTopFiftyChart() }.await()
                val newReleasedAlbums = async { api.getNewReleases() }.await()
                val featuredPlaylist = async { api.getFeaturedPlaylist() }.await()

                val savedFavoritePlayList = database?.getFavoritePlayList()
                val savedPlayListIds = savedFavoritePlayList?.map { it.href }
                savedPlayListIds?.let {
                    featuredPlaylist.playlists?.items?.forEach {
                        if (it.href.toString() in savedPlayListIds) {
                            it.isFavorite = true
                        }
                    }
                }

                dashboardState.value = DashboardViewState.Success(
                    topFiftyCharts = topFiftyCharts,
                    newReleasedAlbums = newReleasedAlbums,
                    featuredPlayList = featuredPlaylist
                )
            } catch (e: Exception) {
                e.printStackTrace()
                dashboardState.value = DashboardViewState.Failure(e.message.toString())
            }
        }
    }

    fun saveFavoritePlayList(favoritePlayList: FavoritePlayList) {
        viewModelScope.launch {
            database?.saveFavoritePlayList(favoritePlayList)
        }
    }

    fun removePlayListFromFavorite(favoritePlayList: FavoritePlayList) {
        viewModelScope.launch {
            database?.deleteFavoritePlayList(favoritePlayList.href)
        }
    }

    override fun onDestroy() {
        viewModelScope.cancel()
    }
}