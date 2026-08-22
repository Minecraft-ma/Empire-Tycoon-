package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LeaderboardRepository(
    private val leaderboardDao: LeaderboardDao
) {
    val topScores: Flow<List<LeaderboardScoreEntity>> = leaderboardDao.getTopScores()
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
        seedInitialHistoricalScoresIfEmpty()
        val entity = LeaderboardScoreEntity(
            playerName = playerName.ifBlank { "CEO Anonyme" },
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
        seedInitialHistoricalScoresIfEmpty()
    }

    suspend fun seedInitialHistoricalScoresIfEmpty() = withContext(Dispatchers.IO) {
        val count = leaderboardDao.getScoresCount()
        if (count == 0) {
            val historicalLegends = listOf(
                LeaderboardScoreEntity(
                    playerName = "John D. Rockefeller",
                    avatarEmoji = "🛢️",
                    totalCashEarned = 500_000_000_000.0,
                    peakRevenuePerSec = 4_500_000_000.0,
                    prestigeLevel = 10,
                    businessesCount = 10,
                    contractsSignedCount = 12500L,
                    timestamp = System.currentTimeMillis() - (86400000L * 30),
                    notes = "Fondateur de Standard Oil & Monopole de l'Énergie",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "Elon M. Space Lord",
                    avatarEmoji = "🚀",
                    totalCashEarned = 280_000_000_000.0,
                    peakRevenuePerSec = 2_800_000_000.0,
                    prestigeLevel = 8,
                    businessesCount = 10,
                    contractsSignedCount = 9800L,
                    timestamp = System.currentTimeMillis() - (86400000L * 22),
                    notes = "Constellation OrbitX & Singularity AI",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "Warren B. Value King",
                    avatarEmoji = "💎",
                    totalCashEarned = 140_000_000_000.0,
                    peakRevenuePerSec = 1_200_000_000.0,
                    prestigeLevel = 6,
                    businessesCount = 9,
                    contractsSignedCount = 7400L,
                    timestamp = System.currentTimeMillis() - (86400000L * 15),
                    notes = "Holding d'Investissement & Intérêts Composés",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "Madame C.J. Walker",
                    avatarEmoji = "👑",
                    totalCashEarned = 25_000_000_000.0,
                    peakRevenuePerSec = 350_000_000.0,
                    prestigeLevel = 4,
                    businessesCount = 8,
                    contractsSignedCount = 5200L,
                    timestamp = System.currentTimeMillis() - (86400000L * 10),
                    notes = "Empire Cosmétique & Réseau de Distribution Mondial",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "Cornelius Vanderbilt",
                    avatarEmoji = "🚂",
                    totalCashEarned = 5_000_000_000.0,
                    peakRevenuePerSec = 75_000_000.0,
                    prestigeLevel = 3,
                    businessesCount = 7,
                    contractsSignedCount = 3800L,
                    timestamp = System.currentTimeMillis() - (86400000L * 7),
                    notes = "Réseau Ferroviaire & Flottes Commerciales",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "Steve J. Neo Design",
                    avatarEmoji = "🍏",
                    totalCashEarned = 850_000_000.0,
                    peakRevenuePerSec = 18_000_000.0,
                    prestigeLevel = 2,
                    businessesCount = 6,
                    contractsSignedCount = 2900L,
                    timestamp = System.currentTimeMillis() - (86400000L * 5),
                    notes = "Révolution Informatique & Design Épuré",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "Alexandre Dupont (Alpha Run)",
                    avatarEmoji = "💼",
                    totalCashEarned = 120_000_000.0,
                    peakRevenuePerSec = 3_200_000.0,
                    prestigeLevel = 1,
                    businessesCount = 5,
                    contractsSignedCount = 1800L,
                    timestamp = System.currentTimeMillis() - (86400000L * 3),
                    notes = "Startup de livraison & Fintech NeoBank",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "Sofia Chen (Seed Founder)",
                    avatarEmoji = "☕",
                    totalCashEarned = 15_000_000.0,
                    peakRevenuePerSec = 450_000.0,
                    prestigeLevel = 0,
                    businessesCount = 4,
                    contractsSignedCount = 950L,
                    timestamp = System.currentTimeMillis() - (86400000L * 1),
                    notes = "Chaîne de Coffee Shops & Agence IA",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "Lucas Martin (Rookie Tycoon)",
                    avatarEmoji = "⚡",
                    totalCashEarned = 1_500_000.0,
                    peakRevenuePerSec = 65_000.0,
                    prestigeLevel = 0,
                    businessesCount = 3,
                    contractsSignedCount = 420L,
                    timestamp = System.currentTimeMillis() - 3600000L * 6,
                    notes = "Premiers contrats boursiers & studio de mode",
                    isPlayerRun = false
                )
            )
            leaderboardDao.insertScores(historicalLegends)
        }
    }
}
