package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.model.DailyLoginReward
import com.example.model.DailyMilestoneChest
import com.example.model.DailyMission
import com.example.model.DailyMissionType
import com.example.model.DailyMissionsUiState
import com.example.model.MissionRewardClaimResult
import com.example.model.MoneyFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Dedicated ViewModel to manage and track the full lifecycle of Daily Missions,
 * player progression (e.g., 'Earn 1000 coins', 'Click 50 times'), Milestone Chests,
 * and the distribution of rewards.
 */
class DailyMissionsViewModel(
    initialNetWorth: Double = 1000.0,
    initialPrestigeLevel: Int = 0
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DailyMissionsUiState(
            missions = GameRepository.generateDailyMissions(initialNetWorth, initialPrestigeLevel),
            milestoneChests = GameRepository.getDefaultMilestoneChests(initialPrestigeLevel),
            dailyLoginRewards = GameRepository.getDefaultDailyRewards(),
            dailyStreakDays = 1,
            timeUntilReset = calculateTimeUntilMidnight()
        )
    )
    val uiState: StateFlow<DailyMissionsUiState> = _uiState.asStateFlow()

    private var currentNetWorth: Double = initialNetWorth
    private var currentPrestigeLevel: Int = initialPrestigeLevel
    private var lastDayEpoch: Long = getCurrentDayEpoch()

    init {
        updateCounts()
        startDailyResetTicker()
    }

    /**
     * Updates player context (Net worth & Prestige) used to scale dynamically generated missions.
     */
    fun updatePlayerContext(netWorth: Double, prestigeLevel: Int) {
        this.currentNetWorth = netWorth
        this.currentPrestigeLevel = prestigeLevel
    }

    /**
     * Generic progress tracker for any mission type.
     */
    fun trackProgress(type: DailyMissionType, amount: Long = 1L) {
        if (amount <= 0L) return

        _uiState.update { currentState ->
            val updatedMissions = currentState.missions.map { mission ->
                if (mission.type == type && !mission.isCompleted) {
                    val newProgress = (mission.currentProgress + amount).coerceAtMost(mission.targetProgress)
                    val isNowCompleted = newProgress >= mission.targetProgress
                    mission.copy(
                        currentProgress = newProgress,
                        isCompleted = isNowCompleted
                    )
                } else {
                    mission
                }
            }

            val totalCompleted = updatedMissions.count { it.isCompleted || it.isClaimed }

            // Unlock milestone chests if enough missions are completed
            val updatedChests = currentState.milestoneChests.map { chest ->
                if (totalCompleted >= chest.milestoneTarget && !chest.isUnlocked) {
                    chest.copy(isUnlocked = true)
                } else {
                    chest
                }
            }

            currentState.copy(
                missions = updatedMissions,
                milestoneChests = updatedChests
            )
        }
        updateCounts()
    }

    /**
     * Tracks coins earned by the player (e.g., 'Earn 1000 coins').
     */
    fun trackEarnCoins(coins: Double) {
        if (coins > 0.0) {
            trackProgress(DailyMissionType.EARN_CASH, coins.toLong().coerceAtLeast(1L))
        }
    }

    /**
     * Tracks player clicks / taps (e.g., 'Click 50 times').
     */
    fun trackClicks(count: Long = 1L) {
        trackProgress(DailyMissionType.TAP_CONTRACTS, count)
    }

    /**
     * Tracks business upgrades.
     */
    fun trackBusinessUpgrade(levels: Long = 1L) {
        trackProgress(DailyMissionType.UPGRADE_BUSINESSES, levels)
    }

    /**
     * Tracks stock trades.
     */
    fun trackStockTrade(trades: Long = 1L) {
        trackProgress(DailyMissionType.TRADE_STOCKS, trades)
    }

    /**
     * Tracks sponsor mini-game played.
     */
    fun trackMiniGamePlayed(count: Long = 1L) {
        trackProgress(DailyMissionType.PLAY_SPONSOR_MINIGAME, count)
    }

    /**
     * Tracks crisis events successfully resolved.
     */
    fun trackCrisisResolved(count: Long = 1L) {
        trackProgress(DailyMissionType.RESOLVE_CRISIS, count)
    }

    /**
     * Tracks AdMob reward ads watched.
     */
    fun trackAdWatched(count: Long = 1L) {
        trackProgress(DailyMissionType.WATCH_ADMOB_AD, count)
    }

    /**
     * Tracks fortune wheel spins.
     */
    fun trackWheelSpin(count: Long = 1L) {
        trackProgress(DailyMissionType.SPIN_WHEEL, count)
    }

    /**
     * Tracks combo streak achievement.
     */
    fun trackComboStreak(streak: Int) {
        _uiState.update { currentState ->
            val updatedMissions = currentState.missions.map { mission ->
                if (mission.type == DailyMissionType.REACH_COMBO && !mission.isCompleted) {
                    val newProgress = maxOf(mission.currentProgress, streak.toLong()).coerceAtMost(mission.targetProgress)
                    val isNowCompleted = newProgress >= mission.targetProgress
                    mission.copy(
                        currentProgress = newProgress,
                        isCompleted = isNowCompleted
                    )
                } else {
                    mission
                }
            }
            currentState.copy(missions = updatedMissions)
        }
        updateCounts()
    }

    /**
     * Claims the reward for a completed daily mission and triggers callback for reward application.
     */
    fun claimMissionReward(
        missionId: String,
        onRewardGranted: ((cash: Double, boostMultiplier: Double, boostDurationSec: Int) -> Unit)? = null
    ): MissionRewardClaimResult? {
        var result: MissionRewardClaimResult? = null

        _uiState.update { currentState ->
            val targetMission = currentState.missions.find { it.id == missionId }
            if (targetMission != null && targetMission.isCompleted && !targetMission.isClaimed) {
                result = MissionRewardClaimResult(
                    missionId = targetMission.id,
                    missionTitle = targetMission.title,
                    rewardCash = targetMission.rewardCash,
                    rewardBoostMultiplier = targetMission.rewardBoostMultiplier,
                    rewardBoostDurationSec = targetMission.rewardBoostDurationSec,
                    message = "Mission accomplie : ${targetMission.title} ! +${MoneyFormatter.format(targetMission.rewardCash)} encaissés !",
                    success = true
                )

                val updatedMissions = currentState.missions.map {
                    if (it.id == missionId) it.copy(isClaimed = true) else it
                }

                currentState.copy(
                    missions = updatedMissions,
                    latestRewardClaimed = result
                )
            } else {
                currentState
            }
        }

        result?.let {
            onRewardGranted?.invoke(it.rewardCash, it.rewardBoostMultiplier, it.rewardBoostDurationSec)
            updateCounts()
        }

        return result
    }

    /**
     * Claims a milestone chest reward.
     */
    fun claimMilestoneChest(
        milestoneTarget: Int,
        onRewardGranted: ((cash: Double, boostMultiplier: Double, boostDurationSec: Int) -> Unit)? = null
    ): MissionRewardClaimResult? {
        var result: MissionRewardClaimResult? = null

        _uiState.update { currentState ->
            val targetChest = currentState.milestoneChests.find { it.milestoneTarget == milestoneTarget }
            if (targetChest != null && targetChest.isUnlocked && !targetChest.isClaimed) {
                result = MissionRewardClaimResult(
                    missionId = "chest_$milestoneTarget",
                    missionTitle = targetChest.title,
                    rewardCash = targetChest.rewardCash,
                    rewardBoostMultiplier = targetChest.rewardBoostMultiplier,
                    rewardBoostDurationSec = targetChest.rewardBoostDurationSec,
                    message = "${targetChest.iconEmoji} ${targetChest.title} ouvert ! +${MoneyFormatter.format(targetChest.rewardCash)}",
                    success = true
                )

                val updatedChests = currentState.milestoneChests.map {
                    if (it.milestoneTarget == milestoneTarget) it.copy(isClaimed = true) else it
                }

                currentState.copy(
                    milestoneChests = updatedChests,
                    latestRewardClaimed = result
                )
            } else {
                currentState
            }
        }

        result?.let {
            onRewardGranted?.invoke(it.rewardCash, it.rewardBoostMultiplier, it.rewardBoostDurationSec)
            updateCounts()
        }

        return result
    }

    /**
     * Claims a 7-day login reward item.
     */
    fun claimDailyLoginReward(
        dayNumber: Int,
        onRewardGranted: ((cash: Double, boostMultiplier: Double, boostDurationSec: Int) -> Unit)? = null
    ): MissionRewardClaimResult? {
        var result: MissionRewardClaimResult? = null

        _uiState.update { currentState ->
            val reward = currentState.dailyLoginRewards.find { it.dayNumber == dayNumber }
            if (reward != null && reward.isCurrentDay && !reward.isClaimed) {
                result = MissionRewardClaimResult(
                    missionId = "daily_login_$dayNumber",
                    missionTitle = reward.title,
                    rewardCash = reward.cashReward,
                    rewardBoostMultiplier = reward.boostMultiplier,
                    rewardBoostDurationSec = reward.boostDurationSec,
                    message = "${reward.iconEmoji} Récolte J$dayNumber : ${reward.title} (+${MoneyFormatter.format(reward.cashReward)}) !",
                    success = true
                )

                val updatedRewards = currentState.dailyLoginRewards.map {
                    if (it.dayNumber == dayNumber) it.copy(isClaimed = true) else it
                }

                currentState.copy(
                    dailyLoginRewards = updatedRewards,
                    latestRewardClaimed = result
                )
            } else {
                currentState
            }
        }

        result?.let {
            onRewardGranted?.invoke(it.rewardCash, it.rewardBoostMultiplier, it.rewardBoostDurationSec)
            updateCounts()
        }

        return result
    }

    /**
     * Dialog visibility and tab controls.
     */
    fun openDialog(tabIndex: Int = 0) {
        _uiState.update { it.copy(isDialogOpen = true, selectedTab = tabIndex) }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isDialogOpen = false) }
    }

    fun setSelectedTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    /**
     * Resets missions and generates a fresh daily set.
     */
    fun generateFreshDailyMissions() {
        val newMissions = GameRepository.generateDailyMissions(currentNetWorth, currentPrestigeLevel)
        val newChests = GameRepository.getDefaultMilestoneChests(currentPrestigeLevel)

        _uiState.update { currentState ->
            currentState.copy(
                missions = newMissions,
                milestoneChests = newChests,
                dailyStreakDays = currentState.dailyStreakDays + 1
            )
        }
        updateCounts()
    }

    /**
     * Recalculates badge and counter totals for the UI.
     */
    private fun updateCounts() {
        _uiState.update { state ->
            val total = state.missions.size
            val completed = state.missions.count { it.isCompleted || it.isClaimed }
            val claimableMissions = state.missions.count { it.isCompleted && !it.isClaimed }
            val claimableChests = state.milestoneChests.count { it.isUnlocked && !it.isClaimed }
            val claimableLogin = state.dailyLoginRewards.count { it.isCurrentDay && !it.isClaimed }

            state.copy(
                totalMissionsCount = total,
                completedMissionsCount = completed,
                claimableMissionsCount = claimableMissions,
                totalClaimableItemsCount = claimableMissions + claimableChests + claimableLogin
            )
        }
    }

    /**
     * Background ticker that checks for midnight resets and updates formatted countdown timer.
     */
    private fun startDailyResetTicker() {
        viewModelScope.launch {
            while (true) {
                val formattedTime = calculateTimeUntilMidnight()
                val currentDay = getCurrentDayEpoch()

                if (currentDay > lastDayEpoch) {
                    lastDayEpoch = currentDay
                    generateFreshDailyMissions()
                }

                _uiState.update { it.copy(timeUntilReset = formattedTime) }
                delay(1000L)
            }
        }
    }

    private fun getCurrentDayEpoch(): Long {
        return System.currentTimeMillis() / (1000L * 60L * 60L * 24L)
    }

    private fun calculateTimeUntilMidnight(): String {
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffMs = maxOf(0L, midnight.timeInMillis - now.timeInMillis)
        val hours = diffMs / (1000 * 60 * 60)
        val minutes = (diffMs % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (diffMs % (1000 * 60)) / 1000
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds)
    }
}
