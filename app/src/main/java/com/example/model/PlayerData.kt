package com.example.model

enum class AchievementRewardType {
    CASH,
    TEMPORARY_BOOST,
    PERMANENT_BONUS
}

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val rewardType: AchievementRewardType,
    val rewardValue: Double,
    val rewardLabel: String,
    val targetValue: Long,
    val currentValue: Long = 0L,
    val isUnlocked: Boolean = false,
    val isClaimed: Boolean = false
) {
    val progressFraction: Float
        get() = (currentValue.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f)
}

data class PlayerAvatar(
    val id: Int,
    val emoji: String,
    val title: String,
    val unlockedByPrestige: Int = 0
)

data class PlayerRank(
    val title: String,
    val subtitle: String,
    val badgeEmoji: String,
    val minNetWorth: Double,
    val nextRankThreshold: Double
) {
    companion object {
        val RANKS = listOf(
            PlayerRank(
                title = "Stagiaire Sans Le Sou",
                subtitle = "Fais tes premières armes avec le stand de café",
                badgeEmoji = "☕",
                minNetWorth = 0.0,
                nextRankThreshold = 1_000.0
            ),
            PlayerRank(
                title = "Jeune Entrepreneur",
                subtitle = "Tes premiers bénéfices commencent à affluer",
                badgeEmoji = "💼",
                minNetWorth = 1_000.0,
                nextRankThreshold = 25_000.0
            ),
            PlayerRank(
                title = "Fondateur de Startup",
                subtitle = "Ton empire technologique prend son envol",
                badgeEmoji = "💻",
                minNetWorth = 25_000.0,
                nextRankThreshold = 250_000.0
            ),
            PlayerRank(
                title = "PDG & Investisseur Émergent",
                subtitle = "Présent sur les marchés boursiers mondiaux",
                badgeEmoji = "📈",
                minNetWorth = 250_000.0,
                nextRankThreshold = 2_000_000.0
            ),
            PlayerRank(
                title = "Multi-Millionnaire",
                subtitle = "Un empire solide générant des millions passifs",
                badgeEmoji = "💎",
                minNetWorth = 2_000_000.0,
                nextRankThreshold = 50_000_000.0
            ),
            PlayerRank(
                title = "Magnat de la Haute Finance",
                subtitle = "Le conseil d'administration est sous tes ordres",
                badgeEmoji = "🏦",
                minNetWorth = 50_000_000.0,
                nextRankThreshold = 1_000_000_000.0
            ),
            PlayerRank(
                title = "Milliardaire de l'Olympe",
                subtitle = "Une des plus grandes fortunes de la planète",
                badgeEmoji = "👑",
                minNetWorth = 1_000_000_000.0,
                nextRankThreshold = 100_000_000_000.0
            ),
            PlayerRank(
                title = "Empereur Galactique & Trillionnaire",
                subtitle = "Maître absolu de l'économie interstellaire",
                badgeEmoji = "🪐",
                minNetWorth = 100_000_000_000.0,
                nextRankThreshold = Double.MAX_VALUE
            )
        )

        fun getRankForNetWorth(netWorth: Double): PlayerRank {
            return RANKS.lastOrNull { netWorth >= it.minNetWorth } ?: RANKS.first()
        }
    }
}

data class CareerStats(
    val totalTaps: Long = 0,
    val highestCombo: Int = 0,
    val totalCrisesResolved: Int = 0,
    val totalMiniGamesWon: Int = 0,
    val totalStockTrades: Int = 0,
    val totalAdRevenueEarned: Double = 0.0,
    val totalPrestigeResets: Int = 0,
    val totalPlayTimeSeconds: Long = 0,
    val lastSavedTimestamp: Long = System.currentTimeMillis()
)

data class OfflineEarningsReport(
    val offlineSeconds: Long,
    val earnedCash: Double,
    val formattedTime: String
)
