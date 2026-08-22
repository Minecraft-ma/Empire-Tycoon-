package com.example

import android.app.Application
import com.example.ads.AdManager

class EmpireApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Google Mobile Ads SDK and start loading real AdMob ads immediately
        AdManager.initialize(this)
    }
}
