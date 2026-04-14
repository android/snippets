package androidx.security.state

import android.content.Context

open class SecurityPatchState(context: Context) {
    companion object {
        const val COMPONENT_SYSTEM = "system"
        fun getVulnerabilityReportUrl(): String = ""
    }

    fun getDeviceSecurityPatchLevel(component: String): String = ""
    fun getPublishedSecurityPatchLevel(component: String): String = ""
    fun fetchAvailableSecurityPatchLevel(component: String): String = ""
    fun isDeviceFullyUpdated(): Boolean = true
    fun loadVulnerabilityReport(jsonString: String) {}
    fun queryAllAvailableUpdates(): List<UpdateCheckResult> = emptyList()
    fun areCvesPatched(cves: List<String>): Boolean = true
    fun getPatchedCves(component: String, deviceSpl: String): List<String> = emptyList()
}

class UpdateInfo(component: String, spl: String, publishedDate: Long) {
    class Builder {
        fun setComponent(component: String) = this
        fun setSecurityPatchLevel(spl: String) = this
        fun setPublishedDateMillis(millis: Long) = this
        fun build() = UpdateInfo("", "", 0)
    }
}

class UpdateCheckResult
