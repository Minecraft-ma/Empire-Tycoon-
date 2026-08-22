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
    val description: String
) {
    MAGASINS(
        id = "MAGASINS",
        title = "Magasins & Commerce",
        shortName = "Magasins",
        emoji = "🏪",
        description = "Boutiques, franchises de restauration, retail et distribution"
    ),
    BANQUE(
        id = "BANQUE",
        title = "Banque & Finance",
        shortName = "Banques",
        emoji = "🏦",
        description = "Agences de crédit, néo-banques, fonds et trading quant"
    ),
    INDUSTRIE(
        id = "INDUSTRIE",
        title = "Industrie & Logistique",
        shortName = "Industrie",
        emoji = "🏭",
        description = "Flotte de fret, complexes robotisés, énergie et gigafactories"
    ),
    TECH(
        id = "TECH",
        title = "Tech & Médias",
        shortName = "Tech",
        emoji = "🚀",
        description = "Studios IA, plateformes de streaming et centres cloud"
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
    val managerName: String,
    val managerCost: Double,
    val managerAvatar: String = "👤",
    val description: String,
    val iconType: String = "coffee",
    val cycleTimeSeconds: Float = 1.0f,
    val currentCycleProgress: Float = 0f
) {
    val currentCost: Double
        get() = if (level == 0) baseCost else baseCost * Math.pow(1.15, level.toDouble())

    val revenuePerSecond: Double
        get() {
            if (level == 0 || !isUnlocked) return 0.0
            val speedFactor = 1.0 / cycleTimeSeconds.coerceAtLeast(0.1f)
            val milestoneMultiplier = when {
                level >= 200 -> 16.0
                level >= 100 -> 8.0
                level >= 50 -> 4.0
                level >= 25 -> 2.0
                level >= 10 -> 1.5
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
            "space" -> Icons.Default.RocketLaunch
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
    val volatility: Float = 0.05f
) {
    val changePercent: Double
        get() = if (previousPrice > 0) ((price - previousPrice) / previousPrice) * 100.0 else 0.0
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

data class AdNetworkTier(
    val id: String,
    val name: String,
    val cpmRate: Double,
    val unlockCost: Double,
    val isUnlocked: Boolean = false,
    val description: String,
    val autoAdIncomePerSec: Double = 0.0
)

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
