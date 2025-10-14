package musicapp.playerview

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

class CountdownViewModel : InstanceKeeper.Instance {

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    private var job = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + coroutineExceptionHandler + job)

    @OptIn(ExperimentalTime::class)
    fun startCountdown(initialMillis: Long, intervalMillis: Long, onCountDownFinish: () -> Unit) {
        val targetTime = kotlin.time.Clock.System.now() + initialMillis.milliseconds

        viewModelScope.launch {
            while (true) {
                val now = kotlin.time.Clock.System.now()
                val millisUntilFinished = (targetTime - now).inWholeMilliseconds

                if (millisUntilFinished <= 0) {
                    onCountDownFinish()
                    break
                }

                delay(intervalMillis)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModelScope.cancel()
    }
}