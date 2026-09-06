package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.ui.platform.LocalContext
import com.example.ads.AdManager
import com.example.updater.UpdateManager
import com.example.ui.components.UpdateDialog
import com.example.ui.components.AccountSetupDialog
import com.example.ui.components.AuctionWarDialog
import com.example.ui.components.CrisisDialog
import com.example.ui.components.DailyRewardsAndQuestsDialog
import com.example.ui.components.InteractiveSponsorBanner
import com.example.ui.components.MarketNewsTickerBar
import com.example.ui.components.MiniGameHostDialog
import com.example.ui.components.LeaderboardDialog
import com.example.ui.components.OfflineEarningsDialog
import com.example.ui.components.PlayerProfileDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.SimulatedAdDialog
import com.example.ui.components.TopStatsBar
import com.example.ui.components.UpgradesStoreDialog
import com.example.ui.components.WheelOfFortuneDialog
import com.example.ui.screens.RealEstateMarketDialog
import com.example.ui.screens.ActionTappingScreen
import com.example.ui.screens.AdMonetizationScreen
import com.example.ui.screens.EmpireBusinessesScreen
import com.example.ui.screens.ExecutivePrestigeScreen
import com.example.ui.screens.ModernDashboardScreen
import com.example.ui.screens.TradingMarketScreen
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantBackground
import com.example.ui.theme.VibrantCardBorder
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.viewmodel.EmpireGameViewModel

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@Composable
fun EmpireApp(
    viewModel: EmpireGameViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val handleTriggerAd: (String, Double, String) -> Unit = { desc, bonus, action ->
        viewModel.triggerRewardedAd(desc, bonus, action)
    }

    val handleTriggerInterstitialAd: () -> Unit = {
        viewModel.onAdRewardEarned("INTERSTITIAL", 5000.0)
        viewModel.showFeedback("Campagne Flash Entreprise finalisée (+5 000 $ versés à la trésorerie) !")
    }

    LaunchedEffect(state.feedbackMessage) {
        state.feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = DarkBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White,
                    modifier = Modifier.padding(12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = data.visuals.message, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        bottomBar = {
            val infiniteTransition = rememberInfiniteTransition(label = "liquidBarAnim")
            val liquidOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 20f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "liquidOffset"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color(0xFF070C1A).copy(alpha = 0.98f), Color(0xFF050811))
                        )
                    )
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0F172A).copy(alpha = 0.98f),
                                    Color(0xFF1E293B).copy(alpha = 0.95f),
                                    Color(0xFF0F172A).copy(alpha = 0.98f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF22C55E).copy(alpha = 0.7f),
                                    Color(0xFF38BDF8).copy(alpha = 0.7f),
                                    Color(0xFF22C55E).copy(alpha = 0.7f)
                                )
                            ),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .clip(RoundedCornerShape(32.dp))
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("bottom_navigation_bar")
                    ) {
                        val tabs = listOf(
                            Triple(0, "Accueil", Icons.Default.FlashOn),
                            Triple(1, "Propriétés", Icons.Default.Business),
                            Triple(2, "Bourse", Icons.Default.ShowChart),
                            Triple(3, "Régie Pub", Icons.Default.MonetizationOn),
                            Triple(4, "Prestige", Icons.Default.WorkspacePremium)
                        )

                        tabs.forEach { (index, label, icon) ->
                            val isSelected = state.selectedTab == index
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.selectTab(index) },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .size(if (isSelected) 40.dp + liquidOffset.dp * 0.1f else 32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                brush = if (isSelected)
                                                    Brush.radialGradient(listOf(Color(0xFF22C55E).copy(alpha = 0.4f), Color(0xFF10B981).copy(alpha = 0.1f)))
                                                else
                                                    Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = label,
                                            tint = if (isSelected) Color(0xFF4ADE80) else Color(0xFF64748B),
                                            modifier = Modifier.size(if (isSelected) 22.dp else 18.dp)
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF4ADE80) else Color(0xFF64748B),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF4ADE80),
                                    selectedTextColor = Color(0xFF4ADE80),
                                    indicatorColor = Color.Transparent,
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("nav_tab_$index")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Active Screen Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (state.selectedTab) {
                    0 -> ModernDashboardScreen(
                        state = state,
                        onTap = { x, y -> viewModel.onTapAction(x, y) },
                        onTriggerRewardedAd = { desc, bonus, action -> handleTriggerAd(desc, bonus, action) },
                        onOpenWheel = { viewModel.openWheelDialog() },
                        onActivateAutoTapper = { viewModel.activateAutoTapper(30) },
                        onOpenUpgradesStore = { viewModel.openUpgradesStore() },
                        onUpgradeClickLevel = { viewModel.upgradeClickLevel() },
                        onTriggerAdBoost = { viewModel.triggerAdBoost() },
                        onOpenProfile = { viewModel.openProfileDialog() },
                        onOpenLeaderboard = { viewModel.openLeaderboard() },
                        onOpenRealEstateMarket = { viewModel.openRealEstateMarket() },
                        onOpenSettings = { viewModel.openSettingsDialog() },
                        onOpenAccountSetup = { viewModel.openAccountSetupDialog() },
                        onUpdatePlayerName = { viewModel.updatePlayerName(it) }
                    )
                    1 -> EmpireBusinessesScreen(
                        state = state,
                        onUpgradeBusiness = { viewModel.upgradeBusiness(it) },
                        onHireManager = { viewModel.hireManager(it) },
                        onUnlockBusiness = { viewModel.unlockBusiness(it) },
                        onRenameBusiness = { id, name -> viewModel.renameBusiness(id, name) },
                        onBuildingClick = { viewModel.collectManualBusiness(it) },
                        onPurchaseProperty = { viewModel.purchaseLuxuryAsset(it) },
                        onRenovateProperty = { viewModel.renovateLuxuryAsset(it) },
                        onSellProperty = { viewModel.sellLuxuryAsset(it) }
                    )
                    2 -> TradingMarketScreen(
                        state = state,
                        onBuyStock = { ticker, qty -> viewModel.buyStock(ticker, qty) },
                        onSellStock = { ticker, qty -> viewModel.sellStock(ticker, qty) },
                        onStakeCrypto = { ticker, qty -> viewModel.stakeCrypto(ticker, qty) },
                        onUnstakeCrypto = { ticker, qty -> viewModel.unstakeCrypto(ticker, qty) },
                        onPlaceAuctionBid = { viewModel.placeAuctionBid(it) },
                        onBuyoutAuctionLot = { viewModel.buyoutAuctionLot(it) },
                        onResetAuctionLot = { viewModel.resetAuctionLot(it) },
                        onAcquireTakeoverStake = { viewModel.acquireTakeoverStake(it) },
                        onOpenAuctionDialog = { viewModel.openAuctionDialog(it) }
                    )
                    3 -> AdMonetizationScreen(
                        state = state,
                        onUnlockAdNetwork = { viewModel.unlockAdNetwork(it) },
                        onUpgradeAdNetwork = { viewModel.upgradeAdNetwork(it) },
                        onLaunchAdCampaign = { viewModel.launchCompanyAdCampaign(it) },
                        onOpenSponsorDeal = { viewModel.openActiveSponsorMiniGame() },
                        onTriggerRewardedAd = { desc, bonus, action -> handleTriggerAd(desc, bonus, action) },
                        onTriggerInterstitialAd = handleTriggerInterstitialAd,
                        onSignSponsorshipContract = { viewModel.signSponsorshipContract(it) },
                        onRenewSponsorshipContract = { viewModel.renewSponsorshipContract(it) }
                    )
                    4 -> ExecutivePrestigeScreen(
                        state = state,
                        onHireExecutive = { viewModel.hireExecutive(it) },
                        onUnlockTech = { viewModel.unlockTechUpgrade(it) },
                        onUpgradeMegaproject = { viewModel.upgradeMegaproject(it) },
                        onPrestige = { viewModel.triggerPrestige() },
                        onOpenUpgradesStore = { viewModel.openUpgradesStore() },
                        onUnlockExpandedTech = { viewModel.unlockExpandedTech(it) },
                        onPurchaseLuxuryAsset = { viewModel.purchaseLuxuryAsset(it) }
                    )
                }
            }
        }

        // Active Interactive Mini Game Modal
        state.activeMiniGame?.let { miniGame ->
            MiniGameHostDialog(
                offer = miniGame,
                onComplete = { success, bonus, score, maxScore -> 
                    viewModel.completeMiniGame(success, bonus, score, maxScore) 
                },
                onDismiss = { viewModel.closeMiniGame() },
                onRegisterTap = { viewModel.registerMinigameTap() }
            )
        }

        // Active Crisis Strategic Event Modal
        state.activeCrisis?.let { crisis ->
            CrisisDialog(
                crisis = crisis,
                timeRemainingSec = state.crisisTimeRemainingSec,
                onChoiceSelected = { choiceIndex -> viewModel.resolveCrisis(choiceIndex) }
            )
        }

        // Player Profile & Career Stats Modal
        if (state.isProfileDialogOpen) {
            PlayerProfileDialog(
                state = state,
                onDismiss = { viewModel.closeProfileDialog() },
                onUpdateName = { viewModel.updatePlayerName(it) },
                onSelectAvatar = { viewModel.selectAvatar(it) },
                onToggleSound = { viewModel.toggleSound() },
                onToggleHaptics = { viewModel.toggleHaptics() },
                onClaimAchievement = { viewModel.claimAchievement(it) },
                onExportSave = { viewModel.exportSaveData() },
                onImportSave = { viewModel.importSaveData(it) },
                onResetGame = { viewModel.resetGameProgress() },
                onToggleBatterySaver = { viewModel.toggleBatterySaver() }
            )
        }

        // Daily Rewards and Quests Dialog
        if (state.isDailyRewardsDialogOpen) {
            DailyRewardsAndQuestsDialog(
                isOpen = state.isDailyRewardsDialogOpen,
                dailyRewards = state.dailyRewards,
                dailyMissions = state.dailyMissions,
                milestoneChests = state.dailyMilestoneChests,
                dailyStreakDays = state.dailyStreakDays,
                timeUntilReset = state.timeUntilDailyResetFormatted,
                onClaimReward = { viewModel.claimDailyLoginReward(it) },
                onClaimMission = { viewModel.claimDailyMission(it) },
                onClaimChest = { viewModel.claimDailyMilestoneChest(it) },
                onClaimAll = { viewModel.claimAllAvailableRewards() },
                onNavigateToTab = { viewModel.navigateToMissionTarget(it) },
                onDismiss = { viewModel.closeDailyRewardsDialog() }
            )
        }

        // Offline Passive Earnings Welcome Dialog
        state.offlineEarningsReport?.let { report ->
            OfflineEarningsDialog(
                report = report,
                onClaimStandard = { viewModel.claimOfflineEarnings(isDoubled = false) },
                onClaimDoubled = { handleTriggerAd("Doubler les gains hors-ligne !", report.earnedCash, "OFFLINE_DOUBLER") }
            )
        }

        // Simulated Rewarded Video Ad Modal
        if (state.isSimulatedAdOpen) {
            SimulatedAdDialog(
                rewardDescription = state.simulatedAdRewardDesc,
                rewardBonusCash = state.simulatedAdRewardBonusCash,
                onAdCompleted = { viewModel.completeSimulatedAd() },
                onDismiss = { viewModel.closeSimulatedAd() }
            )
        }

        // Upgrades Store Dialog
        if (state.isUpgradesStoreOpen) {
            UpgradesStoreDialog(
                state = state,
                onBuyUpgrade = { viewModel.buyProductivityUpgrade(it) },
                onBuyMaxUpgrade = { viewModel.buyMaxProductivityUpgrade(it) },
                onTriggerRewardedAd = { desc, bonus, action -> handleTriggerAd(desc, bonus, action) },
                onDismiss = { viewModel.closeUpgradesStore() }
            )
        }

        // Wheel of Fortune Casino Modal
        if (state.isWheelDialogOpen) {
            WheelOfFortuneDialog(
                isOpen = state.isWheelDialogOpen,
                isDailySpinAvailable = state.isDailySpinAvailable,
                timeUntilDailyResetFormatted = state.timeUntilDailyResetFormatted,
                onStartSpin = { viewModel.startDailyWheelSession() },
                onSpinReward = { cash, mult ->
                    viewModel.spinWheelReward(cash, mult)
                    viewModel.closeWheelDialog()
                },
                onWatchAdForSpin = {
                    viewModel.closeWheelDialog()
                    handleTriggerAd("Tirage Bonus Roue de la Fortune + Cash x2 !", state.cashPerTap * 300.0, "WHEEL_SPIN")
                },
                onDismiss = { viewModel.closeWheelDialog() }
            )
        }

        // Leaderboard & Hall of Fame Modal
        if (state.isLeaderboardOpen) {
            LeaderboardDialog(
                state = state,
                scores = state.leaderboardScores,
                playerRuns = state.playerRuns,
                currentRank = state.playerCurrentRank,
                onSaveCurrentScore = { viewModel.saveCurrentScoreToLeaderboard() },
                onDeleteScore = { viewModel.deleteLeaderboardScore(it) },
                onResetToHistorical = { viewModel.resetHistoricalLeaderboard() },
                onClearPlayerRuns = { viewModel.clearPlayerLeaderboardRuns() },
                onPublishOnlineScore = { viewModel.publishMyScoreToOnlineLeaderboard() },
                onRefreshOnline = { viewModel.fetchOnlineLeaderboard() },
                onOpenAccountSetup = { viewModel.openAccountSetupDialog() },
                onSignInGoogle = { viewModel.signInWithGoogle() },
                onSignOut = { viewModel.signOutAuth() },
                onDismiss = { viewModel.closeLeaderboard() }
            )
        }

        // Live Auction War Focused Modal
        if (state.isAuctionDialogOpen) {
            val activeLot = state.activeAuctionLotId?.let { id ->
                state.auctionLots.find { it.id == id }
            } ?: state.auctionLots.firstOrNull { !it.isExpired }
            AuctionWarDialog(
                isOpen = state.isAuctionDialogOpen,
                lot = activeLot,
                playerCash = state.cash,
                onPlaceBid = { viewModel.placeAuctionBid(it) },
                onBuyoutLot = { viewModel.buyoutAuctionLot(it) },
                onResetLot = { viewModel.resetAuctionLot(it) },
                onDismiss = { viewModel.closeAuctionDialog() }
            )
        }

        // Automatic In-App Update Dialog
        val activeUpdateInfo by UpdateManager.activeUpdateInfo.collectAsState()
        activeUpdateInfo?.let { updateInfo ->
            UpdateDialog(
                updateInfo = updateInfo,
                onDismiss = { UpdateManager.dismissUpdateDialog() }
            )
        }

        // Real Estate Market & Residences Modal (Empire Tycoon)
        if (state.isRealEstateMarketOpen) {
            RealEstateMarketDialog(
                isOpen = state.isRealEstateMarketOpen,
                state = state,
                onPurchaseProperty = { viewModel.purchaseLuxuryAsset(it) },
                onRenovateProperty = { viewModel.renovateLuxuryAsset(it) },
                onSellProperty = { viewModel.sellLuxuryAsset(it) },
                onDismiss = { viewModel.closeRealEstateMarket() }
            )
        }

        // Advanced Settings Dialog
        if (state.isSettingsDialogOpen) {
            SettingsDialog(
                state = state,
                onDismiss = { viewModel.closeSettingsDialog() },
                onOpenAccountSetup = { viewModel.openAccountSetupDialog() },
                onToggleSound = { viewModel.toggleSound() },
                onToggleHaptics = { viewModel.toggleHaptics() },
                onToggleBatterySaver = { viewModel.toggleBatterySaver() },
                onSyncCloud = { viewModel.fetchOnlineLeaderboard() },
                onExportSave = { viewModel.exportSaveData() },
                onImportSave = { viewModel.importSaveData(it) },
                onResetGame = { viewModel.resetGameProgress() }
            )
        }

        // Custom Account Setup Dialog with Firebase Auth & Cloud Sync
        if (state.isAccountSetupDialogOpen) {
            AccountSetupDialog(
                state = state,
                onDismiss = { viewModel.closeAccountSetupDialog() },
                onSignInGoogle = { viewModel.signInWithGoogle() },
                onSignOut = { viewModel.signOutAuth() },
                onSaveAccount = { name, company, flag, avatar ->
                    viewModel.savePlayerAccount(name, company, flag, avatar)
                }
            )
        }
    }
}
