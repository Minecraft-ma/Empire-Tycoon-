package com.example.model

enum class DailyMissionCategory(val displayName: String, val iconEmoji: String, val colorHex: Long) {
    NEGOTIATION("Négociation", "⚡", 0xFFF59E0B),
    MANAGEMENT("Entreprises", "🏢", 0xFF10B981),
    TRADING("Bourse & Finance", "📊", 0xFF3B82F6),
    MARKETING("Marketing & Pubs", "📺", 0xFF8B5CF6),
    CHALLENGE("Défis & Fortune", "👑", 0xFFEC4899)
}

enum class DailyMissionType {
    EARN_CASH,
    TAP_CONTRACTS,
    UPGRADE_BUSINESSES,
    TRADE_STOCKS,
    PLAY_SPONSOR_MINIGAME,
    RESOLVE_CRISIS,
    WATCH_ADMOB_AD,
    SPIN_WHEEL,
    REACH_COMBO
}

data class DailyMission(
    val id: String,
    val type: DailyMissionType,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val category: DailyMissionCategory,
    val currentProgress: Long,
    val targetProgress: Long,
    val rewardCash: Double,
    val rewardBoostMultiplier: Double = 1.0,
    val rewardBoostDurationSec: Int = 0,
    val rewardLabel: String,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val targetTab: Int = 0 // 0: Action, 1: Empire, 2: Bourse, 3: Pubs, 4: Direction
) {
    val progressFraction: Float
        get() = if (targetProgress > 0L) (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f) else 0f
}

data class DailyMilestoneChest(
    val milestoneTarget: Int,
    val title: String,
    val iconEmoji: String,
    val rewardCash: Double,
    val rewardBoostMultiplier: Double = 1.0,
    val rewardBoostDurationSec: Int = 0,
    val rewardLabel: String,
    val isUnlocked: Boolean = false,
    val isClaimed: Boolean = false
)

data class DailyLoginReward(
    val dayNumber: Int,
    val title: String,
    val rewardLabel: String,
    val iconEmoji: String,
    val cashReward: Double,
    val boostMultiplier: Double = 1.0,
    val boostDurationSec: Int = 0,
    val isClaimed: Boolean = false,
    val isCurrentDay: Boolean = false
)

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardCash: Double,
    val rewardLabel: String,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false
) {
    val progressFraction: Float
        get() = (currentProgress.toFloat() / targetProgress.toFloat()).coerceIn(0f, 1f)
}

data class MarketNewsItem(
    val id: String,
    val headline: String,
    val affectedTicker: String?,
    val priceImpactPercent: Double,
    val emoji: String,
    val timestampFormatted: String
)

data class MissionRewardClaimResult(
    val missionId: String,
    val missionTitle: String,
    val rewardCash: Double,
    val rewardBoostMultiplier: Double = 1.0,
    val rewardBoostDurationSec: Int = 0,
    val message: String,
    val success: Boolean = true
)

data class DailyMissionsUiState(
    val missions: List<DailyMission> = emptyList(),
    val milestoneChests: List<DailyMilestoneChest> = emptyList(),
    val dailyLoginRewards: List<DailyLoginReward> = emptyList(),
    val dailyStreakDays: Int = 1,
    val timeUntilReset: String = "23h 59m 59s",
    val isDialogOpen: Boolean = false,
    val selectedTab: Int = 0, // 0: Missions, 1: 7-day Login
    val totalMissionsCount: Int = 0,
    val completedMissionsCount: Int = 0,
    val claimableMissionsCount: Int = 0,
    val totalClaimableItemsCount: Int = 0,
    val latestRewardClaimed: MissionRewardClaimResult? = null
)

data class TechUpgrade(
    val id: String,
    val name: String,
    val description: String,
    val cost: Double,
    val techCostPoints: Int,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val effectType: TechEffectType
)

enum class TechEffectType {
    PASSIVE_BOOST_25,
    CLICK_CRIT_CHANCE,
    STOCK_DIVIDEND_AUTO,
    SPONSOR_REWARD_DOUBLED,
    OFFLINE_EARNINGS_MAX
}

