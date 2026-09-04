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
            val liveGlobalTycoons = listOf(
                // Tier Trillionaires / Top Global Legends
                LeaderboardScoreEntity(
                    playerName = "🇺🇸 Elon M. Space Lord",
                    avatarEmoji = "🚀",
                    totalCashEarned = 340_000_000_000.0,
                    peakRevenuePerSec = 4_200_000_000.0,
                    prestigeLevel = 10,
                    businessesCount = 10,
                    contractsSignedCount = 14500L,
                    timestamp = System.currentTimeMillis() - (86400000L * 2),
                    notes = "Constellation OrbitX & Singularity AI • 🟢 En ligne (Session Bourse)",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇫🇷 Bernard A. Luxe Dynasty",
                    avatarEmoji = "👑",
                    totalCashEarned = 230_000_000_000.0,
                    peakRevenuePerSec = 2_900_000_000.0,
                    prestigeLevel = 9,
                    businessesCount = 10,
                    contractsSignedCount = 11200L,
                    timestamp = System.currentTimeMillis() - (86400000L * 1),
                    notes = "Monopole de Haute Couture & Joaillerie • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇦🇪 Sheikh Rashid Capital",
                    avatarEmoji = "💎",
                    totalCashEarned = 160_000_000_000.0,
                    peakRevenuePerSec = 1_800_000_000.0,
                    prestigeLevel = 8,
                    businessesCount = 10,
                    contractsSignedCount = 9800L,
                    timestamp = System.currentTimeMillis() - 3600000L * 2,
                    notes = "Burj Holding & Énergies Renouvelables • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇯🇵 Satoshi & Tokyo Venture",
                    avatarEmoji = "⚡",
                    totalCashEarned = 85_000_000_000.0,
                    peakRevenuePerSec = 950_000_000.0,
                    prestigeLevel = 7,
                    businessesCount = 9,
                    contractsSignedCount = 8400L,
                    timestamp = System.currentTimeMillis() - 3600000L * 5,
                    notes = "Réseau Blockchain & Robots Industriels • 🟢 En minage",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇬🇧 Crown Global Fintech",
                    avatarEmoji = "🏦",
                    totalCashEarned = 35_000_000_000.0,
                    peakRevenuePerSec = 420_000_000.0,
                    prestigeLevel = 6,
                    businessesCount = 9,
                    contractsSignedCount = 6700L,
                    timestamp = System.currentTimeMillis() - 3600000L * 8,
                    notes = "Fonds d'investissement & Neo-banques • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇨🇭 Zurich Private Vault",
                    avatarEmoji = "🛡️",
                    totalCashEarned = 12_000_000_000.0,
                    peakRevenuePerSec = 160_000_000.0,
                    prestigeLevel = 5,
                    businessesCount = 8,
                    contractsSignedCount = 5300L,
                    timestamp = System.currentTimeMillis() - 3600000L * 12,
                    notes = "Gestion de Fortune & Réserves d'Or • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇸🇬 Singapore Straits Holding",
                    avatarEmoji = "🚢",
                    totalCashEarned = 4_500_000_000.0,
                    peakRevenuePerSec = 65_000_000.0,
                    prestigeLevel = 4,
                    businessesCount = 8,
                    contractsSignedCount = 4100L,
                    timestamp = System.currentTimeMillis() - 3600000L * 18,
                    notes = "Flotte Commerciale & Hub Logistique • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇩🇪 Hans_Capital Berlin",
                    avatarEmoji = "🏭",
                    totalCashEarned = 950_000_000.0,
                    peakRevenuePerSec = 18_500_000.0,
                    prestigeLevel = 3,
                    businessesCount = 7,
                    contractsSignedCount = 3200L,
                    timestamp = System.currentTimeMillis() - 3600000L * 1,
                    notes = "Usines Automobiles & Chimie • 🟢 En direct",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇫🇷 Alexandre_CEO (Elysée)",
                    avatarEmoji = "💼",
                    totalCashEarned = 280_000_000.0,
                    peakRevenuePerSec = 5_800_000.0,
                    prestigeLevel = 2,
                    businessesCount = 6,
                    contractsSignedCount = 2400L,
                    timestamp = System.currentTimeMillis() - 600000L * 2,
                    notes = "Fintech & Startups IA • 🟢 En train de jouer",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇺🇸 CryptoWhale_99",
                    avatarEmoji = "🐳",
                    totalCashEarned = 75_000_000.0,
                    peakRevenuePerSec = 1_900_000.0,
                    prestigeLevel = 2,
                    businessesCount = 5,
                    contractsSignedCount = 1850L,
                    timestamp = System.currentTimeMillis() - 600000L * 4,
                    notes = "Trading Bourse & Arbitrage • 🟢 En direct",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇧🇷 Silva_Amazonia Empire",
                    avatarEmoji = "🌿",
                    totalCashEarned = 25_000_000.0,
                    peakRevenuePerSec = 680_000.0,
                    prestigeLevel = 1,
                    businessesCount = 5,
                    contractsSignedCount = 1420L,
                    timestamp = System.currentTimeMillis() - 600000L * 6,
                    notes = "Agro-business & Énergie Solaire • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇰🇷 Seoul_K_Ventures",
                    avatarEmoji = "🎮",
                    totalCashEarned = 8_500_000.0,
                    peakRevenuePerSec = 240_000.0,
                    prestigeLevel = 1,
                    businessesCount = 4,
                    contractsSignedCount = 1100L,
                    timestamp = System.currentTimeMillis() - 300000L,
                    notes = "Studios de Jeux & E-Sport • 🟢 Tape activement",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇨🇦 Maple_Tycoon_QC",
                    avatarEmoji = "🍁",
                    totalCashEarned = 2_800_000.0,
                    peakRevenuePerSec = 85_000.0,
                    prestigeLevel = 0,
                    businessesCount = 4,
                    contractsSignedCount = 820L,
                    timestamp = System.currentTimeMillis() - 180000L,
                    notes = "Mines & Bois d'œuvre • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇮🇹 Milano_Luxe_Design",
                    avatarEmoji = "👠",
                    totalCashEarned = 950_000.0,
                    peakRevenuePerSec = 32_000.0,
                    prestigeLevel = 0,
                    businessesCount = 3,
                    contractsSignedCount = 610L,
                    timestamp = System.currentTimeMillis() - 120000L,
                    notes = "Atelier de Mode & Design • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇪🇸 Carlos_Startup_BCN",
                    avatarEmoji = "☕",
                    totalCashEarned = 320_000.0,
                    peakRevenuePerSec = 11_500.0,
                    prestigeLevel = 0,
                    businessesCount = 3,
                    contractsSignedCount = 430L,
                    timestamp = System.currentTimeMillis() - 60000L,
                    notes = "Chaîne de Cafés & Vente en ligne • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇲🇽 Monterrey_Steel_Maker",
                    avatarEmoji = "⚙️",
                    totalCashEarned = 95_000.0,
                    peakRevenuePerSec = 3_800.0,
                    prestigeLevel = 0,
                    businessesCount = 2,
                    contractsSignedCount = 290L,
                    timestamp = System.currentTimeMillis() - 40000L,
                    notes = "Atelier Métallurgique • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇫🇷 Thomas_Rookie_92",
                    avatarEmoji = "🥖",
                    totalCashEarned = 28_000.0,
                    peakRevenuePerSec = 1_200.0,
                    prestigeLevel = 0,
                    businessesCount = 2,
                    contractsSignedCount = 180L,
                    timestamp = System.currentTimeMillis() - 25000L,
                    notes = "Boulangerie Artisanale • 🟢 En train de taper",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇧🇪 Liam_Brussels_Tycoon",
                    avatarEmoji = "🍫",
                    totalCashEarned = 7_500.0,
                    peakRevenuePerSec = 350.0,
                    prestigeLevel = 0,
                    businessesCount = 1,
                    contractsSignedCount = 95L,
                    timestamp = System.currentTimeMillis() - 15000L,
                    notes = "Chocolaterie & Kiosque à gaufres • 🟢 En ligne",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇨🇭 Julien_Apprenti_Genève",
                    avatarEmoji = "⌚",
                    totalCashEarned = 1_800.0,
                    peakRevenuePerSec = 80.0,
                    prestigeLevel = 0,
                    businessesCount = 1,
                    contractsSignedCount = 45L,
                    timestamp = System.currentTimeMillis() - 5000L,
                    notes = "Stand de limonade & premiers clics • 🟢 Tape activement",
                    isPlayerRun = false
                ),
                LeaderboardScoreEntity(
                    playerName = "🇵🇹 Joao_Porto_Beginner",
                    avatarEmoji = "⚡",
                    totalCashEarned = 450.0,
                    peakRevenuePerSec = 20.0,
                    prestigeLevel = 0,
                    businessesCount = 1,
                    contractsSignedCount = 18L,
                    timestamp = System.currentTimeMillis() - 2000L,
                    notes = "Nouveau joueur vient de rejoindre la session • 🟢 En direct",
                    isPlayerRun = false
                )
            )
            leaderboardDao.insertScores(liveGlobalTycoons)
        }
    }
}
