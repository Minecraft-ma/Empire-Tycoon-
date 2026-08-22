package com.example.model

enum class TechBranch(val displayName: String, val colorHex: Long, val iconEmoji: String) {
    AI_COMPUTING("IA & Algorithmes", 0xFF00E5FF, "🧠"),
    ENERGY_FUSION("Énergie & Fusion", 0xFFFF9100, "⚡"),
    SPACE_MINING("Spatial & Astéroïdes", 0xFFB388FF, "🚀"),
    BIO_NANOTECH("Bio-Nanotech", 0xFF00E676, "🧬")
}

data class ExpandedTechNode(
    val id: String,
    val name: String,
    val branch: TechBranch,
    val tier: Int, // 1 to 3
    val description: String,
    val cost: Double,
    val isUnlocked: Boolean = false,
    val requiresTechId: String? = null,
    val bonusLabel: String,
    val multiplierBoost: Double, // e.g. 0.35 = +35%
    val iconEmoji: String
)

data class RivalBidder(
    val id: String,
    val name: String,
    val title: String,
    val avatar: String,
    val maxBudget: Double
)

data class AuctionLot(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val startingBid: Double,
    val currentBid: Double,
    val highestBidderName: String = "Banque Centrale",
    val isPlayerWinning: Boolean = false,
    val timeRemainingSec: Int = 15,
    val isExpired: Boolean = false,
    val isWonByPlayer: Boolean = false,
    val bonusCashYieldPerSec: Double = 0.0,
    val permanentMultiplier: Double = 0.20,
    val iconEmoji: String = "🏛️",
    val activeRivals: List<RivalBidder> = emptyList()
) {
    val nextMinBid: Double
        get() = (currentBid * 1.15).coerceAtLeast(currentBid + 1_000.0)

    val instantBuyoutPrice: Double
        get() = (startingBid * 2.8).coerceAtLeast(currentBid * 1.8)
}

data class CorporateTakeover(
    val id: String,
    val name: String,
    val industry: String,
    val rivalCeoName: String,
    val rivalCeoAvatar: String,
    val totalEnterpriseValue: Double,
    val ownedStakePercentage: Int = 0, // 0%, 25%, 50%, 75%, 100%
    val baseRevenuePerSec: Double,
    val iconEmoji: String,
    val description: String
) {
    val isFullyAcquired: Boolean
        get() = ownedStakePercentage >= 100

    val currentPassiveIncome: Double
        get() = baseRevenuePerSec * (ownedStakePercentage / 100.0)

    val nextStakeCost: Double
        get() {
            val step = (ownedStakePercentage / 25) + 1
            return (totalEnterpriseValue * 0.25) * Math.pow(1.25, (step - 1).toDouble())
        }
}

data class LuxuryAsset(
    val id: String,
    val name: String,
    val category: String,
    val location: String,
    val description: String,
    val cost: Double,
    val isPurchased: Boolean = false,
    val prestigeScore: Int,
    val passiveIncomeMultiplier: Double, // e.g. 0.15 = +15% passif
    val clickPowerBoostPercent: Double = 0.0,
    val iconEmoji: String
)
