package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard_scores")
data class LeaderboardScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val playerName: String,
    val avatarEmoji: String,
    val totalCashEarned: Double,
    val peakRevenuePerSec: Double = 0.0,
    val prestigeLevel: Int = 0,
    val businessesCount: Int = 1,
    val contractsSignedCount: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isPlayerRun: Boolean = true
)
