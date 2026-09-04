package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.data.GameSaveManager
import com.example.model.Achievement
import com.example.model.AchievementRewardType
import com.example.model.AdNetworkTier
import com.example.model.AuctionLot
import com.example.model.Business
import com.example.model.CareerStats
import com.example.model.CorporateTakeover
import com.example.model.CrisisEvent
import com.example.model.DailyLoginReward
import com.example.model.DailyMilestoneChest
import com.example.model.DailyMission
import com.example.model.DailyMissionCategory
import com.example.model.DailyMissionType
import com.example.model.DailyQuest
import com.example.model.Executive
import com.example.model.ExpandedTechNode
import com.example.model.FloatingCoin
import com.example.model.LuxuryAsset
import com.example.model.MarketNewsItem
import com.example.model.MoneyFormatter
import com.example.model.OfflineEarningsReport
import com.example.model.PlayerAvatar
import com.example.model.PlayerRank
import com.example.model.ProductivityUpgrade
import com.example.model.SponsorOffer
import com.example.model.StockItem
import com.example.model.TechBranch
import com.example.model.TechEffectType
import com.example.model.TechUpgrade
import com.example.model.UpgradeCategory
import com.example.audio.SoundManager
import com.example.data.local.EmpireDatabase
import com.example.data.local.GameDataStoreManager
import com.example.data.local.LeaderboardRepository
import com.example.data.local.LeaderboardScoreEntity
import com.example.data.online.OnlineLeaderboardService
import com.example.data.online.OnlinePlayerScore
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

data class GameUiState(
    val playerName: String = "Player234",
    val selectedAvatarId: Int = 0,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val cash: Double = 0.0,
    val totalCashEarned: Double = 0.0,
    val prestigeLevel: Int = 0,
    val prestigeBonusMultiplier: Double = 1.0,
    val businesses: List<Business> = GameRepository.getDefaultBusinesses(),
    val stocks: List<StockItem> = GameRepository.getDefaultStocks(),
    val executives: List<Executive> = GameRepository.getDefaultExecutives(),
    val adNetworks: List<AdNetworkTier> = GameRepository.getDefaultAdNetworks(),
    val achievements: List<Achievement> = GameRepository.getDefaultAchievements(),
    val dailyRewards: List<DailyLoginReward> = GameRepository.getDefaultDailyRewards(),
    val dailyQuests: List<DailyQuest> = GameRepository.getDefaultDailyQuests(),
    val techUpgrades: List<TechUpgrade> = GameRepository.getDefaultTechUpgrades(),
    val productivityUpgrades: List<ProductivityUpgrade> = GameRepository.getDefaultProductivityUpgrades(),
    val auctionLots: List<AuctionLot> = GameRepository.getDefaultAuctionLots(),
    val corporateTakeovers: List<CorporateTakeover> = GameRepository.getDefaultCorporateTakeovers(),
    val expandedTechNodes: List<ExpandedTechNode> = GameRepository.getDefaultExpandedTechTree(),
    val luxuryAssets: List<LuxuryAsset> = GameRepository.getDefaultLuxuryAssets(),
    val isRealEstateMarketOpen: Boolean = false,
    val isUpgradesStoreOpen: Boolean = false,
    val isAuctionDialogOpen: Boolean = false,
    val activeAuctionLotId: String? = null,
    val marketNews: List<MarketNewsItem> = GameRepository.getDefaultNewsFeed(),
    val activeNews: MarketNewsItem? = GameRepository.getDefaultNewsFeed().firstOrNull(),
    val isDailyRewardsDialogOpen: Boolean = false,
    val careerStats: CareerStats = CareerStats(),
    val clickPower: Double = 0.50,
    val clickLevel: Int = 1,
    val adBoostTimeRemainingSec: Int = 0,
    val frenzyProgress: Float = 0f,
    val isFrenzyActive: Boolean = false,
    val frenzyTimeRemainingSec: Int = 0,
    val activeSponsorOffer: SponsorOffer? = null,
    val isSponsorBannerVisible: Boolean = true,
    val activeMiniGame: SponsorOffer? = null,
    val activeCrisis: CrisisEvent? = null,
    val crisisTimeRemainingSec: Int = 0,
    val floatingCoins: List<FloatingCoin> = emptyList(),
    val comboStreak: Int = 0,
    val globalMultiplier: Double = 1.0,
    val multiplierTimeRemainingSec: Int = 0,
    val totalTaps: Long = 0,
    val adImpressionsCount: Long = 142,
    val totalAdRevenueEarned: Double = 0.0,
    val selectedTab: Int = 0,
    val feedbackMessage: String? = null,
    val isProfileDialogOpen: Boolean = false,
    val offlineEarningsReport: OfflineEarningsReport? = null,
    val isSimulatedAdOpen: Boolean = false,
    val simulatedAdRewardDesc: String = "",
    val simulatedAdRewardBonusCash: Double = 0.0,
    val simulatedAdActionType: String = "",
    val isWheelDialogOpen: Boolean = false,
    val isAutoTapperActive: Boolean = false,
    val autoTapperTimeRemainingSec: Int = 0,
    val megaprojects: List<com.example.model.Megaproject> = GameRepository.getDefaultMegaprojects(),
    val dailyMissions: List<DailyMission> = GameRepository.generateDailyMissions(),
    val dailyMilestoneChests: List<DailyMilestoneChest> = GameRepository.getDefaultMilestoneChests(),
    val dailyStreakDays: Int = 1,
    val lastMissionDayEpoch: Long = 0L,
    val dailyCashEarned: Double = 0.0,
    val timeUntilDailyResetFormatted: String = "23h 59m 59s",
    val isLeaderboardOpen: Boolean = false,
    val leaderboardScores: List<LeaderboardScoreEntity> = emptyList(),
    val playerRuns: List<LeaderboardScoreEntity> = emptyList(),
    val playerCurrentRank: Int = 1,
    val onlineScores: List<OnlinePlayerScore> = emptyList(),
    val isOnlineSyncing: Boolean = false,
    val isBatterySaverEnabled: Boolean = false,
    val isSettingsDialogOpen: Boolean = false,
    val activeSessionId: String? = null,
    val lastWheelSpinTimestampEpoch: Long = 0L
) {
    val isDailySpinAvailable: Boolean
        get() {
            if (lastWheelSpinTimestampEpoch == 0L) return true
            val now = System.currentTimeMillis()
            val calendarNow = java.util.Calendar.getInstance().apply { timeInMillis = now }
            val calendarLast = java.util.Calendar.getInstance().apply { timeInMillis = lastWheelSpinTimestampEpoch }
            return calendarNow.get(java.util.Calendar.YEAR) != calendarLast.get(java.util.Calendar.YEAR) ||
                   calendarNow.get(java.util.Calendar.DAY_OF_YEAR) != calendarLast.get(java.util.Calendar.DAY_OF_YEAR)
        }

    val clickUpgradeCost: Double
        get() = 15.0 * Math.pow(1.35, (clickLevel - 1).toDouble())

    val nextClickPowerGain: Double
        get() = 0.50 + (clickLevel * 0.35)

    val clickProgressPercent: Float
        get() = if (clickUpgradeCost > 0) (cash / clickUpgradeCost).toFloat().coerceIn(0f, 1f) else 0f

    val isAdBoostActive: Boolean
        get() = adBoostTimeRemainingSec > 0

    val claimableDailyMissionsCount: Int
        get() = dailyMissions.count { it.isCompleted && !it.isClaimed } +
                dailyMilestoneChests.count { it.isUnlocked && !it.isClaimed } +
                dailyRewards.count { it.isCurrentDay && !it.isClaimed }

    val currentDailyLoginReward: DailyLoginReward?
        get() = dailyRewards.find { it.isCurrentDay }

    val isDailyRewardClaimable: Boolean
        get() = dailyRewards.any { it.isCurrentDay && !it.isClaimed }

    val totalClaimableItemsCount: Int
        get() = claimableDailyMissionsCount

    val completedDailyMissionsCount: Int
        get() = dailyMissions.count { it.isCompleted || it.isClaimed }

    val luxuryPrestigeScore: Int
        get() = luxuryAssets.filter { it.isPurchased }.sumOf { it.prestigeScore }

    val totalRealEstateRentPerSec: Double
        get() = luxuryAssets.filter { it.isPurchased }.sumOf { it.effectiveRentRevenuePerSec }

    val totalRealEstateEmpireValue: Double
        get() = luxuryAssets.filter { it.isPurchased }.sumOf { it.cost }

    val activeWonAuctionsCount: Int
        get() = auctionLots.count { it.isWonByPlayer }

    val fullyAcquiredTakeoversCount: Int
        get() = corporateTakeovers.count { it.isFullyAcquired }

    val cashPerTap: Double
        get() {
            val execClickBoost = executives.filter { it.hired }.sumOf { it.clickPowerBoost }
            val luxuryClickBoost = luxuryAssets.filter { it.isPurchased }.sumOf { it.clickPowerBoostPercent }
            val techClickBoost = expandedTechNodes.filter { it.isUnlocked && it.branch == TechBranch.BIO_NANOTECH }.sumOf { it.multiplierBoost }
            val upgradeClickMultiplier = 1.0 + productivityUpgrades
                .filter { it.category == UpgradeCategory.CLICK_POWER }
                .sumOf { it.level * it.multiplierPerLevel }
            val base = (clickPower + execClickBoost) * (1.0 + luxuryClickBoost + techClickBoost) * upgradeClickMultiplier
            val frenzyUpgradeExtra = if (isFrenzyActive) {
                val frenzyUpg = productivityUpgrades.find { it.id == "upg_frenzy_amplifier" }?.level ?: 0
                1.0 + (frenzyUpg * 0.30)
            } else 1.0
            val frenzyMult = (if (isFrenzyActive) 10.0 else 1.0) * frenzyUpgradeExtra
            val adMult = if (isAdBoostActive) 2.0 else 1.0
            return base * frenzyMult * globalMultiplier * prestigeBonusMultiplier * adMult
        }

    val totalPassiveRevenuePerSec: Double
        get() {
            var sum = 0.0
            for (biz in businesses) {
                if (biz.isUnlocked && biz.managerHired) {
                    sum += biz.revenuePerSecond
                }
            }
            for (ad in adNetworks) {
                if (ad.isUnlocked) {
                    sum += ad.autoAdIncomePerSec
                }
            }
            // Takeover passive income
            for (takeover in corporateTakeovers) {
                sum += takeover.currentPassiveIncome
            }
            // Auction won lots income
            for (auc in auctionLots) {
                if (auc.isWonByPlayer) {
                    sum += auc.bonusCashYieldPerSec
                }
            }
            // Real estate rent income
            for (lux in luxuryAssets) {
                if (lux.isPurchased) {
                    sum += lux.effectiveRentRevenuePerSec
                }
            }

            val executiveBoost = executives.filter { it.hired }.sumOf { it.passiveRevenueBoost }
            var techBoost = 0.0
            if (techUpgrades.any { it.id == "tech_quantum_ai" && it.isUnlocked }) techBoost += 0.30
            if (techUpgrades.any { it.id == "tech_crypto_miner" && it.isUnlocked }) techBoost += 0.50
            if (techUpgrades.any { it.id == "tech_prestige_angel_perk" && it.isUnlocked }) techBoost += 0.50

            val expandedTechBoost = expandedTechNodes.filter { it.isUnlocked }.sumOf { it.multiplierBoost }
            val luxuryPassiveBoost = luxuryAssets.filter { it.isPurchased }.sumOf { it.passiveIncomeMultiplier }
            val auctionMultiplierBoost = auctionLots.filter { it.isWonByPlayer }.sumOf { it.permanentMultiplier }

            val megaBoost = megaprojects.sumOf { it.stage * it.passiveMultiplierBonus }
            val upgradePassiveMult = 1.0 + productivityUpgrades
                .filter { it.category == UpgradeCategory.PASSIVE_BUSINESS }
                .sumOf { it.level * it.multiplierPerLevel }
            val netMultiplier = (1.0 + executiveBoost + techBoost + expandedTechBoost + luxuryPassiveBoost + auctionMultiplierBoost + megaBoost) * upgradePassiveMult * prestigeBonusMultiplier * globalMultiplier * (if (isFrenzyActive) 5.0 else 1.0)
            return sum * netMultiplier
        }

    val netWorth: Double
        get() {
            val stockValue = stocks.sumOf { it.ownedShares * it.price }
            val businessValue = businesses.filter { it.isUnlocked }.sumOf { it.currentCost }
            val luxuryValue = luxuryAssets.filter { it.isPurchased }.sumOf { it.cost }
            val takeoversValue = corporateTakeovers.sumOf { it.totalEnterpriseValue * (it.ownedStakePercentage / 100.0) }
            return cash + stockValue + businessValue + luxuryValue + takeoversValue
        }
}

class EmpireGameViewModel(application: Application) : AndroidViewModel(application) {

    val gameSessionManager = GameSessionManager()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val database = EmpireDatabase.getInstance(application)
    private val leaderboardRepository = LeaderboardRepository(database.leaderboardDao())
    private val onlineLeaderboardService = OnlineLeaderboardService(application)
    private val gameDataStoreManager = GameDataStoreManager(application)

    private var lastTapTime: Long = 0L
    private val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    init {
        SoundManager.isSoundEnabled = _uiState.value.soundEnabled
        loadSavedGame()
        observeLeaderboard()
        fetchOnlineLeaderboard()
        startMainGameLoop()
        startStockMarketLoop()
        startSponsorRotationLoop()
        startCrisisEventScheduler()
        startNewsRotationLoop()
        startAutoSaveLoop()
    }

    private fun observeLeaderboard() {
        viewModelScope.launch {
            leaderboardRepository.seedInitialHistoricalScoresIfEmpty()
        }
        viewModelScope.launch {
            leaderboardRepository.topScores.collect { scores ->
                val currentCash = _uiState.value.totalCashEarned
                val rank = leaderboardRepository.getRankForScore(currentCash)
                _uiState.update {
                    it.copy(
                        leaderboardScores = scores,
                        playerCurrentRank = rank
                    )
                }
            }
        }
        viewModelScope.launch {
            leaderboardRepository.playerRuns.collect { runs ->
                _uiState.update { it.copy(playerRuns = runs) }
            }
        }
    }

    private fun loadSavedGame() {
        val app = getApplication<Application>()
        val saved = GameSaveManager.loadGame(app) ?: run {
            val backupJson = runBlocking {
                gameDataStoreManager.fullSaveJsonFlow.firstOrNull()
            }
            if (backupJson != null) {
                GameSaveManager.loadGameFromJson(backupJson)
            } else {
                null
            }
        }
        val now = System.currentTimeMillis()
        val currentEpochDay = now / (1000L * 60L * 60L * 24L)

        if (saved != null) {
            val updatedBiz = GameRepository.getDefaultBusinesses().map { def ->
                val s = saved.businessSavedData[def.id]
                val customName = saved.customBusinessNames[def.id]
                val withCustomName = if (customName != null) def.copy(name = customName) else def
                if (s != null) {
                    withCustomName.copy(
                        level = s.first,
                        isUnlocked = s.second,
                        managerHired = s.third
                    )
                } else withCustomName
            }

            val updatedStocks = GameRepository.getDefaultStocks().map { def ->
                val s = saved.stockSavedData[def.ticker]
                if (s != null) {
                    def.copy(
                        ownedShares = s.first,
                        totalInvested = s.second
                    )
                } else def
            }

            val updatedExecs = GameRepository.getDefaultExecutives().map { def ->
                def.copy(hired = saved.hiredExecIds.contains(def.id))
            }

            val updatedAds = GameRepository.getDefaultAdNetworks().map { def ->
                def.copy(isUnlocked = saved.unlockedAdIds.contains(def.id) || def.id == "ad_banner_basic")
            }

            val updatedAchievements = GameRepository.getDefaultAchievements().map { def ->
                val s = saved.achSavedData[def.id]
                if (s != null) {
                    def.copy(
                        currentValue = s.first,
                        isUnlocked = s.second,
                        isClaimed = s.third
                    )
                } else def
            }

            val updatedProductivityUpgrades = GameRepository.getDefaultProductivityUpgrades().map { def ->
                val lvl = saved.upgradeLevels[def.id] ?: 0
                def.copy(level = lvl)
            }

            val updatedAuctions = GameRepository.getDefaultAuctionLots().map { def ->
                if (saved.wonAuctionLotIds.contains(def.id)) {
                    def.copy(isWonByPlayer = true, isExpired = true, isPlayerWinning = true, timeRemainingSec = 0)
                } else def
            }

            val updatedTakeovers = GameRepository.getDefaultCorporateTakeovers().map { def ->
                val stake = saved.takeoverStakes[def.id] ?: 0
                def.copy(ownedStakePercentage = stake)
            }

            val updatedTechTree = GameRepository.getDefaultExpandedTechTree().map { def ->
                def.copy(isUnlocked = saved.unlockedTechIds.contains(def.id))
            }

            val updatedLuxury = GameRepository.getDefaultLuxuryAssets().map { def ->
                def.copy(isPurchased = saved.purchasedLuxuryIds.contains(def.id))
            }

            // Restore daily missions if same day epoch, or generate fresh set
            val isSameDay = saved.lastMissionDayEpoch == currentEpochDay && saved.lastMissionDayEpoch > 0L
            val initialMissions = if (isSameDay && saved.missionSavedData.isNotEmpty()) {
                val defaultList = GameRepository.generateDailyMissions(saved.cash, saved.prestigeLevel)
                defaultList.map { def ->
                    val m = saved.missionSavedData[def.id]
                    if (m != null) {
                        def.copy(
                            currentProgress = m.first,
                            isCompleted = m.second,
                            isClaimed = m.third
                        )
                    } else def
                }
            } else {
                GameRepository.generateDailyMissions(saved.cash, saved.prestigeLevel)
            }

            val initialChests = if (isSameDay && saved.chestSavedData.isNotEmpty()) {
                val defaultChests = GameRepository.getDefaultMilestoneChests(saved.prestigeLevel)
                defaultChests.map { chest ->
                    val c = saved.chestSavedData[chest.milestoneTarget]
                    if (c != null) {
                        chest.copy(
                            isUnlocked = c.first,
                            isClaimed = c.second
                        )
                    } else chest
                }
            } else {
                GameRepository.getDefaultMilestoneChests(saved.prestigeLevel)
            }

            val streak = if (isSameDay) saved.dailyStreakDays else {
                if (saved.lastMissionDayEpoch == currentEpochDay - 1) saved.dailyStreakDays + 1 else 1
            }

            val initialDailyRewards = GameRepository.getDailyRewardsForStreak(
                streakDays = streak,
                claimedDays = if (isSameDay) saved.claimedDailyRewardDays else {
                    val prevCycleDay = ((saved.dailyStreakDays - 1) % 7) + 1
                    val newCycleDay = ((streak - 1) % 7) + 1
                    if (newCycleDay <= prevCycleDay) emptySet() else saved.claimedDailyRewardDays
                }
            )

            val rawCash = saved.cash
            val rawTotal = saved.totalCashEarned
            val sanitizedCash = if (rawCash.isNaN() || rawCash.isInfinite() || rawCash > 1e18 || rawCash < 0.0) 250.0 else rawCash
            val sanitizedTotal = if (rawTotal.isNaN() || rawTotal.isInfinite() || rawTotal > 1e18 || rawTotal < 0.0) sanitizedCash else rawTotal
            val sanitizedPrestigeMult = if (saved.prestigeBonusMultiplier.isNaN() || saved.prestigeBonusMultiplier.isInfinite() || saved.prestigeBonusMultiplier > 100.0) 1.0 + (saved.prestigeLevel * 0.20) else saved.prestigeBonusMultiplier

            _uiState.update {
                it.copy(
                    playerName = saved.playerName,
                    selectedAvatarId = saved.selectedAvatarId,
                    soundEnabled = saved.soundEnabled,
                    hapticsEnabled = saved.hapticsEnabled,
                    cash = sanitizedCash,
                    totalCashEarned = sanitizedTotal,
                    prestigeLevel = saved.prestigeLevel,
                    prestigeBonusMultiplier = sanitizedPrestigeMult,
                    businesses = updatedBiz,
                    stocks = updatedStocks,
                    executives = updatedExecs,
                    adNetworks = updatedAds,
                    achievements = updatedAchievements,
                    productivityUpgrades = updatedProductivityUpgrades,
                    auctionLots = updatedAuctions,
                    corporateTakeovers = updatedTakeovers,
                    expandedTechNodes = updatedTechTree,
                    luxuryAssets = updatedLuxury,
                    careerStats = saved.careerStats,
                    totalTaps = saved.careerStats.totalTaps,
                    dailyMissions = initialMissions,
                    dailyMilestoneChests = initialChests,
                    dailyRewards = initialDailyRewards,
                    dailyStreakDays = streak,
                    lastMissionDayEpoch = currentEpochDay,
                    dailyCashEarned = if (isSameDay) saved.dailyCashEarned else 0.0,
                    lastWheelSpinTimestampEpoch = saved.lastWheelSpinTimestampEpoch
                )
            }
            SoundManager.isSoundEnabled = saved.soundEnabled

            // Calculate offline earnings
            val offlineMillis = now - saved.careerStats.lastSavedTimestamp
            val offlineSec = (offlineMillis / 1000L).coerceIn(0L, 8L * 3600L) // Max 8h

            if (offlineSec > 15) {
                val passivePerSec = _uiState.value.totalPassiveRevenuePerSec
                if (passivePerSec > 0) {
                    val earned = passivePerSec * offlineSec * 0.75 // 75% efficiency
                    if (earned > 10.0) {
                        val minutes = offlineSec / 60
                        val formattedTime = if (minutes >= 60) "${minutes / 60}h ${minutes % 60}m" else "${minutes}m"
                        val report = OfflineEarningsReport(
                            offlineSeconds = offlineSec,
                            earnedCash = earned,
                            formattedTime = formattedTime
                        )
                        _uiState.update { it.copy(offlineEarningsReport = report) }
                    }
                }
            }
        } else {
            _uiState.update {
                it.copy(
                    lastMissionDayEpoch = currentEpochDay,
                    dailyMissions = GameRepository.generateDailyMissions(50.0, 0),
                    dailyMilestoneChests = GameRepository.getDefaultMilestoneChests(0)
                )
            }
        }
    }

    private fun startAutoSaveLoop() {
        viewModelScope.launch {
            while (true) {
                delay(10000) // Auto-save every 10s
                saveCurrentGameState()
            }
        }
    }

    fun saveCurrentGameState() {
        val app = getApplication<Application>()
        val state = _uiState.value
        val jsonString = GameSaveManager.saveGame(
            context = app,
            playerName = state.playerName,
            selectedAvatarId = state.selectedAvatarId,
            soundEnabled = state.soundEnabled,
            hapticsEnabled = state.hapticsEnabled,
            cash = state.cash,
            totalCashEarned = state.totalCashEarned,
            prestigeLevel = state.prestigeLevel,
            prestigeBonusMultiplier = state.prestigeBonusMultiplier,
            businesses = state.businesses,
            stocks = state.stocks,
            executives = state.executives,
            adNetworks = state.adNetworks,
            achievements = state.achievements,
            careerStats = state.careerStats.copy(
                totalTaps = state.totalTaps,
                totalAdRevenueEarned = state.totalAdRevenueEarned
            ),
            dailyMissions = state.dailyMissions,
            milestoneChests = state.dailyMilestoneChests,
            dailyRewards = state.dailyRewards,
            dailyStreakDays = state.dailyStreakDays,
            lastMissionDayEpoch = state.lastMissionDayEpoch,
            dailyCashEarned = state.dailyCashEarned,
            productivityUpgrades = state.productivityUpgrades,
            lastWheelSpinTimestampEpoch = state.lastWheelSpinTimestampEpoch,
            wonAuctionLotIds = state.auctionLots.filter { it.isWonByPlayer }.map { it.id }.toSet(),
            takeoverStakes = state.corporateTakeovers.associate { it.id to it.ownedStakePercentage },
            unlockedTechIds = state.expandedTechNodes.filter { it.isUnlocked }.map { it.id }.toSet(),
            purchasedLuxuryIds = state.luxuryAssets.filter { it.isPurchased }.map { it.id }.toSet()
        )

        // Asynchronously persist currency and upgrade status to DataStore
        viewModelScope.launch {
            try {
                val upgArray = org.json.JSONArray()
                state.productivityUpgrades.forEach { u ->
                    val obj = org.json.JSONObject()
                    obj.put("id", u.id)
                    obj.put("level", u.level)
                    upgArray.put(obj)
                }
                gameDataStoreManager.saveProgress(
                    cash = state.cash,
                    totalCashEarned = state.totalCashEarned,
                    upgradesJson = upgArray.toString(),
                    fullSaveJson = jsonString
                )
            } catch (_: Exception) {}
        }
    }

    private fun triggerHapticFeedback(isStrong: Boolean = false) {
        if (!_uiState.value.hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (isStrong) {
                    VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE)
                } else {
                    VibrationEffect.createOneShot(15, 80)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(if (isStrong) 35L else 15L)
            }
        } catch (_: Exception) {}
    }

    private fun startMainGameLoop() {
        viewModelScope.launch {
            while (true) {
                val stateBefore = _uiState.value
                val isEco = stateBefore.isBatterySaverEnabled
                val elapsedSeconds = if (isEco) 0.5f else 0.1f
                val delayMillis = if (isEco) 500L else 100L

                delay(delayMillis)

                val state = _uiState.value
                val passiveRevenuePerTick = state.totalPassiveRevenuePerSec * elapsedSeconds.toDouble()

                // Update business cycle progress
                val updatedBusinesses = state.businesses.map { biz ->
                    if (biz.isUnlocked) {
                        val progressIncrement = elapsedSeconds / biz.cycleTimeSeconds
                        val newProgress = biz.currentCycleProgress + progressIncrement
                        if (newProgress >= 1f) {
                            biz.copy(currentCycleProgress = 0f)
                        } else {
                            biz.copy(currentCycleProgress = newProgress)
                        }
                    } else biz
                }

                // Frenzy timer countdown
                var frenzyProgress = state.frenzyProgress
                var isFrenzyActive = state.isFrenzyActive
                var frenzyTimeRemaining = state.frenzyTimeRemainingSec
                if (isFrenzyActive) {
                    frenzyProgress = max(0f, frenzyProgress - (elapsedSeconds / 10.0f))
                    if (frenzyProgress <= 0f) {
                        isFrenzyActive = false
                        frenzyTimeRemaining = 0
                    } else {
                        frenzyTimeRemaining = (frenzyProgress * 10).toInt()
                    }
                } else {
                    if (System.currentTimeMillis() - lastTapTime > 2500 && frenzyProgress > 0f) {
                        frenzyProgress = max(0f, frenzyProgress - (0.005f * (elapsedSeconds / 0.1f)))
                    }
                }

                // Clean floating coins
                val now = System.currentTimeMillis()
                val activeFloatingCoins = state.floatingCoins.filter { now - it.id < 900 }

                // Auto-tapper tick income
                val autoTapIncome = if (state.isAutoTapperActive) state.cashPerTap else 0.0
                val totalTickIncome = passiveRevenuePerTick + autoTapIncome

                _uiState.update { current ->
                    current.copy(
                        cash = current.cash + totalTickIncome,
                        totalCashEarned = current.totalCashEarned + totalTickIncome,
                        dailyCashEarned = current.dailyCashEarned + totalTickIncome,
                        totalTaps = if (current.isAutoTapperActive) current.totalTaps + 1 else current.totalTaps,
                        businesses = updatedBusinesses,
                        frenzyProgress = frenzyProgress,
                        isFrenzyActive = isFrenzyActive,
                        frenzyTimeRemainingSec = frenzyTimeRemaining,
                        floatingCoins = activeFloatingCoins
                    )
                }
            }
        }

        // 1-second interval timer
        viewModelScope.launch {
            while (true) {
                delay(1000)
                val now = System.currentTimeMillis()
                val currentEpochDay = now / (1000L * 60L * 60L * 24L)
                val millisInDay = now % (1000L * 60L * 60L * 24L)
                val millisUntilMidnight = (1000L * 60L * 60L * 24L) - millisInDay
                val hours = millisUntilMidnight / (1000L * 60L * 60L)
                val minutes = (millisUntilMidnight % (1000L * 60L * 60L)) / (1000L * 60L)
                val seconds = (millisUntilMidnight % (1000L * 60L)) / 1000L
                val countdownFormatted = String.format("%02dh %02dm %02ds", hours, minutes, seconds)

                _uiState.update { current ->
                    // Check if new day
                    if (current.lastMissionDayEpoch > 0L && currentEpochDay > current.lastMissionDayEpoch) {
                        val allCompleted = current.dailyMissions.all { it.isCompleted }
                        val newStreak = if (allCompleted) current.dailyStreakDays + 1 else 1
                        val newMissions = GameRepository.generateDailyMissions(current.netWorth, current.prestigeLevel)
                        val newChests = GameRepository.getDefaultMilestoneChests(current.prestigeLevel)
                        val newDailyRewards = GameRepository.getDailyRewardsForStreak(newStreak, emptySet())
                        current.copy(
                            dailyMissions = newMissions,
                            dailyMilestoneChests = newChests,
                            dailyRewards = newDailyRewards,
                            dailyQuests = GameRepository.getDefaultDailyQuests(),
                            dailyStreakDays = newStreak,
                            lastMissionDayEpoch = currentEpochDay,
                            dailyCashEarned = 0.0,
                            timeUntilDailyResetFormatted = countdownFormatted
                        )
                    } else {
                        val newCrisisTime = if (current.activeCrisis != null) {
                            max(0, current.crisisTimeRemainingSec - 1)
                        } else 0

                        val newCrisis = if (newCrisisTime == 0 && current.activeCrisis != null) null else current.activeCrisis
                        val newMultTime = max(0, current.multiplierTimeRemainingSec - 1)
                        val newMult = if (newMultTime == 0) 1.0 else current.globalMultiplier

                        val newAutoTime = if (current.isAutoTapperActive) max(0, current.autoTapperTimeRemainingSec - 1) else 0
                        val stillAutoActive = newAutoTime > 0
                        val newAdBoostTime = max(0, current.adBoostTimeRemainingSec - 1)

                        val updatedStats = current.careerStats.copy(
                            totalPlayTimeSeconds = current.careerStats.totalPlayTimeSeconds + 1
                        )

                        // Update auctions live countdown and simulate rival bids
                        var auctionWonMessage: String? = null
                        val updatedAuctionLots = current.auctionLots.map { lot ->
                            if (!lot.isWonByPlayer && !lot.isExpired && lot.timeRemainingSec > 0) {
                                val nextTime = lot.timeRemainingSec - 1
                                if (nextTime == 0) {
                                    if (lot.isPlayerWinning) {
                                        auctionWonMessage = "🏆 Adjugé ! Vous avez remporté l'enchère '${lot.title}' !"
                                        lot.copy(timeRemainingSec = 0, isWonByPlayer = true, isExpired = true)
                                    } else {
                                        // Expired without player win - can restart in 60s
                                        lot.copy(timeRemainingSec = 0, isExpired = true)
                                    }
                                } else {
                                    // Live bidding dynamics: if player is winning and time > 3, rivals might counter-bid
                                    var lotState = lot.copy(timeRemainingSec = nextTime)
                                    if (lot.isPlayerWinning && nextTime > 4 && Random.nextFloat() < 0.22f) {
                                        val affordableRivals = lot.activeRivals.filter { it.maxBudget >= lot.currentBid * 1.15 }
                                        if (affordableRivals.isNotEmpty()) {
                                            val rival = affordableRivals.random()
                                            val rivalBid = lot.currentBid * 1.15
                                            lotState = lotState.copy(
                                                currentBid = rivalBid,
                                                highestBidderName = "${rival.name} (${rival.title})",
                                                isPlayerWinning = false,
                                                timeRemainingSec = nextTime + 5 // Anti-sniping extension
                                            )
                                        }
                                    }
                                    lotState
                                }
                            } else lot
                        }

                        if (auctionWonMessage != null) {
                            triggerHapticFeedback(isStrong = true)
                            showFeedback(auctionWonMessage!!)
                        }

                        current.copy(
                            crisisTimeRemainingSec = newCrisisTime,
                            activeCrisis = newCrisis,
                            multiplierTimeRemainingSec = newMultTime,
                            globalMultiplier = newMult,
                            autoTapperTimeRemainingSec = newAutoTime,
                            isAutoTapperActive = stillAutoActive,
                            adBoostTimeRemainingSec = newAdBoostTime,
                            auctionLots = updatedAuctionLots,
                            careerStats = updatedStats,
                            timeUntilDailyResetFormatted = countdownFormatted,
                            lastMissionDayEpoch = if (current.lastMissionDayEpoch == 0L) currentEpochDay else current.lastMissionDayEpoch
                        )
                    }
                }
                checkAchievements()
                syncDailyMissions()
            }
        }
    }

    private fun startStockMarketLoop() {
        viewModelScope.launch {
            while (true) {
                delay(3500)
                _uiState.update { current ->
                    val updatedStocks = current.stocks.map { stock ->
                        val changeFactor = 1.0 + ((Random.nextFloat() * 2f - 0.98f) * stock.volatility)
                        val newPrice = max(1.0, stock.price * changeFactor)
                        val newHistory = (stock.history + newPrice.toFloat()).takeLast(12)
                        stock.copy(
                            previousPrice = stock.price,
                            price = newPrice,
                            history = newHistory
                        )
                    }
                    current.copy(stocks = updatedStocks)
                }
            }
        }
    }

    private fun startSponsorRotationLoop() {
        viewModelScope.launch {
            while (true) {
                val pool = GameRepository.getSponsorOffersPool()
                val nextOffer = pool.random()
                _uiState.update {
                    it.copy(
                        activeSponsorOffer = nextOffer,
                        isSponsorBannerVisible = true,
                        adImpressionsCount = it.adImpressionsCount + Random.nextLong(12, 45)
                    )
                }
                delay(120000) // Moins fréquent : toutes les 2 minutes
            }
        }
    }

    private fun startCrisisEventScheduler() {
        viewModelScope.launch {
            while (true) {
                delay(240000) // Moins fréquent : toutes les 4 minutes
                if (_uiState.value.activeCrisis == null && _uiState.value.cash > 100) {
                    val crisis = GameRepository.getRandomCrisis()
                    _uiState.update {
                        it.copy(
                            activeCrisis = crisis,
                            crisisTimeRemainingSec = crisis.timeLimitSec
                        )
                    }
                }
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun openProfileDialog() {
        _uiState.update { it.copy(isProfileDialogOpen = true) }
    }

    fun closeProfileDialog() {
        _uiState.update { it.copy(isProfileDialogOpen = false) }
    }

    fun openSettingsDialog() {
        _uiState.update { it.copy(isSettingsDialogOpen = true) }
    }

    fun closeSettingsDialog() {
        _uiState.update { it.copy(isSettingsDialogOpen = false) }
    }

    fun updatePlayerName(newName: String) {
        if (newName.isNotBlank()) {
            _uiState.update { it.copy(playerName = newName.trim()) }
            saveCurrentGameState()
            showFeedback("Nom mis à jour : ${newName.trim()}")
        }
    }

    fun renameBusiness(bizId: String, newName: String) {
        if (newName.isNotBlank()) {
            val updated = _uiState.value.businesses.map {
                if (it.id == bizId) {
                    it.copy(name = newName.trim())
                } else it
            }
            _uiState.update { it.copy(businesses = updated) }
            saveCurrentGameState()
            showFeedback("Filiale renommée avec succès !")
        }
    }

    fun selectAvatar(avatarId: Int) {
        _uiState.update { it.copy(selectedAvatarId = avatarId) }
        saveCurrentGameState()
    }

    fun toggleSound() {
        val nextVal = !_uiState.value.soundEnabled
        SoundManager.isSoundEnabled = nextVal
        _uiState.update { it.copy(soundEnabled = nextVal) }
        saveCurrentGameState()
        if (nextVal) {
            SoundManager.playTap()
        }
    }

    fun toggleHaptics() {
        _uiState.update { it.copy(hapticsEnabled = !it.hapticsEnabled) }
        saveCurrentGameState()
    }

    fun onTapAction(x: Float = 0.5f, y: Float = 0.5f) {
        val now = System.currentTimeMillis()
        val timeDiff = now - lastTapTime
        lastTapTime = now

        val newCombo = if (timeDiff < 600) {
            (_uiState.value.comboStreak + 1).coerceAtMost(50)
        } else {
            1
        }

        val isCrit = Random.nextFloat() < 0.15f
        val critFactor = if (isCrit) 3.5 else 1.0

        val execClickBoost = 1.0 + _uiState.value.executives.filter { it.hired }.sumOf { it.clickPowerBoost }
        val frenzyBoost = if (_uiState.value.isFrenzyActive) 10.0 else 1.0
        val comboMultiplier = 1.0 + (newCombo * 0.05)

        val baseTapValue = max(1.0, (_uiState.value.totalPassiveRevenuePerSec * 0.08) + _uiState.value.clickPower)
        val earned = baseTapValue * execClickBoost * frenzyBoost * critFactor * comboMultiplier * _uiState.value.globalMultiplier

        var newFrenzy = _uiState.value.frenzyProgress + (if (_uiState.value.isFrenzyActive) 0f else 0.045f)
        var isFrenzyTriggered = _uiState.value.isFrenzyActive
        var frenzyTime = _uiState.value.frenzyTimeRemainingSec

        if (newFrenzy >= 1f && !_uiState.value.isFrenzyActive) {
            newFrenzy = 1f
            isFrenzyTriggered = true
            frenzyTime = 10
            triggerHapticFeedback(isStrong = true)
        } else {
            triggerHapticFeedback(isStrong = isCrit)
        }
        SoundManager.playTap()

        val coinText = if (isCrit) "CRIT! +${MoneyFormatter.format(earned)}" else "+${MoneyFormatter.format(earned)}"
        val newFloatingCoin = FloatingCoin(
            id = now,
            x = x + (Random.nextFloat() * 0.2f - 0.1f),
            y = y + (Random.nextFloat() * 0.2f - 0.1f),
            text = coinText,
            isCrit = isCrit
        )

        val newTaps = _uiState.value.totalTaps + 1
        val newHighestCombo = max(_uiState.value.careerStats.highestCombo, newCombo)

        _uiState.update { current ->
            current.copy(
                cash = current.cash + earned,
                totalCashEarned = current.totalCashEarned + earned,
                dailyCashEarned = current.dailyCashEarned + earned,
                totalTaps = newTaps,
                comboStreak = newCombo,
                frenzyProgress = newFrenzy,
                isFrenzyActive = isFrenzyTriggered,
                frenzyTimeRemainingSec = frenzyTime,
                floatingCoins = current.floatingCoins + newFloatingCoin,
                careerStats = current.careerStats.copy(
                    totalTaps = newTaps,
                    highestCombo = newHighestCombo
                )
            )
        }
        trackDailyMission(DailyMissionType.TAP_CONTRACTS, 1L)
        trackDailyMission(DailyMissionType.REACH_COMBO, newCombo.toLong())
        checkAchievements()
    }

    fun upgradeBusiness(bizId: String) {
        val state = _uiState.value
        val biz = state.businesses.find { it.id == bizId } ?: return
        val cost = biz.currentCost

        if (state.cash >= cost) {
            triggerHapticFeedback(isStrong = false)
            SoundManager.playUpgrade()
            val updated = state.businesses.map {
                if (it.id == bizId) {
                    it.copy(
                        level = it.level + 1,
                        isUnlocked = true
                    )
                } else it
            }
            _uiState.update {
                it.copy(
                    cash = it.cash - cost,
                    businesses = updated,
                    feedbackMessage = "Niveau amélioré pour ${biz.name} !"
                )
            }
            trackDailyMission(DailyMissionType.UPGRADE_BUSINESSES, 1L)
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(cost)} requis")
        }
    }

    fun hireManager(bizId: String) {
        val state = _uiState.value
        val biz = state.businesses.find { it.id == bizId } ?: return
        if (biz.managerHired) return

        val cost = biz.managerCost
        if (state.cash >= cost) {
            triggerHapticFeedback(isStrong = true)
            SoundManager.playManagerHired()
            val updated = state.businesses.map {
                if (it.id == bizId) {
                    it.copy(managerHired = true)
                } else it
            }
            _uiState.update {
                it.copy(
                    cash = it.cash - cost,
                    businesses = updated,
                    feedbackMessage = "Automatisation activée pour ${biz.name} !"
                )
            }
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(cost)} requis")
        }
    }

    fun upgradeClickLevel() {
        val state = _uiState.value
        val cost = state.clickUpgradeCost
        if (state.cash >= cost) {
            triggerHapticFeedback(isStrong = true)
            SoundManager.playUpgrade()
            val gain = state.nextClickPowerGain
            _uiState.update { current ->
                current.copy(
                    cash = current.cash - cost,
                    clickLevel = current.clickLevel + 1,
                    clickPower = current.clickPower + gain,
                    feedbackMessage = "Clic amélioré au Niveau ${current.clickLevel + 1} (+${MoneyFormatter.format(gain)}/clic) !"
                )
            }
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(cost)} requis")
        }
    }

    fun triggerAdBoost() {
        triggerHapticFeedback(isStrong = true)
        SoundManager.playDailyReward()
        _uiState.update { current ->
            val addedSec = 30
            val newDuration = current.adBoostTimeRemainingSec + addedSec
            current.copy(
                adBoostTimeRemainingSec = newDuration,
                feedbackMessage = "⚡ Boost Clic x2 Cumulé (+${addedSec}s) ! Total: ${newDuration}s"
            )
        }
    }

    fun unlockBusiness(bizId: String) {
        val state = _uiState.value
        val biz = state.businesses.find { it.id == bizId } ?: return
        if (biz.isUnlocked) return

        val cost = biz.baseCost
        if (state.cash >= cost) {
            triggerHapticFeedback(isStrong = true)
            SoundManager.playBuildingClick()
            val updated = state.businesses.map {
                if (it.id == bizId) {
                    it.copy(isUnlocked = true, level = 1)
                } else it
            }
            _uiState.update {
                it.copy(
                    cash = it.cash - cost,
                    businesses = updated,
                    feedbackMessage = "Entreprise débloquée : ${biz.name} !"
                )
            }
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(cost)} requis")
        }
    }

    fun collectManualBusiness(bizId: String) {
        val state = _uiState.value
        val biz = state.businesses.find { it.id == bizId } ?: return
        if (!biz.isUnlocked) return

        val singleCycleRev = max(1.0, biz.revenuePerSecond * biz.cycleTimeSeconds)
        val boostCash = max(1.0, (singleCycleRev * 0.35) * state.globalMultiplier)
        _uiState.update {
            it.copy(
                cash = it.cash + boostCash,
                totalCashEarned = it.totalCashEarned + boostCash,
                dailyCashEarned = it.dailyCashEarned + boostCash,
                feedbackMessage = "Production instantanée ${biz.name} : +${MoneyFormatter.format(boostCash)} !"
            )
        }
        triggerHapticFeedback(isStrong = false)
        SoundManager.playBuildingClick()
        saveCurrentGameState()
    }

    fun buyStock(ticker: String, quantity: Int = 1) {
        val state = _uiState.value
        val stock = state.stocks.find { it.ticker == ticker } ?: return
        val totalCost = stock.price * quantity

        if (state.cash >= totalCost) {
            triggerHapticFeedback(isStrong = false)
            val updated = state.stocks.map {
                if (it.ticker == ticker) {
                    it.copy(
                        ownedShares = it.ownedShares + quantity,
                        totalInvested = it.totalInvested + totalCost
                    )
                } else it
            }
            val newStockTrades = state.careerStats.totalStockTrades + 1
            _uiState.update {
                it.copy(
                    cash = it.cash - totalCost,
                    stocks = updated,
                    careerStats = it.careerStats.copy(totalStockTrades = newStockTrades),
                    feedbackMessage = "Achat de $quantity action(s) ${stock.ticker} réussi !"
                )
            }
            trackDailyMission(DailyMissionType.TRADE_STOCKS, 1L)
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants pour acheter ces actions")
        }
    }

    fun sellStock(ticker: String, quantity: Int = 1) {
        val state = _uiState.value
        val stock = state.stocks.find { it.ticker == ticker } ?: return

        if (stock.ownedShares >= quantity) {
            triggerHapticFeedback(isStrong = false)
            SoundManager.playCollectRevenue()
            val totalRevenue = stock.price * quantity
            val updated = state.stocks.map {
                if (it.ticker == ticker) {
                    val remaining = it.ownedShares - quantity
                    it.copy(
                        ownedShares = remaining,
                        totalInvested = if (remaining == 0) 0.0 else (it.totalInvested * (remaining.toDouble() / it.ownedShares))
                    )
                } else it
            }
            val newStockTrades = state.careerStats.totalStockTrades + 1
            _uiState.update {
                it.copy(
                    cash = it.cash + totalRevenue,
                    stocks = updated,
                    careerStats = it.careerStats.copy(totalStockTrades = newStockTrades),
                    feedbackMessage = "Vente de $quantity action(s) pour ${MoneyFormatter.format(totalRevenue)} !"
                )
            }
            trackDailyMission(DailyMissionType.TRADE_STOCKS, 1L)
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Vous ne possédez pas assez d'actions")
        }
    }

    fun hireExecutive(execId: String) {
        val state = _uiState.value
        val exec = state.executives.find { it.id == execId } ?: return
        if (exec.hired) return

        if (state.cash >= exec.cost) {
            triggerHapticFeedback(isStrong = true)
            val updated = state.executives.map {
                if (it.id == execId) it.copy(hired = true) else it
            }
            _uiState.update {
                it.copy(
                    cash = it.cash - exec.cost,
                    executives = updated,
                    feedbackMessage = "${exec.name} (${exec.role}) a rejoint le conseil !"
                )
            }
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants pour embaucher cet exécutif")
        }
    }

    fun unlockAdNetwork(networkId: String) {
        val state = _uiState.value
        val net = state.adNetworks.find { it.id == networkId } ?: return
        if (net.isUnlocked) return

        if (state.cash >= net.unlockCost) {
            triggerHapticFeedback(isStrong = true)
            val updated = state.adNetworks.map {
                if (it.id == net.id) it.copy(isUnlocked = true) else it
            }
            _uiState.update {
                it.copy(
                    cash = it.cash - net.unlockCost,
                    adNetworks = updated,
                    feedbackMessage = "Régie publicitaire débloquée : ${net.name} !"
                )
            }
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants pour déployer cette régie")
        }
    }

    fun openActiveSponsorMiniGame() {
        val offer = _uiState.value.activeSponsorOffer ?: return
        val sessionId = gameSessionManager.startSession(offer.miniGameType.name)
        _uiState.update {
            it.copy(
                activeMiniGame = offer,
                isSponsorBannerVisible = false,
                activeSessionId = sessionId
            )
        }
    }

    fun dismissSponsorBanner() {
        _uiState.update { it.copy(isSponsorBannerVisible = false) }
    }

    fun closeMiniGame() {
        gameSessionManager.endSession()
        _uiState.update { it.copy(activeMiniGame = null, activeSessionId = null) }
    }

    fun registerMinigameTap(): Boolean {
        val sessionId = _uiState.value.activeSessionId ?: return false
        val isValid = gameSessionManager.registerInput(sessionId)
        if (!isValid) {
            triggerHapticFeedback(isStrong = false)
        }
        return isValid
    }

    fun completeMiniGame(success: Boolean, bonusFactor: Double = 1.0, score: Int = 0, maxScore: Int = 100) {
        val miniGame = _uiState.value.activeMiniGame ?: return
        val sessionId = _uiState.value.activeSessionId

        if (success && sessionId != null) {
            // Enforce rate-limiting and session-based score validation
            val isSessionValid = gameSessionManager.validateAndComplete(
                sessionId = sessionId,
                declaredScore = score,
                expectedMaxScore = maxScore
            )
            if (!isSessionValid) {
                triggerHapticFeedback(isStrong = true)
                _uiState.update {
                    it.copy(
                        activeMiniGame = null,
                        activeSessionId = null,
                        feedbackMessage = "⚠️ Session invalide : Vitesse anormale (Spam bloqué par le GameSessionManager)"
                    )
                }
                return
            }
        } else {
            gameSessionManager.endSession()
        }

        if (success) {
            triggerHapticFeedback(isStrong = true)
            SoundManager.playCollectRevenue()
            val cmoBoost = 1.0 + _uiState.value.executives.filter { it.hired }.sumOf { it.adCpmBoost }
            val baseReward = max(150.0, _uiState.value.totalPassiveRevenuePerSec * miniGame.rewardCashFactor)
            val cashEarned = baseReward * bonusFactor * cmoBoost
            val newMiniGamesWon = _uiState.value.careerStats.totalMiniGamesWon + 1

            _uiState.update {
                it.copy(
                    cash = it.cash + cashEarned,
                    totalCashEarned = it.totalCashEarned + cashEarned,
                    dailyCashEarned = it.dailyCashEarned + cashEarned,
                    totalAdRevenueEarned = it.totalAdRevenueEarned + cashEarned,
                    globalMultiplier = miniGame.rewardMultiplier,
                    multiplierTimeRemainingSec = miniGame.multiplierDurationSec,
                    activeMiniGame = null,
                    activeSessionId = null,
                    careerStats = it.careerStats.copy(totalMiniGamesWon = newMiniGamesWon),
                    feedbackMessage = "Sponsor validé ! +${MoneyFormatter.format(cashEarned)} & Boost x${miniGame.rewardMultiplier} !"
                )
            }
            trackDailyMission(DailyMissionType.PLAY_SPONSOR_MINIGAME, 1L)
            saveCurrentGameState()
            checkAchievements()
        } else {
            _uiState.update {
                it.copy(
                    activeMiniGame = null,
                    activeSessionId = null,
                    feedbackMessage = "Défi sponsor non complété. Réessaye au prochain créneau !"
                )
            }
        }
    }

    fun resolveCrisis(choiceIndex: Int) {
        val crisis = _uiState.value.activeCrisis ?: return
        triggerHapticFeedback(isStrong = true)
        val newCrisesCount = _uiState.value.careerStats.totalCrisesResolved + 1

        if (choiceIndex == 0) {
            val cost = _uiState.value.cash * crisis.costRatio
            val reward = max(200.0, _uiState.value.totalPassiveRevenuePerSec * 40.0) * crisis.rewardMultiplier

            _uiState.update {
                it.copy(
                    cash = max(0.0, it.cash - cost) + reward,
                    totalCashEarned = it.totalCashEarned + reward,
                    dailyCashEarned = it.dailyCashEarned + reward,
                    activeCrisis = null,
                    globalMultiplier = crisis.rewardMultiplier,
                    multiplierTimeRemainingSec = 20,
                    careerStats = it.careerStats.copy(totalCrisesResolved = newCrisesCount),
                    feedbackMessage = "Succès de crise ! Décision audacieuse récompensée : +${MoneyFormatter.format(reward)} !"
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    activeCrisis = null,
                    careerStats = it.careerStats.copy(totalCrisesResolved = newCrisesCount),
                    feedbackMessage = "Crise contenue prudemment sans dépenses majeures."
                )
            }
        }
        trackDailyMission(DailyMissionType.RESOLVE_CRISIS, 1L)
        saveCurrentGameState()
        checkAchievements()
    }

    fun triggerPrestige() {
        val state = _uiState.value
        val requiredCash = 50_000.0 * (state.prestigeLevel + 1) * (state.prestigeLevel + 1)
        if (state.totalCashEarned < requiredCash) {
            showFeedback("Vous devez avoir cumulé au moins ${MoneyFormatter.format(requiredCash)} pour débloquer ce Prestige !")
            return
        }

        // Auto-save run to Room leaderboard
        val avatar = GameRepository.getDefaultAvatars().find { it.id == state.selectedAvatarId }?.emoji ?: "💼"
        viewModelScope.launch {
            leaderboardRepository.savePlayerScore(
                playerName = state.playerName,
                avatarEmoji = avatar,
                totalCashEarned = state.totalCashEarned,
                peakRevenuePerSec = state.totalPassiveRevenuePerSec,
                prestigeLevel = state.prestigeLevel,
                businessesCount = state.businesses.count { it.isUnlocked },
                contractsSignedCount = state.totalTaps,
                notes = "Prestige P${state.prestigeLevel} vendu avec succès !"
            )
        }

        triggerHapticFeedback(isStrong = true)
        val newPrestigeLevel = state.prestigeLevel + 1
        val newMultiplier = 1.0 + (newPrestigeLevel * 1.5)
        val resetBusinesses = GameRepository.getDefaultBusinesses()
        val newPrestigeResets = state.careerStats.totalPrestigeResets + 1

        _uiState.update {
            val startingCash = 100.0 * newMultiplier
            it.copy(
                cash = startingCash,
                totalCashEarned = startingCash,
                prestigeLevel = newPrestigeLevel,
                prestigeBonusMultiplier = newMultiplier,
                businesses = resetBusinesses,
                globalMultiplier = 1.0,
                activeCrisis = null,
                activeMiniGame = null,
                careerStats = it.careerStats.copy(totalPrestigeResets = newPrestigeResets),
                feedbackMessage = "Vente d'empire réussie ! Prestige Niveau $newPrestigeLevel (Multiplicateur x$newMultiplier) !"
            )
        }
        saveCurrentGameState()
        checkAchievements()
    }

    fun claimAchievement(achievementId: String) {
        val state = _uiState.value
        val ach = state.achievements.find { it.id == achievementId } ?: return
        if (!ach.isUnlocked || ach.isClaimed) return

        triggerHapticFeedback(isStrong = true)
        SoundManager.playAchievement()
        val rewardCash = if (ach.rewardType == AchievementRewardType.CASH) ach.rewardValue else 0.0

        val updated = state.achievements.map {
            if (it.id == achievementId) it.copy(isClaimed = true) else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash + rewardCash,
                totalCashEarned = it.totalCashEarned + rewardCash,
                achievements = updated,
                feedbackMessage = "Succès réclamé : ${ach.title} (${ach.rewardLabel}) !"
            )
        }
        saveCurrentGameState()
    }

    fun triggerRewardedAd(rewardDesc: String, bonusCash: Double = 0.0, actionType: String = "BOOST") {
        _uiState.update {
            it.copy(
                isSimulatedAdOpen = true,
                simulatedAdRewardDesc = rewardDesc,
                simulatedAdRewardBonusCash = bonusCash,
                simulatedAdActionType = actionType
            )
        }
    }

    fun closeSimulatedAd() {
        _uiState.update { it.copy(isSimulatedAdOpen = false) }
    }

    fun onAdRewardEarned(actionType: String, bonusCash: Double = 0.0) {
        val state = _uiState.value
        triggerHapticFeedback(isStrong = true)
        SoundManager.playCollectRevenue()

        val newImpressions = state.adImpressionsCount + 1
        val cmoBoost = 1.0 + state.executives.filter { it.hired }.sumOf { it.adCpmBoost }
        val adUpgBonus = 1.0 + state.productivityUpgrades
            .filter { it.category == UpgradeCategory.ADS_AND_SPONSORS }
            .sumOf { it.level * it.multiplierPerLevel }
        val durationBonusFactor = 1.0 + (state.productivityUpgrades.find { it.id == "upg_turbo_boost_duration" }?.level ?: 0) * 0.25

        val baseAdRev = max(100.0, state.totalPassiveRevenuePerSec * 15.0) * cmoBoost * adUpgBonus
        val totalGained = (bonusCash + baseAdRev) * adUpgBonus

        when (actionType) {
            "OFFLINE_DOUBLER" -> {
                claimOfflineEarnings(isDoubled = true)
            }
            "SUPER_BOOST_4X" -> {
                val boostSec = (60.0 * durationBonusFactor).toInt()
                _uiState.update {
                    val accumulatedSec = it.multiplierTimeRemainingSec + boostSec
                    it.copy(
                        globalMultiplier = max(it.globalMultiplier, 4.0),
                        multiplierTimeRemainingSec = accumulatedSec,
                        cash = it.cash + totalGained,
                        totalCashEarned = it.totalCashEarned + totalGained,
                        dailyCashEarned = it.dailyCashEarned + totalGained,
                        totalAdRevenueEarned = it.totalAdRevenueEarned + totalGained,
                        adImpressionsCount = newImpressions,
                        isSimulatedAdOpen = false,
                        feedbackMessage = "Pub validée ! Surcharge x4.0 cumulée (+${boostSec}s, Total: ${accumulatedSec}s) & +${MoneyFormatter.format(totalGained)} !"
                    )
                }
            }
            "INSTANT_CASH_DROP" -> {
                val dropCash = (bonusCash * adUpgBonus).coerceAtLeast(totalGained)
                _uiState.update {
                    it.copy(
                        cash = it.cash + dropCash,
                        totalCashEarned = it.totalCashEarned + dropCash,
                        dailyCashEarned = it.dailyCashEarned + dropCash,
                        totalAdRevenueEarned = it.totalAdRevenueEarned + dropCash,
                        adImpressionsCount = newImpressions,
                        isSimulatedAdOpen = false,
                        feedbackMessage = "Pub validée ! Colis Sponsor Express : +${MoneyFormatter.format(dropCash)} !"
                    )
                }
            }
            "FRENZY_BOOST" -> {
                val frenzySec = (15.0 * durationBonusFactor).toInt()
                val addedMultTime = (14400.0 * durationBonusFactor).toInt()
                _uiState.update {
                    val accumulatedFrenzy = it.frenzyTimeRemainingSec + frenzySec
                    val accumulatedMultTime = it.multiplierTimeRemainingSec + addedMultTime
                    it.copy(
                        isFrenzyActive = true,
                        frenzyProgress = 1.0f,
                        frenzyTimeRemainingSec = accumulatedFrenzy,
                        globalMultiplier = max(it.globalMultiplier, 2.5),
                        multiplierTimeRemainingSec = accumulatedMultTime,
                        cash = it.cash + totalGained,
                        totalCashEarned = it.totalCashEarned + totalGained,
                        dailyCashEarned = it.dailyCashEarned + totalGained,
                        totalAdRevenueEarned = it.totalAdRevenueEarned + totalGained,
                        adImpressionsCount = newImpressions,
                        isSimulatedAdOpen = false,
                        feedbackMessage = "Pub Réelle AdMob validée ! Frenzy x10 (+${frenzySec}s) & Multiplicateur x2.5 cumulé (+${addedMultTime / 3600}h) !"
                    )
                }
            }
            "WHEEL_SPIN" -> {
                val addedSec = (3600.0 * durationBonusFactor).toInt()
                _uiState.update {
                    val accumulatedTime = it.multiplierTimeRemainingSec + addedSec
                    it.copy(
                        globalMultiplier = max(it.globalMultiplier, 2.0),
                        multiplierTimeRemainingSec = accumulatedTime,
                        cash = it.cash + totalGained,
                        totalCashEarned = it.totalCashEarned + totalGained,
                        dailyCashEarned = it.dailyCashEarned + totalGained,
                        totalAdRevenueEarned = it.totalAdRevenueEarned + totalGained,
                        adImpressionsCount = newImpressions,
                        isSimulatedAdOpen = false,
                        feedbackMessage = "Pub Réelle AdMob validée ! +${MoneyFormatter.format(totalGained)} & Boost x2.0 cumulé (+${addedSec / 3600}h) !"
                    )
                }
            }
            else -> {
                val addedSec = (7200.0 * durationBonusFactor).toInt()
                _uiState.update {
                    val accumulatedTime = it.multiplierTimeRemainingSec + addedSec
                    it.copy(
                        globalMultiplier = max(it.globalMultiplier, 2.0),
                        multiplierTimeRemainingSec = accumulatedTime,
                        cash = it.cash + totalGained,
                        totalCashEarned = it.totalCashEarned + totalGained,
                        dailyCashEarned = it.dailyCashEarned + totalGained,
                        totalAdRevenueEarned = it.totalAdRevenueEarned + totalGained,
                        adImpressionsCount = newImpressions,
                        isSimulatedAdOpen = false,
                        feedbackMessage = "Pub Réelle AdMob validée ! Boost Global x2.0 Cumulé (+${addedSec / 3600}h, Total: ${accumulatedTime / 3600}h) & +${MoneyFormatter.format(totalGained)} !"
                    )
                }
            }
        }
        trackDailyMission(DailyMissionType.WATCH_ADMOB_AD, 1L)
        saveCurrentGameState()
        checkAchievements()
    }

    fun openUpgradesStore() {
        _uiState.update { it.copy(isUpgradesStoreOpen = true) }
    }

    fun closeUpgradesStore() {
        _uiState.update { it.copy(isUpgradesStoreOpen = false) }
    }

    fun buyProductivityUpgrade(upgradeId: String) {
        val state = _uiState.value
        val upgrade = state.productivityUpgrades.find { it.id == upgradeId } ?: return
        if (upgrade.isMaxed) return

        val cost = upgrade.currentCost
        if (state.cash >= cost) {
            triggerHapticFeedback(isStrong = true)
            SoundManager.playUpgrade()
            val updated = state.productivityUpgrades.map {
                if (it.id == upgradeId) it.copy(level = it.level + 1) else it
            }
            _uiState.update {
                it.copy(
                    cash = it.cash - cost,
                    productivityUpgrades = updated,
                    feedbackMessage = "Amélioration : ${upgrade.name} (Niv. ${upgrade.level + 1}) !"
                )
            }
            trackDailyMission(DailyMissionType.UPGRADE_BUSINESSES, 1L)
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants pour cette amélioration")
        }
    }

    fun buyMaxProductivityUpgrade(upgradeId: String) {
        val state = _uiState.value
        val upgrade = state.productivityUpgrades.find { it.id == upgradeId } ?: return
        if (upgrade.isMaxed) return

        var tempCash = state.cash
        var tempLevel = upgrade.level
        var totalSpent = 0.0

        while (tempLevel < upgrade.maxLevel) {
            val cost = upgrade.baseCost * Math.pow(upgrade.costMultiplier, tempLevel.toDouble())
            if (tempCash >= cost) {
                tempCash -= cost
                totalSpent += cost
                tempLevel++
            } else {
                break
            }
        }

        if (tempLevel > upgrade.level) {
            triggerHapticFeedback(isStrong = true)
            SoundManager.playUpgrade()
            val levelsGained = tempLevel - upgrade.level
            val updated = state.productivityUpgrades.map {
                if (it.id == upgradeId) it.copy(level = tempLevel) else it
            }
            _uiState.update {
                it.copy(
                    cash = tempCash,
                    productivityUpgrades = updated,
                    feedbackMessage = "+$levelsGained Niveaux achetés pour ${upgrade.name} !"
                )
            }
            trackDailyMission(DailyMissionType.UPGRADE_BUSINESSES, levelsGained.toLong())
            saveCurrentGameState()
            checkAchievements()
        } else {
            showFeedback("Fonds insuffisants pour améliorer ${upgrade.name}")
        }
    }

    fun completeSimulatedAd() {
        val state = _uiState.value
        onAdRewardEarned(state.simulatedAdActionType, state.simulatedAdRewardBonusCash)
    }

    fun openWheelDialog() {
        _uiState.update { it.copy(isWheelDialogOpen = true) }
    }

    fun closeWheelDialog() {
        _uiState.update { it.copy(isWheelDialogOpen = false) }
    }

    fun startDailyWheelSession(): Boolean {
        val state = _uiState.value
        if (!state.isDailySpinAvailable) {
            _uiState.update { it.copy(feedbackMessage = "⚠️ Tu as déjà lancé la Roue aujourd'hui !") }
            return false
        }
        val sessionId = gameSessionManager.startSession("DAILY_WHEEL_SPIN")
        _uiState.update { it.copy(activeSessionId = sessionId) }
        return true
    }

    fun spinWheelReward(cashFactor: Double, boostMult: Double) {
        val state = _uiState.value
        val sessionId = state.activeSessionId

        if (sessionId == null) {
            _uiState.update { it.copy(feedbackMessage = "⚠️ Session de tirage expirée ou inexistante.") }
            return
        }

        // Validate session via GameSessionManager
        val isSessionValid = gameSessionManager.validateAndComplete(
            sessionId = sessionId,
            declaredScore = 1,
            expectedMaxScore = 1
        )
        if (!isSessionValid) {
            _uiState.update { it.copy(feedbackMessage = "⚠️ Tentative de re-tirage bloquée par le GameSessionManager !") }
            return
        }

        triggerHapticFeedback(isStrong = true)
        SoundManager.playDailyReward()
        val rewardCash = max(500.0, state.cashPerTap * cashFactor)
        _uiState.update {
            it.copy(
                cash = it.cash + rewardCash,
                totalCashEarned = it.totalCashEarned + rewardCash,
                dailyCashEarned = it.dailyCashEarned + rewardCash,
                globalMultiplier = max(it.globalMultiplier, boostMult),
                multiplierTimeRemainingSec = max(it.multiplierTimeRemainingSec, 120),
                feedbackMessage = "Roue de la Fortune : +${MoneyFormatter.format(rewardCash)} & Boost x$boostMult !",
                lastWheelSpinTimestampEpoch = System.currentTimeMillis(),
                activeSessionId = null
            )
        }
        trackDailyMission(DailyMissionType.SPIN_WHEEL, 1L)
        saveCurrentGameState()
        checkAchievements()
    }

    fun activateAutoTapper(seconds: Int = 30) {
        triggerHapticFeedback(isStrong = true)
        _uiState.update {
            it.copy(
                isAutoTapperActive = true,
                autoTapperTimeRemainingSec = seconds,
                feedbackMessage = "Assistant IA Auto-Deal activé pour ${seconds}s !"
            )
        }
    }

    fun upgradeMegaproject(projectId: String) {
        val state = _uiState.value
        val project = state.megaprojects.find { it.id == projectId } ?: return
        if (project.isMaxed) return

        val cost = project.currentCost
        if (state.cash >= cost) {
            triggerHapticFeedback(isStrong = true)
            val updatedProjects = state.megaprojects.map {
                if (it.id == projectId) it.copy(stage = it.stage + 1) else it
            }
            _uiState.update {
                it.copy(
                    cash = it.cash - cost,
                    megaprojects = updatedProjects,
                    feedbackMessage = "Mégaprojet '${project.name}' amélioré au Niveau ${project.stage + 1} !"
                )
            }
            saveCurrentGameState()
            checkAchievements()
        } else {
            _uiState.update {
                it.copy(feedbackMessage = "Fonds insuffisants : ${MoneyFormatter.format(cost)} requis.")
            }
        }
    }

    fun claimOfflineEarnings(isDoubled: Boolean) {
        val report = _uiState.value.offlineEarningsReport ?: return
        val factor = if (isDoubled) 2.0 else 1.0
        val finalCash = report.earnedCash * factor

        triggerHapticFeedback(isStrong = true)
        SoundManager.playCollectRevenue()
        _uiState.update {
            it.copy(
                cash = it.cash + finalCash,
                totalCashEarned = it.totalCashEarned + finalCash,
                offlineEarningsReport = null,
                feedbackMessage = "Gains hors-ligne récoltés : +${MoneyFormatter.format(finalCash)} !"
            )
        }
        saveCurrentGameState()
    }

    fun exportSaveData(): String? {
        val app = getApplication<Application>()
        saveCurrentGameState()
        return GameSaveManager.exportSaveString(app)
    }

    fun importSaveData(code: String): Boolean {
        val app = getApplication<Application>()
        val success = GameSaveManager.importSaveString(app, code)
        if (success) {
            loadSavedGame()
            showFeedback("Sauvegarde restaurée avec succès !")
        } else {
            showFeedback("Code de sauvegarde invalide !")
        }
        return success
    }

    fun resetGameProgress() {
        val app = getApplication<Application>()
        GameSaveManager.clearSave(app)
        _uiState.value = GameUiState()
        saveCurrentGameState()
        showFeedback("Partie réinitialisée à zéro.")
    }

    private fun startNewsRotationLoop() {
        viewModelScope.launch {
            while (true) {
                delay(15000)
                val feed = GameRepository.getDefaultNewsFeed()
                val nextNews = feed.random()
                _uiState.update { current ->
                    // Also slightly affect stock price if affectedTicker exists
                    var updatedStocks = current.stocks
                    if (nextNews.affectedTicker != null) {
                        updatedStocks = current.stocks.map { st ->
                            if (st.ticker == nextNews.affectedTicker) {
                                val boostedPrice = max(1.0, st.price * (1.0 + nextNews.priceImpactPercent / 100.0))
                                st.copy(price = boostedPrice, history = (st.history + boostedPrice.toFloat()).takeLast(12))
                            } else st
                        }
                    }
                    current.copy(
                        activeNews = nextNews,
                        stocks = updatedStocks
                    )
                }
            }
        }
    }

    fun openDailyRewardsDialog() {
        _uiState.update { it.copy(isDailyRewardsDialogOpen = true) }
    }

    fun closeDailyRewardsDialog() {
        _uiState.update { it.copy(isDailyRewardsDialogOpen = false) }
    }

    fun claimDailyLoginReward(dayNumber: Int) {
        val state = _uiState.value
        val reward = state.dailyRewards.find { it.dayNumber == dayNumber } ?: return
        if (reward.isClaimed) return

        triggerHapticFeedback(isStrong = true)
        SoundManager.playDailyReward()
        val updatedRewards = state.dailyRewards.map {
            if (it.dayNumber == dayNumber) it.copy(isClaimed = true) else it
        }

        var newGlobalMultiplier = state.globalMultiplier
        var newMultiplierTime = state.multiplierTimeRemainingSec
        if (reward.boostMultiplier > 1.0 && reward.boostDurationSec > 0) {
            newGlobalMultiplier = reward.boostMultiplier
            newMultiplierTime = reward.boostDurationSec
        }

        _uiState.update {
            it.copy(
                cash = it.cash + reward.cashReward,
                totalCashEarned = it.totalCashEarned + reward.cashReward,
                dailyRewards = updatedRewards,
                globalMultiplier = newGlobalMultiplier,
                multiplierTimeRemainingSec = newMultiplierTime,
                feedbackMessage = "Cadeau du Jour $dayNumber récupéré : ${reward.rewardLabel} !"
            )
        }
        saveCurrentGameState()
    }

    fun claimDailyQuest(questId: String) {
        val state = _uiState.value
        val quest = state.dailyQuests.find { it.id == questId } ?: return
        if (!quest.isCompleted || quest.isClaimed) return

        triggerHapticFeedback(isStrong = true)
        val updatedQuests = state.dailyQuests.map {
            if (it.id == questId) it.copy(isClaimed = true) else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash + quest.rewardCash,
                totalCashEarned = it.totalCashEarned + quest.rewardCash,
                dailyQuests = updatedQuests,
                feedbackMessage = "Mission complétée : ${quest.rewardLabel} encaissés !"
            )
        }
        saveCurrentGameState()
    }

    fun unlockTechUpgrade(techId: String) {
        val state = _uiState.value
        val tech = state.techUpgrades.find { it.id == techId } ?: return
        if (tech.isUnlocked) return

        if (state.cash >= tech.cost) {
            triggerHapticFeedback(isStrong = true)
            val updated = state.techUpgrades.map {
                if (it.id == techId) it.copy(isUnlocked = true) else it
            }
            _uiState.update {
                it.copy(
                    cash = it.cash - tech.cost,
                    techUpgrades = updated,
                    feedbackMessage = "Technologie ${tech.name} activée !"
                )
            }
            saveCurrentGameState()
        } else {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(tech.cost)} requis")
        }
    }

    private fun checkAchievements() {
        val state = _uiState.value
        val updated = state.achievements.map { ach ->
            var currentVal = ach.currentValue
            when (ach.id) {
                "ach_taps_10", "ach_taps_100", "ach_taps_1000", "ach_taps_5000" -> currentVal = state.totalTaps
                "ach_combo_20", "ach_combo_50", "ach_combo_100" -> currentVal = state.careerStats.highestCombo.toLong()
                "ach_cash_10k", "ach_cash_1m", "ach_cash_1b", "ach_cash_1t" -> currentVal = state.totalCashEarned.toLong()
                "ach_stocks_10", "ach_stocks_50", "ach_stocks_100" -> currentVal = state.careerStats.totalStockTrades.toLong()
                "ach_ads_unlocked", "ach_ads_max" -> currentVal = state.adNetworks.count { it.isUnlocked }.toLong()
                "ach_sponsors_5", "ach_sponsors_20" -> currentVal = state.careerStats.totalMiniGamesWon.toLong()
                "ach_crisis_3", "ach_crisis_10" -> currentVal = state.careerStats.totalCrisesResolved.toLong()
                "ach_prestige_1", "ach_prestige_5", "ach_prestige_10" -> currentVal = state.prestigeLevel.toLong()
                "ach_managers_1", "ach_managers_6", "ach_managers_12" -> currentVal = state.businesses.count { it.managerHired }.toLong()
                "ach_exec_1", "ach_exec_3" -> currentVal = state.executives.count { it.hired }.toLong()
                "ach_biz_coffee_100" -> currentVal = state.businesses.find { it.id == "biz_coffee" }?.level?.toLong() ?: 0L
                "ach_biz_tech_50" -> currentVal = state.businesses.find { it.id == "biz_tech" }?.level?.toLong() ?: 0L
                "ach_biz_quantum_10" -> currentVal = state.businesses.find { it.id == "biz_quantum" }?.level?.toLong() ?: 0L
                "ach_tech_5", "ach_tech_all" -> currentVal = state.techUpgrades.count { it.isUnlocked }.toLong()
                "ach_mega_1" -> currentVal = state.megaprojects.sumOf { it.stage }.toLong()
                "ach_mega_max" -> currentVal = state.megaprojects.count { it.isMaxed }.toLong()
            }
            val unlocked = ach.isUnlocked || currentVal >= ach.targetValue
            ach.copy(currentValue = currentVal, isUnlocked = unlocked)
        }

        // Also update Daily Quests progress
        val updatedQuests = state.dailyQuests.map { q ->
            var progress = q.currentProgress
            when (q.id) {
                "quest_tap_50" -> progress = state.totalTaps.toInt().coerceAtMost(q.targetProgress)
                "quest_upgrade_5" -> progress = state.businesses.sumOf { it.level }.coerceAtMost(q.targetProgress)
                "quest_stock_3" -> progress = state.careerStats.totalStockTrades.coerceAtMost(q.targetProgress)
            }
            val completed = progress >= q.targetProgress
            q.copy(currentProgress = progress, isCompleted = completed)
        }

        _uiState.update { it.copy(achievements = updated, dailyQuests = updatedQuests) }
    }

    fun trackDailyMission(type: DailyMissionType, amount: Long = 1L) {
        val state = _uiState.value
        var hasNewCompletion = false
        val updatedMissions = state.dailyMissions.map { m ->
            if (m.type == type && !m.isClaimed) {
                val newProgress = when (type) {
                    DailyMissionType.REACH_COMBO -> max(m.currentProgress, amount)
                    DailyMissionType.EARN_CASH -> max(m.currentProgress, amount)
                    else -> m.currentProgress + amount
                }
                val isDone = newProgress >= m.targetProgress
                if (isDone && !m.isCompleted) {
                    hasNewCompletion = true
                }
                m.copy(
                    currentProgress = newProgress,
                    isCompleted = isDone
                )
            } else m
        }

        val completedCount = updatedMissions.count { it.isCompleted || it.isClaimed }
        val updatedChests = state.dailyMilestoneChests.map { chest ->
            if (completedCount >= chest.milestoneTarget) {
                chest.copy(isUnlocked = true)
            } else chest
        }

        _uiState.update {
            it.copy(
                dailyMissions = updatedMissions,
                dailyMilestoneChests = updatedChests
            )
        }

        if (hasNewCompletion) {
            triggerHapticFeedback(isStrong = true)
            showFeedback("🎯 Mission quotidienne complétée ! Récompense disponible !")
        }
    }

    private fun syncDailyMissions() {
        val state = _uiState.value
        val dailyCash = state.dailyCashEarned.toLong()
        val completedCount = state.dailyMissions.count { it.isCompleted || it.isClaimed }

        val updatedMissions = state.dailyMissions.map { m ->
            if (m.type == DailyMissionType.EARN_CASH && !m.isClaimed) {
                val progress = dailyCash.coerceAtMost(m.targetProgress)
                m.copy(currentProgress = progress, isCompleted = progress >= m.targetProgress)
            } else m
        }

        val updatedChests = state.dailyMilestoneChests.map { chest ->
            if (completedCount >= chest.milestoneTarget) {
                chest.copy(isUnlocked = true)
            } else chest
        }

        _uiState.update {
            it.copy(
                dailyMissions = updatedMissions,
                dailyMilestoneChests = updatedChests
            )
        }
    }

    fun claimDailyMission(missionId: String) {
        val state = _uiState.value
        val mission = state.dailyMissions.find { it.id == missionId } ?: return
        if (!mission.isCompleted || mission.isClaimed) return

        triggerHapticFeedback(isStrong = true)
        SoundManager.playCollectRevenue()
        val updatedMissions = state.dailyMissions.map {
            if (it.id == missionId) it.copy(isClaimed = true) else it
        }

        val completedCount = updatedMissions.count { it.isCompleted || it.isClaimed }
        val updatedChests = state.dailyMilestoneChests.map { chest ->
            if (completedCount >= chest.milestoneTarget) {
                chest.copy(isUnlocked = true)
            } else chest
        }

        var newGlobalMultiplier = state.globalMultiplier
        var newMultiplierTime = state.multiplierTimeRemainingSec
        if (mission.rewardBoostMultiplier > 1.0 && mission.rewardBoostDurationSec > 0) {
            newGlobalMultiplier = max(state.globalMultiplier, mission.rewardBoostMultiplier)
            newMultiplierTime = max(state.multiplierTimeRemainingSec, mission.rewardBoostDurationSec)
        }

        _uiState.update {
            it.copy(
                cash = it.cash + mission.rewardCash,
                totalCashEarned = it.totalCashEarned + mission.rewardCash,
                dailyMissions = updatedMissions,
                dailyMilestoneChests = updatedChests,
                globalMultiplier = newGlobalMultiplier,
                multiplierTimeRemainingSec = newMultiplierTime,
                feedbackMessage = "Mission réussie : +${MoneyFormatter.format(mission.rewardCash)} encaissés !"
            )
        }
        saveCurrentGameState()
    }

    fun claimDailyMilestoneChest(milestoneTarget: Int) {
        val state = _uiState.value
        val chest = state.dailyMilestoneChests.find { it.milestoneTarget == milestoneTarget } ?: return
        if (!chest.isUnlocked || chest.isClaimed) return

        triggerHapticFeedback(isStrong = true)
        SoundManager.playDailyReward()
        val updatedChests = state.dailyMilestoneChests.map {
            if (it.milestoneTarget == milestoneTarget) it.copy(isClaimed = true) else it
        }

        var newGlobalMultiplier = state.globalMultiplier
        var newMultiplierTime = state.multiplierTimeRemainingSec
        if (chest.rewardBoostMultiplier > 1.0 && chest.rewardBoostDurationSec > 0) {
            newGlobalMultiplier = max(state.globalMultiplier, chest.rewardBoostMultiplier)
            newMultiplierTime = max(state.multiplierTimeRemainingSec, chest.rewardBoostDurationSec)
        }

        _uiState.update {
            it.copy(
                cash = it.cash + chest.rewardCash,
                totalCashEarned = it.totalCashEarned + chest.rewardCash,
                dailyMilestoneChests = updatedChests,
                globalMultiplier = newGlobalMultiplier,
                multiplierTimeRemainingSec = newMultiplierTime,
                feedbackMessage = "🎁 Coffre ${chest.title} ouvert : +${MoneyFormatter.format(chest.rewardCash)} !"
            )
        }
        saveCurrentGameState()
    }

    fun claimCurrentDailyReward() {
        val state = _uiState.value
        val currentReward = state.dailyRewards.find { it.isCurrentDay } ?: return
        if (!currentReward.isClaimed) {
            claimDailyLoginReward(currentReward.dayNumber)
        }
    }

    fun claimAllAvailableRewards() {
        val state = _uiState.value
        var totalEarned = 0.0
        var highestBoostMult = state.globalMultiplier
        var highestBoostSec = state.multiplierTimeRemainingSec

        // 1. Claim all ready missions
        val updatedMissions = state.dailyMissions.map { mission ->
            if (mission.isCompleted && !mission.isClaimed) {
                totalEarned += mission.rewardCash
                if (mission.rewardBoostMultiplier > 1.0 && mission.rewardBoostDurationSec > 0) {
                    highestBoostMult = max(highestBoostMult, mission.rewardBoostMultiplier)
                    highestBoostSec = max(highestBoostSec, mission.rewardBoostDurationSec)
                }
                mission.copy(isClaimed = true)
            } else mission
        }

        // Update chest unlocks
        val completedCount = updatedMissions.count { it.isCompleted || it.isClaimed }
        val unlockedChests = state.dailyMilestoneChests.map { chest ->
            if (completedCount >= chest.milestoneTarget) chest.copy(isUnlocked = true) else chest
        }

        // 2. Claim all ready chests
        val updatedChests = unlockedChests.map { chest ->
            if (chest.isUnlocked && !chest.isClaimed) {
                totalEarned += chest.rewardCash
                if (chest.rewardBoostMultiplier > 1.0 && chest.rewardBoostDurationSec > 0) {
                    highestBoostMult = max(highestBoostMult, chest.rewardBoostMultiplier)
                    highestBoostSec = max(highestBoostSec, chest.rewardBoostDurationSec)
                }
                chest.copy(isClaimed = true)
            } else chest
        }

        // 3. Claim daily login reward if claimable
        val updatedDailyRewards = state.dailyRewards.map { reward ->
            if (reward.isCurrentDay && !reward.isClaimed) {
                totalEarned += reward.cashReward
                if (reward.boostMultiplier > 1.0 && reward.boostDurationSec > 0) {
                    highestBoostMult = max(highestBoostMult, reward.boostMultiplier)
                    highestBoostSec = max(highestBoostSec, reward.boostDurationSec)
                }
                reward.copy(isClaimed = true)
            } else reward
        }

        if (totalEarned > 0.0) {
            triggerHapticFeedback(isStrong = true)
            SoundManager.playDailyReward()
            _uiState.update {
                it.copy(
                    cash = it.cash + totalEarned,
                    totalCashEarned = it.totalCashEarned + totalEarned,
                    dailyMissions = updatedMissions,
                    dailyMilestoneChests = updatedChests,
                    dailyRewards = updatedDailyRewards,
                    globalMultiplier = highestBoostMult,
                    multiplierTimeRemainingSec = highestBoostSec,
                    feedbackMessage = "🎉 Récompenses récupérées : +${MoneyFormatter.format(totalEarned)} !"
                )
            }
            saveCurrentGameState()
        }
    }

    fun navigateToMissionTarget(targetTab: Int) {
        _uiState.update {
            it.copy(
                selectedTab = targetTab,
                isDailyRewardsDialogOpen = false
            )
        }
    }

    fun openLeaderboard() {
        viewModelScope.launch {
            val currentCash = _uiState.value.totalCashEarned
            val rank = leaderboardRepository.getRankForScore(currentCash)
            _uiState.update {
                it.copy(
                    isLeaderboardOpen = true,
                    playerCurrentRank = rank
                )
            }
        }
    }

    fun closeLeaderboard() {
        _uiState.update { it.copy(isLeaderboardOpen = false) }
    }

    fun saveCurrentScoreToLeaderboard(notes: String = "") {
        val state = _uiState.value
        viewModelScope.launch {
            val avatar = GameRepository.getDefaultAvatars().find { it.id == state.selectedAvatarId }?.emoji ?: "💼"
            val noteText = notes.ifBlank { "Run P${state.prestigeLevel} • ${state.businesses.count { it.isUnlocked }} entreprises" }
            leaderboardRepository.savePlayerScore(
                playerName = state.playerName,
                avatarEmoji = avatar,
                totalCashEarned = state.totalCashEarned,
                peakRevenuePerSec = state.totalPassiveRevenuePerSec,
                prestigeLevel = state.prestigeLevel,
                businessesCount = state.businesses.count { it.isUnlocked },
                contractsSignedCount = state.totalTaps,
                notes = noteText
            )
            triggerHapticFeedback(isStrong = true)
            showFeedback("🏆 Score sauvegardé dans le classement Room !")
        }
    }

    fun deleteLeaderboardScore(id: Long) {
        viewModelScope.launch {
            leaderboardRepository.deleteScore(id)
            showFeedback("Score supprimé du classement local.")
        }
    }

    fun resetHistoricalLeaderboard() {
        viewModelScope.launch {
            leaderboardRepository.resetToDefaultHistoricalScores()
            showFeedback("Hall of Fame historique réinitialisé !")
        }
    }

    fun clearPlayerLeaderboardRuns() {
        viewModelScope.launch {
            leaderboardRepository.clearPlayerScores()
            showFeedback("Historique de tes runs effacé.")
        }
    }

    fun fetchOnlineLeaderboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOnlineSyncing = true) }
            val result = onlineLeaderboardService.fetchGlobalLeaderboard()
            val list = result.getOrDefault(onlineLeaderboardService.getSeedRealPlayers())
            _uiState.update {
                it.copy(
                    onlineScores = list,
                    isOnlineSyncing = false
                )
            }
        }
    }

    fun publishMyScoreToOnlineLeaderboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isOnlineSyncing = true) }
            val state = _uiState.value
            val avatar = GameRepository.getDefaultAvatars().find { it.id == state.selectedAvatarId }?.emoji ?: "💼"
            val onlineScore = OnlinePlayerScore(
                playerId = onlineLeaderboardService.myPlayerId,
                playerName = state.playerName.ifBlank { "CEO" },
                countryFlag = onlineLeaderboardService.getSavedCountryFlag(),
                avatarEmoji = avatar,
                netWorth = state.totalCashEarned * 1.5 + state.totalPassiveRevenuePerSec * 1000,
                totalCashEarned = state.totalCashEarned,
                peakRevenuePerSec = state.totalPassiveRevenuePerSec,
                prestigeLevel = state.prestigeLevel,
                businessesCount = state.businesses.count { it.isUnlocked },
                propertiesCount = state.luxuryAssets.count { it.isPurchased },
                contractsSignedCount = state.totalTaps,
                lastActiveTimestamp = System.currentTimeMillis(),
                playerTitle = if (state.prestigeLevel > 5) "Magnat Légendaire" else "Entrepreneur Actif",
                isVerifiedUser = true
            )
            val result = onlineLeaderboardService.publishPlayerScore(onlineScore)
            val list = result.getOrDefault(state.onlineScores)
            _uiState.update {
                it.copy(
                    onlineScores = list,
                    isOnlineSyncing = false
                )
            }
            showFeedback("🌍 Score publié avec succès sur le serveur mondial des vrais joueurs !")
        }
    }

    fun toggleBatterySaver() {
        val nextState = !_uiState.value.isBatterySaverEnabled
        _uiState.update { it.copy(isBatterySaverEnabled = nextState) }
        triggerHapticFeedback(isStrong = false)
        if (nextState) {
            showFeedback("🔋 Mode Éco activé : rendu 2Hz pour économiser la batterie")
        } else {
            showFeedback("⚡ Mode Éco désactivé : performances max (10Hz)")
        }
    }

    fun openAuctionDialog(lotId: String? = null) {
        _uiState.update { it.copy(isAuctionDialogOpen = true, activeAuctionLotId = lotId) }
    }

    fun closeAuctionDialog() {
        _uiState.update { it.copy(isAuctionDialogOpen = false, activeAuctionLotId = null) }
    }

    fun placeAuctionBid(lotId: String) {
        val state = _uiState.value
        val lot = state.auctionLots.find { it.id == lotId } ?: return
        if (lot.isWonByPlayer || lot.isExpired) return

        val bidAmount = lot.nextMinBid
        if (state.cash < bidAmount) {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(bidAmount)} requis !")
            return
        }

        val updatedAuctions = state.auctionLots.map {
            if (it.id == lotId) {
                it.copy(
                    currentBid = bidAmount,
                    isPlayerWinning = true,
                    highestBidderName = state.playerName,
                    timeRemainingSec = max(10, it.timeRemainingSec) // Give breathing room
                )
            } else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash - bidAmount,
                auctionLots = updatedAuctions
            )
        }
        triggerHapticFeedback(isStrong = true)
        showFeedback("Enchère posée : ${MoneyFormatter.format(bidAmount)} sur '${lot.title}' !")
        saveCurrentGameState()
    }

    fun buyoutAuctionLot(lotId: String) {
        val state = _uiState.value
        val lot = state.auctionLots.find { it.id == lotId } ?: return
        if (lot.isWonByPlayer || lot.isExpired) return
        val buyoutPrice = lot.instantBuyoutPrice

        if (state.cash < buyoutPrice) {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(buyoutPrice)} requis pour l'achat immédiat !")
            return
        }

        val updatedAuctions = state.auctionLots.map {
            if (it.id == lotId) {
                it.copy(
                    currentBid = buyoutPrice,
                    isPlayerWinning = true,
                    isWonByPlayer = true,
                    isExpired = true,
                    timeRemainingSec = 0,
                    highestBidderName = state.playerName
                )
            } else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash - buyoutPrice,
                auctionLots = updatedAuctions
            )
        }
        triggerHapticFeedback(isStrong = true)
        showFeedback("🏆 Achat Immédiat réussi : '${lot.title}' est à vous !")
        saveCurrentGameState()
    }

    fun resetAuctionLot(lotId: String) {
        val state = _uiState.value
        val lot = state.auctionLots.find { it.id == lotId } ?: return
        if (lot.isWonByPlayer) return

        val updatedAuctions = state.auctionLots.map {
            if (it.id == lotId) {
                it.copy(
                    currentBid = it.startingBid,
                    isPlayerWinning = false,
                    isExpired = false,
                    timeRemainingSec = 45,
                    highestBidderName = it.activeRivals.firstOrNull()?.name ?: "Enchérisseur Anonyme"
                )
            } else it
        }
        _uiState.update { it.copy(auctionLots = updatedAuctions) }
        showFeedback("Enchère relancée !")
    }

    fun acquireTakeoverStake(takeoverId: String) {
        val state = _uiState.value
        val takeover = state.corporateTakeovers.find { it.id == takeoverId } ?: return
        if (takeover.isFullyAcquired) {
            showFeedback("Vous détenez déjà 100% de ${takeover.name} !")
            return
        }

        val cost = takeover.nextStakeCost
        if (state.cash < cost) {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(cost)} requis pour acquérir 25% !")
            return
        }

        val newStake = (takeover.ownedStakePercentage + 25).coerceAtMost(100)
        val updatedTakeovers = state.corporateTakeovers.map {
            if (it.id == takeoverId) {
                it.copy(ownedStakePercentage = newStake)
            } else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash - cost,
                corporateTakeovers = updatedTakeovers
            )
        }
        triggerHapticFeedback(isStrong = true)
        val successMsg = if (newStake == 100) {
            "🚀 OPA Réussie ! 100% du contrôle conquis sur ${takeover.name} !"
        } else {
            "📈 Participation portée à $newStake% dans ${takeover.name} !"
        }
        showFeedback(successMsg)
        saveCurrentGameState()
    }

    fun unlockExpandedTech(techId: String) {
        val state = _uiState.value
        val tech = state.expandedTechNodes.find { it.id == techId } ?: return
        if (tech.isUnlocked) return

        if (tech.requiresTechId != null) {
            val prereq = state.expandedTechNodes.find { it.id == tech.requiresTechId }
            if (prereq == null || !prereq.isUnlocked) {
                showFeedback("Prérequis manquant : Débloquez d'abord '${prereq?.name ?: tech.requiresTechId}' !")
                return
            }
        }

        if (state.cash < tech.cost) {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(tech.cost)} requis pour la R&D !")
            return
        }

        val updatedTech = state.expandedTechNodes.map {
            if (it.id == techId) it.copy(isUnlocked = true) else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash - tech.cost,
                expandedTechNodes = updatedTech
            )
        }
        triggerHapticFeedback(isStrong = true)
        showFeedback("🔬 Technologie brevetée : ${tech.name} (${tech.bonusLabel}) !")
        saveCurrentGameState()
    }

    fun openRealEstateMarket() {
        _uiState.update { it.copy(isRealEstateMarketOpen = true) }
        SoundManager.playTap()
    }

    fun closeRealEstateMarket() {
        _uiState.update { it.copy(isRealEstateMarketOpen = false) }
        SoundManager.playTap()
    }

    fun purchaseLuxuryAsset(assetId: String) {
        val state = _uiState.value
        val asset = state.luxuryAssets.find { it.id == assetId } ?: return
        if (asset.isPurchased) {
            showFeedback("Résidence ou building déjà dans votre empire !")
            return
        }

        if (state.cash < asset.cost) {
            showFeedback("Fonds insuffisants : ${MoneyFormatter.format(asset.cost)} requis !")
            return
        }

        val updatedLuxury = state.luxuryAssets.map {
            if (it.id == assetId) it.copy(isPurchased = true) else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash - asset.cost,
                luxuryAssets = updatedLuxury
            )
        }
        triggerHapticFeedback(isStrong = true)
        SoundManager.playUpgrade()
        showFeedback("🏠 Félicitations ! Vous avez acquis '${asset.name}' (+${MoneyFormatter.formatPerSec(asset.effectiveRentRevenuePerSec)} loyers) !")
        saveCurrentGameState()
    }

    fun renovateLuxuryAsset(assetId: String) {
        val state = _uiState.value
        val asset = state.luxuryAssets.find { it.id == assetId } ?: return
        if (!asset.isPurchased) {
            showFeedback("Vous devez d'abord acheter cette propriété !")
            return
        }
        if (asset.renovationLevel >= 5) {
            showFeedback("Cette propriété est déjà rénovée au niveau maximal (Niv 5) !")
            return
        }

        val cost = asset.renovationCost
        if (state.cash < cost) {
            showFeedback("Fonds insuffisants pour la rénovation : ${MoneyFormatter.format(cost)} requis !")
            return
        }

        val updatedLuxury = state.luxuryAssets.map {
            if (it.id == assetId) it.copy(renovationLevel = it.renovationLevel + 1) else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash - cost,
                luxuryAssets = updatedLuxury
            )
        }
        triggerHapticFeedback(isStrong = true)
        SoundManager.playUpgrade()
        showFeedback("✨ Rénovation terminée sur '${asset.name}' ! Loyers augmentés à ${MoneyFormatter.formatPerSec(asset.effectiveRentRevenuePerSec * 1.25)}/sec !")
        saveCurrentGameState()
    }

    fun sellLuxuryAsset(assetId: String) {
        val state = _uiState.value
        val asset = state.luxuryAssets.find { it.id == assetId } ?: return
        if (!asset.isPurchased) return

        val resale = asset.resaleValue
        val updatedLuxury = state.luxuryAssets.map {
            if (it.id == assetId) it.copy(isPurchased = false, renovationLevel = 0) else it
        }

        _uiState.update {
            it.copy(
                cash = it.cash + resale,
                luxuryAssets = updatedLuxury
            )
        }
        triggerHapticFeedback(isStrong = true)
        SoundManager.playCollectRevenue()
        showFeedback("💰 Propriété '${asset.name}' vendue pour ${MoneyFormatter.format(resale)} !")
        saveCurrentGameState()
    }

    fun showFeedback(msg: String) {
        _uiState.update { it.copy(feedbackMessage = msg) }
        viewModelScope.launch {
            delay(3000)
            if (_uiState.value.feedbackMessage == msg) {
                _uiState.update { it.copy(feedbackMessage = null) }
            }
        }
    }
}
