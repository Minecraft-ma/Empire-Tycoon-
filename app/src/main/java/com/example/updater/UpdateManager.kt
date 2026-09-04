package com.example.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

const val GITHUB_REPO_OWNER = "Minecraft-ma"
const val GITHUB_REPO_NAME = "Empire-Tycoon-"
const val GITHUB_API_LATEST_RELEASE = "https://api.github.com/repos/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/releases/latest"
const val GITHUB_RELEASES_URL = "https://github.com/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/releases"
const val ITCH_IO_PAGE_URL = "https://itch.io"

/**
 * Information regarding an available app update.
 */
data class AppUpdateInfo(
    val isAvailable: Boolean = false,
    val currentVersion: String = BuildConfig.VERSION_NAME,
    val latestVersion: String = "",
    val releaseTitle: String = "",
    val releaseNotes: String = "",
    val apkDownloadUrl: String? = null,
    val releasePageUrl: String = GITHUB_RELEASES_URL,
    val apkSizeMb: Double = 0.0,
    val isManualCheck: Boolean = false,
    val errorMessage: String? = null
)

sealed class UpdateCheckState {
    object Idle : UpdateCheckState()
    object Checking : UpdateCheckState()
    data class UpdateAvailable(val info: AppUpdateInfo) : UpdateCheckState()
    data class UpToDate(val currentVersion: String) : UpdateCheckState()
    data class Error(val message: String) : UpdateCheckState()
}

/**
 * UpdateManager handles automatic and manual checking of application updates
 * published on GitHub Releases and itch.io.
 */
object UpdateManager {
    private const val TAG = "UpdateManager"
    const val REPO_OWNER = GITHUB_REPO_OWNER
    const val REPO_NAME = GITHUB_REPO_NAME
    const val RELEASES_URL = GITHUB_RELEASES_URL
    const val ITCH_URL = ITCH_IO_PAGE_URL

    private val _updateState = MutableStateFlow<UpdateCheckState>(UpdateCheckState.Idle)
    val updateState: StateFlow<UpdateCheckState> = _updateState.asStateFlow()

    private val _activeUpdateInfo = MutableStateFlow<AppUpdateInfo?>(null)
    val activeUpdateInfo: StateFlow<AppUpdateInfo?> = _activeUpdateInfo.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    /**
     * Checks if a new release is available on GitHub.
     * Can be called quietly on app start (isManual = false) or from Settings (isManual = true).
     */
    fun checkForUpdates(context: Context, isManual: Boolean = false) {
        _updateState.value = UpdateCheckState.Checking

        coroutineScope.launch {
            try {
                val updateInfo = fetchLatestReleaseFromGitHub(isManual)
                withContext(Dispatchers.Main) {
                    if (updateInfo != null && updateInfo.isAvailable) {
                        _activeUpdateInfo.value = updateInfo
                        _updateState.value = UpdateCheckState.UpdateAvailable(updateInfo)
                    } else if (updateInfo != null && !updateInfo.isAvailable) {
                        _updateState.value = UpdateCheckState.UpToDate(BuildConfig.VERSION_NAME)
                    } else {
                        val err = "Impossible de joindre le serveur de mise à jour."
                        _updateState.value = if (isManual) UpdateCheckState.Error(err) else UpdateCheckState.Idle
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check for updates: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    _updateState.value = if (isManual) {
                        UpdateCheckState.Error("Erreur réseau: vérifiez votre connexion Internet.")
                    } else {
                        UpdateCheckState.Idle
                    }
                }
            }
        }
    }

    /**
     * Dismiss the current update prompt.
     */
    fun dismissUpdateDialog() {
        _activeUpdateInfo.value = null
        _updateState.value = UpdateCheckState.Idle
    }

    /**
     * Initiates the download of the APK or opens the game's release page in the browser.
     */
    fun launchUpdateDownload(context: Context, urlToOpen: String? = null) {
        val targetUrl = urlToOpen 
            ?: _activeUpdateInfo.value?.apkDownloadUrl 
            ?: _activeUpdateInfo.value?.releasePageUrl 
            ?: GITHUB_RELEASES_URL

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open update URL: $targetUrl", e)
        }
    }

    /**
     * Internal network request to fetch the latest GitHub release.
     */
    private suspend fun fetchLatestReleaseFromGitHub(isManual: Boolean): AppUpdateInfo? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(GITHUB_API_LATEST_RELEASE)
            connection = url.openConnection() as HttpURLConnection
            connection.apply {
                connectTimeout = 6000
                readTimeout = 6000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "EmpireTycoon-Android-App")
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val jsonString = reader.use { it.readText() }
                parseReleaseJson(jsonString, isManual)
            } else {
                Log.w(TAG, "GitHub API returned HTTP code: $responseCode")
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Network exception while fetching release: ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun parseReleaseJson(jsonString: String, isManual: Boolean): AppUpdateInfo {
        val json = JSONObject(jsonString)
        val tagName = json.optString("tag_name", "").trim()
        val releaseName = json.optString("name", "Nouvelle version")
        val bodyNotes = json.optString("body", "Améliorations de performances et corrections de bugs.")
        val htmlUrl = json.optString("html_url", GITHUB_RELEASES_URL)

        var apkDownloadUrl: String? = null
        var apkSizeMb = 0.0

        val assetsArray = json.optJSONArray("assets")
        if (assetsArray != null) {
            for (i in 0 until assetsArray.length()) {
                val asset = assetsArray.getJSONObject(i)
                val assetName = asset.optString("name", "")
                if (assetName.endsWith(".apk", ignoreCase = true)) {
                    apkDownloadUrl = asset.optString("browser_download_url", null)
                    val sizeBytes = asset.optLong("size", 0L)
                    apkSizeMb = if (sizeBytes > 0) sizeBytes / (1024.0 * 1024.0) else 0.0
                    break
                }
            }
        }

        val currentVersion = BuildConfig.VERSION_NAME
        val isNewer = isVersionNewer(tagName, currentVersion)

        return AppUpdateInfo(
            isAvailable = isNewer,
            currentVersion = currentVersion,
            latestVersion = tagName.ifEmpty { "Dernière version" },
            releaseTitle = releaseName,
            releaseNotes = bodyNotes,
            apkDownloadUrl = apkDownloadUrl,
            releasePageUrl = htmlUrl,
            apkSizeMb = apkSizeMb,
            isManualCheck = isManual
        )
    }

    /**
     * Compares version strings (e.g., "v1.2", "1.1.0", "1.0").
     */
    fun isVersionNewer(remoteTag: String, currentVer: String): Boolean {
        if (remoteTag.isBlank()) return false
        
        // Clean prefixes like "v" or "release-"
        val cleanRemote = remoteTag.replace(Regex("(?i)^[a-z_\\-]+"), "").trim()
        val cleanCurrent = currentVer.replace(Regex("(?i)^[a-z_\\-]+"), "").trim()

        if (cleanRemote.equals(cleanCurrent, ignoreCase = true)) return false

        val remoteParts = cleanRemote.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = cleanCurrent.split(".").mapNotNull { it.toIntOrNull() }

        val length = maxOf(remoteParts.size, currentParts.size)
        for (i in 0 until length) {
            val r = remoteParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }

        // If numeric parts are equal or tag is a named release (e.g. "latest") and remote tag != current tag
        return false
    }
}
