package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LeaderboardRepository(
    private val leaderboardDao: LeaderboardDao
) {
    val topScores: Flow<List<LeaderboardScoreEntity>> = leaderboardDao.getPlayerRuns()
    val playerRuns: Flow<List<LeaderboardScoreEntity>> = leaderboardDao.getPlayerRuns()

    suspend fun getRankForScore(score: Double): Int = withContext(Dispatchers.IO) {
        leaderboardDao.getRankForScore(score) + 1
    }

    suspend fun getBestPlayerScore(): LeaderboardScoreEntity? = withContext(Dispatchers.IO) {
        leaderboardDao.getBestPlayerScore()
    }

    suspend fun savePlayerScore(
        playerName: String,
        avatarEmoji: String,
        totalCashEarned: Double,
        peakRevenuePerSec: Double,
        prestigeLevel: Int,
        businessesCount: Int,
        contractsSignedCount: Long,
        notes: String = ""
    ): Long = withContext(Dispatchers.IO) {
        leaderboardDao.deleteNonPlayerScores()
        val entity = LeaderboardScoreEntity(
            playerName = playerName.ifBlank { "Joueur" },
            avatarEmoji = avatarEmoji.ifBlank { "💼" },
            totalCashEarned = totalCashEarned,
            peakRevenuePerSec = peakRevenuePerSec,
            prestigeLevel = prestigeLevel,
            businessesCount = businessesCount,
            contractsSignedCount = contractsSignedCount,
            timestamp = System.currentTimeMillis(),
            notes = notes,
            isPlayerRun = true
        )
        leaderboardDao.insertScore(entity)
    }

    suspend fun deleteScore(id: Long) = withContext(Dispatchers.IO) {
        leaderboardDao.deleteScoreById(id)
    }

    suspend fun clearPlayerScores() = withContext(Dispatchers.IO) {
        leaderboardDao.clearPlayerScores()
    }

    suspend fun resetToDefaultHistoricalScores() = withContext(Dispatchers.IO) {
        leaderboardDao.clearAll()
    }

    suspend fun seedInitialHistoricalScoresIfEmpty() = withContext(Dispatchers.IO) {
        // Clean out any legacy non-player bot scores - only real player scores are kept
        leaderboardDao.deleteNonPlayerScores()
    }
}
