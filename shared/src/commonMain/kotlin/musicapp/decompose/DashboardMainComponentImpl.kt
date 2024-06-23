package musicapp.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import musicapp.cache.Database
import musicapp.dashboard.DashboardViewModel
import musicapp.network.SpotifyApi


/**
 * Created by abdulbasit on 19/03/2023.
 */
class DashboardMainComponentImpl(
    componentContext: ComponentContext,
    val output: (DashboardMainComponent.Output) -> Unit,
    val spotifyApi: SpotifyApi,
    val database: Database? = null
) : DashboardMainComponent, ComponentContext by componentContext {
    override val viewModel: DashboardViewModel
        get() = instanceKeeper.getOrCreate { DashboardViewModel(spotifyApi, database) }

    override fun onOutPut(output: DashboardMainComponent.Output) {
        output(output)
    }
}