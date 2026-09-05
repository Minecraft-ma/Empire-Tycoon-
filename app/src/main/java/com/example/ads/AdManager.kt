package com.example.ads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * AdLifecycleState defines the internal state of company ad campaigns and sponsor contracts.
 */
sealed class AdLifecycleState {
    object Idle : AdLifecycleState()
    object Ready : AdLifecycleState()
    object ActiveCampaign : AdLifecycleState()
    data class Completed(val campaignName: String, val revenue: Double) : AdLifecycleState()
}

/**
 * AdManager manages the company's internal incremental ad system and sponsored campaigns.
 * No external SDK or third-party ad networks are required.
 */
object AdManager {
    private val _lifecycleState = MutableStateFlow<AdLifecycleState>(AdLifecycleState.Ready)
    val lifecycleState: StateFlow<AdLifecycleState> = _lifecycleState.asStateFlow()

    private val _isAdReady = MutableStateFlow(true)
    val isAdReady: StateFlow<Boolean> = _isAdReady.asStateFlow()

    private val _isInterstitialReady = MutableStateFlow(true)
    val isInterstitialReady: StateFlow<Boolean> = _isInterstitialReady.asStateFlow()

    private val _adStatusMessage = MutableStateFlow("Régie Interne Entreprise : Opérationnelle")
    val adStatusMessage: StateFlow<String> = _adStatusMessage.asStateFlow()

    private val _totalCampaignsCompleted = MutableStateFlow(0)
    val totalCampaignsCompleted: StateFlow<Int> = _totalCampaignsCompleted.asStateFlow()

    fun recordCampaignCompletion(name: String, payout: Double) {
        _totalCampaignsCompleted.value += 1
        _lifecycleState.value = AdLifecycleState.Completed(name, payout)
        _adStatusMessage.value = "Campagne $name diffusée avec succès (+$$payout)"
    }
}
