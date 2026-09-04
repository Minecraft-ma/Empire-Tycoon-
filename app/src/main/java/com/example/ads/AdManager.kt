package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.BuildConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * AdState represents the current lifecycle status of AdMob advertisements.
 */
sealed class AdLifecycleState {
    object Uninitialized : AdLifecycleState()
    object Idle : AdLifecycleState()
    object Loading : AdLifecycleState()
    object Ready : AdLifecycleState()
    object Showing : AdLifecycleState()
    data class Error(val code: Int, val message: String) : AdLifecycleState()
}

/**
 * AdManager is the dedicated manager handling the entire lifecycle of AdMob ads:
 * - Initialization with Google Mobile Ads SDK
 * - Pre-loading & automated exponential backoff on network failures
 * - Safe fullscreen presentation with reward callbacks
 * - Complete error handling and state exposure via StateFlow for Jetpack Compose UI
 */
object AdManager {
    private const val TAG = "AdManager"

    // Real AdMob IDs configured for the project
    const val ADMOB_APP_ID = "ca-app-pub-9856111598579327~6580859360"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-9856111598579327/8702103163"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9856111598579327/6431163043"

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var retryAttempt = 0
    private const val MAX_RETRY_DELAY_MS = 30000L

    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var isCurrentlyLoading: Boolean = false
    private var isInterstitialLoading: Boolean = false

    // StateFlows exposed to UI
    private val _lifecycleState = MutableStateFlow<AdLifecycleState>(AdLifecycleState.Uninitialized)
    val lifecycleState: StateFlow<AdLifecycleState> = _lifecycleState.asStateFlow()

    private val _isAdReady = MutableStateFlow(false)
    val isAdReady: StateFlow<Boolean> = _isAdReady.asStateFlow()

    private val _isInterstitialReady = MutableStateFlow(false)
    val isInterstitialReady: StateFlow<Boolean> = _isInterstitialReady.asStateFlow()

    private val _adStatusMessage = MutableStateFlow("Initialisation AdMob...")
    val adStatusMessage: StateFlow<String> = _adStatusMessage.asStateFlow()

    private val _totalRewardsEarned = MutableStateFlow(0)
    val totalRewardsEarned: StateFlow<Int> = _totalRewardsEarned.asStateFlow()

    /**
     * Detects if the current runtime is an Android Emulator or debug build.
     */
    fun isEmulatorOrDebug(): Boolean {
        return BuildConfig.DEBUG ||
                Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                "google_sdk" == Build.PRODUCT
    }

    /**
     * Initializes the Google Mobile Ads SDK at application startup.
     */
    fun initialize(context: Context, onInitialized: (() -> Unit)? = null) {
        try {
            Log.d(TAG, "Initializing Google Mobile Ads SDK with emulator test configuration...")

            // Register emulator as test device to avoid unsafe cross-origin production ads on emulators
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)

            MobileAds.initialize(context) { initializationStatus ->
                Log.d(TAG, "AdMob initialized successfully: ${initializationStatus.adapterStatusMap}")
                _lifecycleState.value = AdLifecycleState.Idle
                _adStatusMessage.value = "AdMob initialisé."
                onInitialized?.invoke()
                if (!isEmulatorOrDebug()) {
                    loadRewardedAd(context.applicationContext)
                    loadInterstitialAd(context.applicationContext)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MobileAds SDK", e)
            _lifecycleState.value = AdLifecycleState.Error(-1, e.message ?: "Erreur d'initialisation")
            _adStatusMessage.value = "Erreur initialisation AdMob : ${e.localizedMessage}"
        }
    }

    /**
     * Loads a Rewarded Ad from AdMob.
     * Prevents overlapping concurrent load calls and handles errors cleanly.
     */
    fun loadRewardedAd(
        context: Context,
        onLoaded: (() -> Unit)? = null,
        onFailed: ((String) -> Unit)? = null
    ) {
        if (isEmulatorOrDebug()) {
            Log.d(TAG, "Emulator runtime: interactive simulated rewarded ad engine active.")
            _isAdReady.value = false
            _lifecycleState.value = AdLifecycleState.Idle
            _adStatusMessage.value = "Mode interactif prêt."
            onLoaded?.invoke()
            return
        }

        if (isCurrentlyLoading) {
            Log.d(TAG, "RewardedAd is already loading. Skipping redundant request.")
            return
        }

        if (rewardedAd != null) {
            Log.d(TAG, "RewardedAd is already loaded and ready to show.")
            _isAdReady.value = true
            _lifecycleState.value = AdLifecycleState.Ready
            onLoaded?.invoke()
            return
        }

        isCurrentlyLoading = true
        _lifecycleState.value = AdLifecycleState.Loading
        _adStatusMessage.value = "Chargement de la publicité..."

        val adRequest = AdRequest.Builder().build()

        // Prioritize official test unit on emulators and debug builds for maximum compatibility
        val primaryUnitId = if (isEmulatorOrDebug()) TEST_REWARDED_AD_UNIT_ID else REWARDED_AD_UNIT_ID
        val fallbackUnitId = if (primaryUnitId == TEST_REWARDED_AD_UNIT_ID) REWARDED_AD_UNIT_ID else TEST_REWARDED_AD_UNIT_ID

        RewardedAd.load(
            context,
            primaryUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "RewardedAd loaded successfully with unit: $primaryUnitId")
                    rewardedAd = ad
                    isCurrentlyLoading = false
                    retryAttempt = 0
                    _isAdReady.value = true
                    _lifecycleState.value = AdLifecycleState.Ready
                    _adStatusMessage.value = "Publicité prête !"
                    onLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Primary unit ($primaryUnitId) failed ($loadAdError), trying fallback unit: $fallbackUnitId...")
                    RewardedAd.load(
                        context,
                        fallbackUnitId,
                        adRequest,
                        object : RewardedAdLoadCallback() {
                            override fun onAdLoaded(fallbackAd: RewardedAd) {
                                Log.d(TAG, "Fallback RewardedAd loaded successfully!")
                                rewardedAd = fallbackAd
                                isCurrentlyLoading = false
                                retryAttempt = 0
                                _isAdReady.value = true
                                _lifecycleState.value = AdLifecycleState.Ready
                                _adStatusMessage.value = "Publicité prête !"
                                onLoaded?.invoke()
                            }

                            override fun onAdFailedToLoad(fallbackError: LoadAdError) {
                                Log.w(TAG, "Fallback RewardedAd also failed: ${fallbackError.message}")
                                rewardedAd = null
                                isCurrentlyLoading = false
                                _isAdReady.value = false
                                val errorMessage = parseAdMobError(loadAdError.code, loadAdError.message)
                                _lifecycleState.value = AdLifecycleState.Error(loadAdError.code, errorMessage)
                                _adStatusMessage.value = errorMessage
                                onFailed?.invoke(errorMessage)
                                scheduleRetry(context)
                            }
                        }
                    )
                }
            }
        )
    }

    /**
     * Displays the loaded Rewarded Ad in the provided Activity.
     * Triggers callback for user reward, and automatically reloads the next ad once dismissed.
     */
    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: (RewardItem) -> Unit,
        onAdClosed: () -> Unit,
        onAdUnavailable: (errorMessage: String) -> Unit
    ) {
        val ad = rewardedAd
        if (ad == null) {
            val errorMsg = _adStatusMessage.value
            Log.w(TAG, "Cannot show ad: no ad currently available ($errorMsg)")
            onAdUnavailable(errorMsg)
            loadRewardedAd(activity)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded ad opened fullscreen.")
                _lifecycleState.value = AdLifecycleState.Showing
                _adStatusMessage.value = "Affichage de la publicité en cours..."
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Rewarded ad dismissed by user.")
                rewardedAd = null
                _isAdReady.value = false
                _lifecycleState.value = AdLifecycleState.Idle
                _adStatusMessage.value = "Publicité terminée."
                onAdClosed()
                // Automatically preload next ad for seamless subsequent experiences
                loadRewardedAd(activity.applicationContext)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Ad failed to show: ${adError.message} (code ${adError.code})")
                rewardedAd = null
                _isAdReady.value = false
                val errorMsg = "Échec d'affichage AdMob (${adError.code}) : ${adError.message}"
                _lifecycleState.value = AdLifecycleState.Error(adError.code, errorMsg)
                _adStatusMessage.value = errorMsg
                onAdUnavailable(errorMsg)
                loadRewardedAd(activity.applicationContext)
            }

            override fun onAdImpression() {
                Log.d(TAG, "Rewarded ad impression recorded.")
            }

            override fun onAdClicked() {
                Log.d(TAG, "Rewarded ad clicked by user.")
            }
        }

        try {
            ad.show(activity) { rewardItem ->
                Log.d(TAG, "User earned reward: amount=${rewardItem.amount}, type=${rewardItem.type}")
                _totalRewardsEarned.value += 1
                onUserEarnedReward(rewardItem)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception while showing rewarded ad: ${e.message}", e)
            rewardedAd = null
            _isAdReady.value = false
            onAdUnavailable("Erreur d'affichage : ${e.localizedMessage}")
            loadRewardedAd(activity.applicationContext)
        }
    }

    /**
     * Helper to schedule an automated reload after an ad failure.
     */
    private fun scheduleRetry(context: Context) {
        retryAttempt++
        val delayMillis = (1000L * (1 shl retryAttempt.coerceAtMost(5))).coerceAtMost(MAX_RETRY_DELAY_MS)
        Log.d(TAG, "Scheduling ad reload retry #$retryAttempt in ${delayMillis}ms")
        scope.launch {
            delay(delayMillis)
            loadRewardedAd(context)
        }
    }

    /**
     * Translates AdMob error codes into user-friendly diagnostic messages.
     */
    private fun parseAdMobError(code: Int, defaultMessage: String): String {
        return when (code) {
            AdRequest.ERROR_CODE_NO_FILL ->
                "AdMob : Aucune publicité disponible pour l'instant (Code 3 No-Fill)."
            AdRequest.ERROR_CODE_NETWORK_ERROR ->
                "AdMob : Problème de connexion réseau (Code 2)."
            AdRequest.ERROR_CODE_INVALID_REQUEST ->
                "AdMob : Requête publicitaire invalide (Code 1)."
            AdRequest.ERROR_CODE_INTERNAL_ERROR ->
                "AdMob : Erreur interne des serveurs Google (Code 0)."
            else ->
                "AdMob : Code $code - $defaultMessage"
        }
    }

    /**
     * Loads an Interstitial Ad from AdMob.
     */
    fun loadInterstitialAd(context: Context) {
        if (isEmulatorOrDebug()) {
            _isInterstitialReady.value = false
            return
        }

        if (isInterstitialLoading) {
            return
        }

        if (interstitialAd != null) {
            _isInterstitialReady.value = true
            return
        }

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "InterstitialAd loaded successfully with unit: $INTERSTITIAL_AD_UNIT_ID")
                    interstitialAd = ad
                    isInterstitialLoading = false
                    _isInterstitialReady.value = true
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "InterstitialAd failed to load: ${loadAdError.message}")
                    interstitialAd = null
                    isInterstitialLoading = false
                    _isInterstitialReady.value = false
                    // Retry loading after a delay
                    scope.launch {
                        delay(20000)
                        loadInterstitialAd(context)
                    }
                }
            }
        )
    }

    /**
     * Shows the loaded Interstitial Ad in the provided Activity.
     */
    fun showInterstitialAd(activity: Activity, onAdClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            Log.w(TAG, "Cannot show Interstitial Ad: no ad available")
            onAdClosed()
            loadInterstitialAd(activity.applicationContext)
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial ad opened fullscreen.")
                _isInterstitialReady.value = false
            }

            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed by user.")
                interstitialAd = null
                _isInterstitialReady.value = false
                onAdClosed()
                loadInterstitialAd(activity.applicationContext)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Interstitial ad failed to show: ${adError.message}")
                interstitialAd = null
                _isInterstitialReady.value = false
                onAdClosed()
                loadInterstitialAd(activity.applicationContext)
            }
        }

        try {
            ad.show(activity)
        } catch (e: Throwable) {
            Log.e(TAG, "Exception while showing interstitial ad: ${e.message}", e)
            interstitialAd = null
            _isInterstitialReady.value = false
            onAdClosed()
            loadInterstitialAd(activity.applicationContext)
        }
    }
}
