import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import musicapp.decompose.MusicRootImpl
import musicapp.network.SpotifyApiImpl
import musicapp.player.MediaPlayerController
import musicapp.utils.PlatformContext
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        val lifecycle = LifecycleRegistry()
        val rootComponent =
            MusicRootImpl(
                componentContext = DefaultComponentContext(
                    lifecycle = lifecycle,
                ), api = SpotifyApiImpl(), mediaPlayerController = MediaPlayerController(
                    PlatformContext()
                )
            )

        lifecycle.resume()
        ComposeViewport("MusicApp-KMP") {
            CommonMainWeb(rootComponent)
        }
    }
}



