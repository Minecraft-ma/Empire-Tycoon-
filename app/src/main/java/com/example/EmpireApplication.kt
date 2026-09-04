package com.example

import android.app.Application
import android.content.Context
import android.os.Build
import android.webkit.WebView
import com.example.ads.AdManager
import java.io.File

class EmpireApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        setupWebViewEnvironment(this)

        // Initialize Google Mobile Ads SDK with test device configuration
        AdManager.initialize(this)
    }

    private fun setupWebViewEnvironment(context: Context) {
        try {
            // Ensure proper WebView data directory suffix for multi-process or container environments
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val processName = Application.getProcessName()
                if (context.packageName != processName) {
                    WebView.setDataDirectorySuffix(processName)
                }
            }

            // Ensure cache and WebView storage directories exist and are clean of corrupt simple-cache files
            val cacheDir = context.cacheDir
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val webViewCacheDir = File(cacheDir, "WebView")
            if (webViewCacheDir.exists() && (!webViewCacheDir.canWrite() || !webViewCacheDir.canRead())) {
                webViewCacheDir.deleteRecursively()
            }
        } catch (e: Throwable) {
            // Gracefully ignore sandbox/container restrictions
        }
    }
}
