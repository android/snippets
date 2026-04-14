package androidx.security.state.provider

import android.content.Context
import android.content.Intent
import androidx.security.state.UpdateInfo

open class UpdateInfoService {
    val updateInfoManager = UpdateInfoManager(null)
    open suspend fun fetchUpdates(): List<UpdateInfo> = emptyList()
    open fun shouldFetchUpdates(): Boolean = false
    open fun onRequestCompleted(telemetry: UpdateCheckTelemetry) {}
    open fun onClientConnected(intent: Intent) {}
    open fun getCallerUid(): Int = 0
    open fun onFetchFailed(e: Exception) {}
}

class UpdateInfoManager(context: Context?) {
    fun registerUpdate(updateInfo: UpdateInfo) {}
    fun unregisterUpdate(updateInfo: UpdateInfo) {}
    fun setLastCheckTimeMillis(millis: Long) {}
    fun getLastCheckTimeMillis(): Long = 0
}

class UpdateCheckTelemetry {
    val outcome = Outcome.CACHE_HIT
    val totalLatencyMillis: Long = 0

    enum class Outcome {
        CACHE_HIT
    }
}
