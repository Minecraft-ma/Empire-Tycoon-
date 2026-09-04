package com.example.data.online

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OnlinePlayerScore(
    val playerId: String = "",
    val playerName: String = "Joueur",
    val countryFlag: String = "🇫🇷",
    val avatarEmoji: String = "💼",
    val netWorth: Double = 0.0,
    val totalCashEarned: Double = 0.0,
    val peakRevenuePerSec: Double = 0.0,
    val prestigeLevel: Int = 0,
    val businessesCount: Int = 1,
    val propertiesCount: Int = 0,
    val contractsSignedCount: Long = 0L,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val playerTitle: String = "Magnat de l'Empire",
    val isVerifiedUser: Boolean = true
)
