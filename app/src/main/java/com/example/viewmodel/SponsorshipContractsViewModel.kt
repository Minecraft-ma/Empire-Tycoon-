package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.model.MoneyFormatter
import com.example.model.SponsorshipContract
import com.example.model.SponsorshipTier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max

sealed class ContractEvent {
    data class DealSigned(val contract: SponsorshipContract, val signingBonus: Double) : ContractEvent()
    data class DealCompleted(val contract: SponsorshipContract, val totalPayout: Double) : ContractEvent()
    data class PayoutTick(val payoutPerSec: Double) : ContractEvent()
    data class Error(val message: String) : ContractEvent()
}

data class SponsorshipContractsUiState(
    val contracts: List<SponsorshipContract> = GameRepository.getDefaultSponsorshipContracts(),
    val selectedTierFilter: SponsorshipTier? = null,
    val isDealNegotiationDialogOpen: Boolean = false,
    val selectedDealId: String? = null,
    val totalActiveContractsCount: Int = 0,
    val totalPassiveBonusPercent: Double = 0.0,
    val totalDirectIncomePerSec: Double = 0.0,
    val totalLifetimeSponsorshipEarnings: Double = 0.0
)

class SponsorshipContractsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SponsorshipContractsUiState())
    val uiState: StateFlow<SponsorshipContractsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ContractEvent>()
    val events: SharedFlow<ContractEvent> = _events.asSharedFlow()

    init {
        startContractTickTimer()
    }

    private fun startContractTickTimer() {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                tickActiveContracts(1)
            }
        }
    }

    /**
     * Ticks active contracts down by [elapsedSeconds] and updates accumulated stats.
     */
    fun tickActiveContracts(elapsedSeconds: Int = 1) {
        val current = _uiState.value
        var hasChanges = false
        var directPayoutThisTick = 0.0

        val updatedContracts = current.contracts.map { contract ->
            if (contract.isActive) {
                hasChanges = true
                val newTimeRemaining = max(0, contract.timeRemainingSeconds - elapsedSeconds)
                val earnedThisSec = contract.directPayoutPerSec * elapsedSeconds
                directPayoutThisTick += earnedThisSec

                if (newTimeRemaining == 0) {
                    // Deal term has expired / completed successfully
                    val finalContract = contract.copy(
                        timeRemainingSeconds = 0,
                        isCompleted = true,
                        totalEarningsAccumulated = contract.totalEarningsAccumulated + earnedThisSec
                    )
                    viewModelScope.launch {
                        _events.emit(
                            ContractEvent.DealCompleted(
                                finalContract,
                                finalContract.totalEarningsAccumulated
                            )
                        )
                    }
                    finalContract
                } else {
                    contract.copy(
                        timeRemainingSeconds = newTimeRemaining,
                        totalEarningsAccumulated = contract.totalEarningsAccumulated + earnedThisSec
                    )
                }
            } else {
                contract
            }
        }

        if (hasChanges) {
            val activeList = updatedContracts.filter { it.isActive }
            val activeBonusMultiplier = activeList.sumOf { it.passiveIncomeMultiplier }
            val activeDirectIncomeSec = activeList.sumOf { it.directPayoutPerSec }
            val lifetimeEarnings = updatedContracts.sumOf { it.totalEarningsAccumulated + (if (it.isSigned) it.signingBonus else 0.0) }

            _uiState.update {
                it.copy(
                    contracts = updatedContracts,
                    totalActiveContractsCount = activeList.size,
                    totalPassiveBonusPercent = activeBonusMultiplier * 100.0,
                    totalDirectIncomePerSec = activeDirectIncomeSec,
                    totalLifetimeSponsorshipEarnings = lifetimeEarnings
                )
            }

            if (directPayoutThisTick > 0) {
                viewModelScope.launch {
                    _events.emit(ContractEvent.PayoutTick(directPayoutThisTick))
                }
            }
        }
    }

    /**
     * Signs a sponsorship deal. Verifies prerequisites and initializes duration & payouts.
     */
    fun signContract(
        contractId: String,
        currentNetWorth: Double,
        onBonusCredited: (bonusAmount: Double) -> Unit
    ): Boolean {
        val state = _uiState.value
        val contract = state.contracts.find { it.id == contractId }

        if (contract == null) {
            viewModelScope.launch {
                _events.emit(ContractEvent.Error("Contrat introuvable."))
            }
            return false
        }

        if (contract.isActive) {
            viewModelScope.launch {
                _events.emit(ContractEvent.Error("Ce contrat de sponsoring est déjà actif !"))
            }
            return false
        }

        if (currentNetWorth < contract.requiredNetWorth) {
            val reqFormatted = MoneyFormatter.format(contract.requiredNetWorth)
            viewModelScope.launch {
                _events.emit(ContractEvent.Error("Valeur nette insuffisante : $reqFormatted requis pour négocier ce partenariat !"))
            }
            return false
        }

        val updated = state.contracts.map {
            if (it.id == contractId) {
                it.copy(
                    isSigned = true,
                    isCompleted = false,
                    timeRemainingSeconds = it.durationSeconds
                )
            } else it
        }

        val activeList = updated.filter { it.isActive }
        val activeBonusMultiplier = activeList.sumOf { it.passiveIncomeMultiplier }
        val activeDirectIncomeSec = activeList.sumOf { it.directPayoutPerSec }
        val lifetimeEarnings = updated.sumOf { it.totalEarningsAccumulated + (if (it.isSigned) it.signingBonus else 0.0) }

        _uiState.update {
            it.copy(
                contracts = updated,
                totalActiveContractsCount = activeList.size,
                totalPassiveBonusPercent = activeBonusMultiplier * 100.0,
                totalDirectIncomePerSec = activeDirectIncomeSec,
                totalLifetimeSponsorshipEarnings = lifetimeEarnings
            )
        }

        onBonusCredited(contract.signingBonus)

        viewModelScope.launch {
            _events.emit(ContractEvent.DealSigned(contract, contract.signingBonus))
        }

        return true
    }

    /**
     * Renews an expired contract or re-signs it.
     */
    fun renewContract(
        contractId: String,
        currentNetWorth: Double,
        onBonusCredited: (bonusAmount: Double) -> Unit
    ): Boolean {
        val state = _uiState.value
        val contract = state.contracts.find { it.id == contractId } ?: return false

        if (contract.isActive) return false

        val updated = state.contracts.map {
            if (it.id == contractId) {
                it.copy(
                    isSigned = true,
                    isCompleted = false,
                    timeRemainingSeconds = it.durationSeconds
                )
            } else it
        }

        val activeList = updated.filter { it.isActive }
        val activeBonusMultiplier = activeList.sumOf { it.passiveIncomeMultiplier }
        val activeDirectIncomeSec = activeList.sumOf { it.directPayoutPerSec }
        val lifetimeEarnings = updated.sumOf { it.totalEarningsAccumulated + (if (it.isSigned) it.signingBonus else 0.0) }

        _uiState.update {
            it.copy(
                contracts = updated,
                totalActiveContractsCount = activeList.size,
                totalPassiveBonusPercent = activeBonusMultiplier * 100.0,
                totalDirectIncomePerSec = activeDirectIncomeSec,
                totalLifetimeSponsorshipEarnings = lifetimeEarnings
            )
        }

        onBonusCredited(contract.signingBonus)

        viewModelScope.launch {
            _events.emit(ContractEvent.DealSigned(contract, contract.signingBonus))
        }

        return true
    }

    fun setTierFilter(tier: SponsorshipTier?) {
        _uiState.update { it.copy(selectedTierFilter = tier) }
    }

    fun openDealDialog(contractId: String) {
        _uiState.update {
            it.copy(
                isDealNegotiationDialogOpen = true,
                selectedDealId = contractId
            )
        }
    }

    fun closeDealDialog() {
        _uiState.update {
            it.copy(
                isDealNegotiationDialogOpen = false,
                selectedDealId = null
            )
        }
    }

    fun loadSavedContractStates(savedData: Map<String, Triple<Boolean, Int, Double>>) {
        if (savedData.isEmpty()) return
        val current = _uiState.value.contracts
        val updated = current.map { contract ->
            val saved = savedData[contract.id]
            if (saved != null) {
                val (isSigned, timeRemaining, totalEarned) = saved
                contract.copy(
                    isSigned = isSigned,
                    timeRemainingSeconds = timeRemaining,
                    isCompleted = isSigned && timeRemaining <= 0,
                    totalEarningsAccumulated = totalEarned
                )
            } else contract
        }

        val activeList = updated.filter { it.isActive }
        val activeBonusMultiplier = activeList.sumOf { it.passiveIncomeMultiplier }
        val activeDirectIncomeSec = activeList.sumOf { it.directPayoutPerSec }
        val lifetimeEarnings = updated.sumOf { it.totalEarningsAccumulated + (if (it.isSigned) it.signingBonus else 0.0) }

        _uiState.update {
            it.copy(
                contracts = updated,
                totalActiveContractsCount = activeList.size,
                totalPassiveBonusPercent = activeBonusMultiplier * 100.0,
                totalDirectIncomePerSec = activeDirectIncomeSec,
                totalLifetimeSponsorshipEarnings = lifetimeEarnings
            )
        }
    }
}
