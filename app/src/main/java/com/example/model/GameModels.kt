package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.ui.graphics.vector.ImageVector

enum class BusinessCategory(
    val id: String,
    val title: String,
    val shortName: String,
    val emoji: String,
    val description: String,
    val startingPrice: Double = 10.0
) {
    MAGASINS(
        id = "MAGASINS",
        title = "Magasins & Commerces",
        shortName = "Magasins",
        emoji = "🏬",
        description = "Kiosques, commerces, cafés, supermarchés & luxe",
        startingPrice = 10.0
    ),
    HOUSES(
        id = "HOUSES",
        title = "Maisons & Résidences",
        shortName = "Maisons",
        emoji = "🏡",
        description = "Maisons de banlieue, villas, immeubles & gratte-ciel",
        startingPrice = 185000.0
    ),
    TAXI(
        id = "TAXI",
        title = "Taxi & Transports",
        shortName = "Taxis",
        emoji = "🚕",
        description = "Chauffeurs VTC, flottes de taxis urbains & limousines",
        startingPrice = 350.0
    ),
    LIVRAISON(
        id = "LIVRAISON",
        title = "Delivery & Fret",
        shortName = "Delivery",
        emoji = "🚚",
        description = "Coursiers express, centres de tri et logistique de fret",
        startingPrice = 8000.0
    ),
    BANQUE(
        id = "BANQUE",
        title = "Banque & Finance",
        shortName = "Banques",
        emoji = "🏦",
        description = "Agences de crédit, néo-banques, trading & banque privée",
        startingPrice = 120000.0
    ),
    INDUSTRIE(
        id = "INDUSTRIE",
        title = "Industrie & Usines",
        shortName = "Industrie",
        emoji = "🏭",
        description = "Ateliers, usines robotisées, énergie et gigafactories",
        startingPrice = 150000.0
    ),
    TECH(
        id = "TECH",
        title = "Tech & Startups",
        shortName = "Tech / IA",
        emoji = "💻",
        description = "Studios de jeux, serveurs cloud et laboratoires d'IA",
        startingPrice = 250000.0
    ),
    SPATIAL(
        id = "SPATIAL",
        title = "Spatial & Aérospatial",
        shortName = "Spatial",
        emoji = "🚀",
        description = "Drones cargos, spatioport et constellations orbitales",
        startingPrice = 15000000.0
    )
}

data class Business(
    val id: String,
    val name: String,
    val category: String,
    val categoryGroup: String = "MAGASINS",
    val level: Int = 0,
    val baseCost: Double,
    val baseRevenuePerSec: Double,
    val isUnlocked: Boolean = false,
    val managerHired: Boolean = false,
    val managerName: String = "Automatisé",
    val managerCost: Double,
    val managerAvatar: String = "⚡",
    val description: String,
    val iconType: String = "coffee",
    val cycleTimeSeconds: Float = 1.0f,
    val currentCycleProgress: Float = 0f
) {
    val currentCost: Double
        get() = if (level == 0) baseCost else baseCost * Math.pow(1.18, level.toDouble())

    val revenuePerSecond: Double
        get() {
            if (level == 0 || !isUnlocked) return 0.0
            val speedFactor = 1.0 / cycleTimeSeconds.coerceAtLeast(0.2f)
            val milestoneMultiplier = when {
                level >= 200 -> 8.0
                level >= 100 -> 4.0
                level >= 50 -> 2.5
                level >= 25 -> 1.75
                level >= 10 -> 1.25
                else -> 1.0
            }
            return (baseRevenuePerSec * level * milestoneMultiplier) * speedFactor
        }

    fun getIcon(): ImageVector {
        return when (iconType) {
            "coffee" -> Icons.Default.Coffee
            "store" -> Icons.Default.Storefront
            "fashion" -> Icons.Default.ShoppingBag
            "car" -> Icons.Default.DirectionsCar
            "bank" -> Icons.Default.AccountBalance
            "credit_card" -> Icons.Default.CreditCard
            "crypto" -> Icons.Default.CurrencyBitcoin
            "trading" -> Icons.Default.TrendingUp
            "shipping" -> Icons.Default.LocalShipping
            "factory" -> Icons.Default.Factory
            "industry" -> Icons.Default.PrecisionManufacturing
            "energy" -> Icons.Default.ElectricBolt
            "tech" -> Icons.Default.LaptopMac
            "media" -> Icons.Default.Movie
            "quantum" -> Icons.Default.Memory
            "space", "rocket" -> Icons.Default.RocketLaunch
            "casino" -> Icons.Default.Casino
            else -> Icons.Default.Business
        }
    }
}

enum class MiniGameType {
    GOLD_RUSH_CATCH,
    DEAL_PITCH_SLIDER,
    CRYPTO_FAST_PUMP,
    LUCKY_VIP_SPIN,
    VIRAL_AD_CAMPAIGN
}

enum class SponsorshipTier(val title: String, val badge: String, val colorHex: Long) {
    BRONZE("Partenaire Local", "BRONZE", 0xFFCD7F32),
    SILVER("Sponsor Régional", "SILVER", 0xFFC0C0C0),
    GOLD("Corporation Nationale", "GOLD", 0xFFFFD700),
    PLATINUM("Multinationale Globale", "PLATINUM", 0xFF00E5FF),
    DIAMOND("Conglomérat Spatial", "DIAMOND", 0xFFE040FB)
}

data class SponsorshipContract(
    val id: String,
    val sponsorName: String,
    val sponsorCategory: String,
    val tier: SponsorshipTier,
    val logoEmoji: String,
    val description: String,
    val durationSeconds: Int, // Total duration of the deal
    val timeRemainingSeconds: Int = 0, // Current active countdown
    val requiredNetWorth: Double = 0.0,
    val signingBonus: Double = 0.0,
    val passiveIncomeMultiplier: Double = 0.15, // +15% passive income boost while active
    val directPayoutPerSec: Double = 0.0, // Additional fixed cash per second
    val isSigned: Boolean = false,
    val isCompleted: Boolean = false,
    val totalEarningsAccumulated: Double = 0.0
) {
    val isActive: Boolean
        get() = isSigned && timeRemainingSeconds > 0 && !isCompleted

    val progressFraction: Float
        get() = if (durationSeconds > 0) {
            1f - (timeRemainingSeconds.toFloat() / durationSeconds.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val formattedTimeRemaining: String
        get() {
            val mins = timeRemainingSeconds / 60
            val secs = timeRemainingSeconds % 60
            return String.format("%02d:%02d", mins, secs)
        }
}

data class SponsorOffer(
    val id: String,
    val brandName: String,
    val title: String,
    val description: String,
    val rewardCashFactor: Double, // Multiplied by current net worth or revenue/sec
    val rewardMultiplier: Double = 3.0,
    val multiplierDurationSec: Int = 30,
    val miniGameType: MiniGameType,
    val badge: String = "SPONSOR VIP",
    val tagColorHex: Long = 0xFFFFD700
)

data class StockItem(
    val ticker: String,
    val name: String,
    val category: String,
    val price: Double,
    val previousPrice: Double,
    val history: List<Float> = listOf(),
    val ownedShares: Int = 0,
    val totalInvested: Double = 0.0,
    val volatility: Float = 0.05f,
    val isCrypto: Boolean = false,
    val stakedShares: Int = 0
) {
    val changePercent: Double
        get() = if (previousPrice > 0) ((price - previousPrice) / previousPrice) * 100.0 else 0.0

    val averageBuyPrice: Double
        get() = if (ownedShares > 0) totalInvested / ownedShares else 0.0

    val currentValue: Double
        get() = (ownedShares + stakedShares) * price

    val unrealizedProfitLoss: Double
        get() = if (ownedShares > 0 && totalInvested > 0) currentValue - totalInvested else 0.0

    val unrealizedProfitLossPercent: Double
        get() = if (totalInvested > 0) (unrealizedProfitLoss / totalInvested) * 100.0 else 0.0
}

data class Executive(
    val id: String,
    val name: String,
    val role: String,
    val cost: Double,
    val hired: Boolean = false,
    val perkTitle: String,
    val perkDescription: String,
    val passiveRevenueBoost: Double = 0.0, // e.g. 0.20 = +20%
    val adCpmBoost: Double = 0.0,
    val clickPowerBoost: Double = 0.0,
    val emoji: String = "👩‍💼"
)

data class CrisisEvent(
    val id: String,
    val title: String,
    val description: String,
    val choiceA: String,
    val choiceB: String,
    val timeLimitSec: Int = 10,
    val rewardMultiplier: Double = 2.5,
    val costRatio: Double = 0.10
)

enum class MarketCondition(
    val title: String,
    val opexMultiplier: Double,
    val revenueMultiplier: Double,
    val description: String,
    val emoji: String
) {
    STABLE("Économie Stable", 1.0, 1.0, "Les marchés fonctionnent normalement.", "⚖️"),
    INFLATION("Poussée d'Inflation", 1.6, 1.0, "Hausse des coûts opérationnels et de maintenance (+60%) !", "🔥"),
    RECESSION("Récession Généralisée", 1.25, 0.70, "Baisse du chiffre d'affaires (-30%) et hausse des charges.", "📉"),
    CRASH_IMMOBILIER("Crise Immobilière", 2.2, 0.85, "Explosion des frais de copropriété et taxes foncières (+120%) !", "🏛️"),
    BOOM("Croissance Explosive", 0.85, 1.45, "Expansion économique ! Gains passifs dopés de +45% !", "🚀")
}

data class AdNetworkTier(
    val id: String,
    val name: String,
    val cpmRate: Double,
    val unlockCost: Double,
    val isUnlocked: Boolean = false,
    val description: String,
    val autoAdIncomePerSec: Double = 0.0,
    val level: Int = 1,
    val audienceReach: Long = 1000L,
    val upgradeCost: Double = 250.0,
    val iconEmoji: String = "📡",
    val channelType: String = "DIGITAL",
    val clickBonusMultiplier: Double = 0.05
) {
    val currentRevenuePerSec: Double
        get() {
            if (!isUnlocked) return 0.0
            return autoAdIncomePerSec * level * (1.0 + (level / 10.0))
        }

    val nextUpgradeCost: Double
        get() = upgradeCost * Math.pow(1.35, (level - 1).toDouble())

    val currentReach: Long
        get() = audienceReach * level.toLong()
}

data class FloatingCoin(
    val id: Long,
    val x: Float,
    val y: Float,
    val text: String,
    val isCrit: Boolean = false
)

data class Megaproject(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val stage: Int = 0,
    val maxStage: Int = 5,
    val baseCost: Double,
    val passiveMultiplierBonus: Double, // e.g. 0.25 = +25% per stage
    val iconEmoji: String = "🏗️"
) {
    val currentCost: Double
        get() = baseCost * Math.pow(3.5, stage.toDouble())

    val isMaxed: Boolean
        get() = stage >= maxStage
}

enum class UpgradeCategory {
    CLICK_POWER,
    PASSIVE_BUSINESS,
    ADS_AND_SPONSORS,
    FINANCE_AND_MARKET
}

data class ProductivityUpgrade(
    val id: String,
    val name: String,
    val category: UpgradeCategory,
    val description: String,
    val baseCost: Double,
    val costMultiplier: Double = 1.18,
    val level: Int = 0,
    val maxLevel: Int = 50,
    val multiplierPerLevel: Double, // e.g. 0.10 = +10% per level
    val iconEmoji: String,
    val badgeText: String = "+10%/niv",
    val tagHexColor: Long = 0xFF10B981
) {
    val currentCost: Double
        get() = baseCost * Math.pow(costMultiplier, level.toDouble())

    val totalBonusPercent: Double
        get() = level * multiplierPerLevel * 100.0

    val isMaxed: Boolean
        get() = level >= maxLevel
}

object MoneyFormatter {
    fun formatExact(amount: Double): String {
        if (amount.isNaN() || amount.isInfinite()) return "$ 0.00"
        val symbols = java.text.DecimalFormatSymbols(java.util.Locale.US).apply {
            groupingSeparator = ' '
            decimalSeparator = '.'
        }
        val df = java.text.DecimalFormat("$ #,##0.00", symbols)
        return df.format(amount)
    }

    fun formatCardBalance(amount: Double): String {
        if (amount.isNaN() || amount.isInfinite()) return "$ 0.00"
        return if (amount < 10_000_000.0) {
            formatExact(amount)
        } else {
            format(amount)
        }
    }

    fun format(amount: Double): String {
        if (amount.isNaN() || amount.isInfinite()) return "$0"
        return when {
            amount < 0 -> "-${format(-amount)}"
            amount < 1_000 -> String.format("$%.0f", amount)
            amount < 1_000_000 -> String.format("$%.2fK", amount / 1_000)
            amount < 1_000_000_000 -> String.format("$%.2fM", amount / 1_000_000)
            amount < 1_000_000_000_000 -> String.format("$%.2fB", amount / 1_000_000_000)
            amount < 1_000_000_000_000_000 -> String.format("$%.2fT", amount / 1_000_000_000_000)
            amount < 1e18 -> String.format("$%.2fQa", amount / 1e15)
            amount < 1e21 -> String.format("$%.2fQi", amount / 1e18)
            amount < 1e24 -> String.format("$%.2fSx", amount / 1e21)
            amount < 1e27 -> String.format("$%.2fSp", amount / 1e24)
            amount < 1e30 -> String.format("$%.2fOc", amount / 1e27)
            amount < 1e33 -> String.format("$%.2fNo", amount / 1e30)
            amount < 1e36 -> String.format("$%.2fDc", amount / 1e33)
            else -> String.format("$%.2e", amount)
        }
    }

    fun formatPerSec(amount: Double): String {
        return "${format(amount)}/s"
    }
}
