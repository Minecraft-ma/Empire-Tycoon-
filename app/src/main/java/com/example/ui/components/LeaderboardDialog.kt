package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.LeaderboardScoreEntity
import com.example.data.online.OnlinePlayerScore
import com.example.model.MoneyFormatter
import com.example.viewmodel.GameUiState
import kotlinx.coroutines.delay

enum class LeaderboardTab(val label: String, val icon: String) {
    ALL("Tous", "🌍"),
    TOP10("Top 10", "👑"),
    NEAR_ME("Près de Vous", "🎯"),
    NATIONAL("National", "🇫🇷")
}

@Composable
fun LeaderboardDialog(
    state: GameUiState,
    scores: List<LeaderboardScoreEntity> = emptyList(),
    playerRuns: List<LeaderboardScoreEntity> = emptyList(),
    currentRank: Int = 1,
    onSaveCurrentScore: () -> Unit = {},
    onDeleteScore: (Long) -> Unit = {},
    onResetToHistorical: () -> Unit = {},
    onClearPlayerRuns: () -> Unit = {},
    onPublishOnlineScore: () -> Unit = {},
    onRefreshOnline: () -> Unit = {},
    onOpenAccountSetup: () -> Unit = {},
    onSignInGoogle: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(LeaderboardTab.ALL) }
    var inspectingPlayer by remember { mutableStateOf<OnlinePlayerScore?>(null) }

    // Direct auto-synchronization when opening dialog - no manual publishing needed!
    LaunchedEffect(Unit) {
        onRefreshOnline()
    }

    // Live World Events Ticker List
    val liveWorldEvents = remember(state.playerName, state.companyName) {
        listOf(
            "⚡ Synchronisation temps réel active avec Firebase Firestore",
            "🏢 ${state.companyName.ifBlank { "Mon Entreprise" }} progresse au classement mondial !",
            "🌍 Des millions de tycoons s'affrontent pour le contrôle des marchés",
            "💎 Chaque euro investi et chaque filiale renforcent votre rang mondial",
            "🔥 Classement multijoueur officiel en direct via Firebase Cloud"
        )
    }
    var currentLiveEventIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentLiveEventIndex = (currentLiveEventIndex + 1) % liveWorldEvents.size
        }
    }

    // Real Online Players sorted by net worth descending with current player merged in
    val allOnlinePlayers = remember(state.onlineScores, state.playerName, state.companyName, state.cash, state.totalCashEarned) {
        val myNetWorth = state.totalCashEarned * 1.5 + state.totalPassiveRevenuePerSec * 1000
        val myPlayer = OnlinePlayerScore(
            playerId = "my_current_player",
            playerName = state.playerName.ifBlank { "Player234" },
            companyName = state.companyName.ifBlank { "Mon Entreprise" },
            countryFlag = state.countryFlag.ifBlank { "🇫🇷" },
            avatarEmoji = state.avatarEmoji.ifBlank { "💼" },
            netWorth = myNetWorth,
            totalCashEarned = state.totalCashEarned,
            peakRevenuePerSec = state.totalPassiveRevenuePerSec,
            prestigeLevel = state.prestigeLevel,
            businessesCount = state.businesses.count { it.isUnlocked },
            propertiesCount = state.luxuryAssets.count { it.isPurchased },
            contractsSignedCount = state.totalTaps,
            lastActiveTimestamp = System.currentTimeMillis(),
            playerTitle = if (state.prestigeLevel >= 5) "Titan Suprême" else if (state.prestigeLevel >= 2) "Magnat de l'Industrie" else "Entrepreneur Ambitieux",
            isVerifiedUser = true
        )

        val list = state.onlineScores.filter { score ->
            !score.playerId.startsWith("bot_")
        }.toMutableList()

        val index = list.indexOfFirst {
            it.playerId == myPlayer.playerId || (it.playerName.isNotBlank() && it.playerName.equals(myPlayer.playerName, ignoreCase = true) && !it.playerId.startsWith("seed_"))
        }
        if (index >= 0) {
            list[index] = myPlayer
        } else {
            list.add(myPlayer)
        }
        list.sortedByDescending { it.netWorth.coerceAtLeast(it.totalCashEarned) }
    }

    val myCurrentRank = remember(allOnlinePlayers, state.playerName) {
        val idx = allOnlinePlayers.indexOfFirst { it.playerName.equals(state.playerName, ignoreCase = true) }
        if (idx >= 0) idx + 1 else 1
    }

    // Filter players based on tab and search
    val tabFilteredPlayers = remember(allOnlinePlayers, selectedTab, myCurrentRank, state.countryFlag) {
        when (selectedTab) {
            LeaderboardTab.ALL -> allOnlinePlayers
            LeaderboardTab.TOP10 -> allOnlinePlayers.take(10)
            LeaderboardTab.NEAR_ME -> {
                val centerIndex = (myCurrentRank - 1).coerceIn(0, (allOnlinePlayers.size - 1).coerceAtLeast(0))
                val startIndex = (centerIndex - 3).coerceAtLeast(0)
                val endIndex = (centerIndex + 4).coerceAtMost(allOnlinePlayers.size)
                allOnlinePlayers.subList(startIndex, endIndex)
            }
            LeaderboardTab.NATIONAL -> {
                val myFlag = state.countryFlag.ifBlank { "🇫🇷" }
                val nat = allOnlinePlayers.filter { it.countryFlag == myFlag }
                if (nat.isEmpty()) allOnlinePlayers else nat
            }
        }
    }

    val filteredPlayers = remember(tabFilteredPlayers, searchQuery) {
        if (searchQuery.isBlank()) {
            tabFilteredPlayers
        } else {
            val q = searchQuery.trim().lowercase()
            tabFilteredPlayers.filter {
                it.playerName.lowercase().contains(q) ||
                it.companyName.lowercase().contains(q) ||
                it.countryFlag.contains(q)
            }
        }
    }

    // Top 3 for Podium
    val top1 = allOnlinePlayers.getOrNull(0)
    val top2 = allOnlinePlayers.getOrNull(1)
    val top3 = allOnlinePlayers.getOrNull(2)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .testTag("leaderboard_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF070C1A)),
            border = BorderStroke(1.2.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // =========================================================================
                // 1. TOP HEADER: Title, Live Status Pill, Sync Loader, Close Button
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF22C55E), Color(0xFF15803D))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CLASSEMENT MONDIAL",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF14532D))
                                        .border(1.dp, Color(0xFF22C55E), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "🟢 EN DIRECT",
                                        color = Color(0xFF4ADE80),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = "Firebase Firestore • Synchronisation Temps Réel",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Refresh button
                        IconButton(
                            onClick = onRefreshOnline,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        ) {
                            if (state.isOnlineSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFF4ADE80),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualiser",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Close Button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                                .testTag("close_leaderboard_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // 2. LIVE TICKER BANNER
                // =========================================================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = liveWorldEvents[currentLiveEventIndex],
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // 3. MY PLAYER CARD & AUTO-SYNC STATUS (Direct Auto-Publishing)
                // =========================================================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF16233B)),
                    border = BorderStroke(1.2.dp, Color(0xFF22C55E))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                // Rank Badge
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                                        .border(1.5.dp, Color(0xFF22C55E), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#$myCurrentRank",
                                        color = Color(0xFF4ADE80),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Avatar + Flag
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF1E293B)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = state.avatarEmoji.ifBlank { "💼" }, fontSize = 20.sp)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = state.countryFlag.ifBlank { "🇫🇷" }, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = state.playerName.ifBlank { "Player234" },
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "• VOUS",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                    Text(
                                        text = "🏢 ${state.companyName.ifBlank { "Mon Entreprise" }}",
                                        color = Color(0xFF4ADE80),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = MoneyFormatter.format(state.totalCashEarned * 1.5 + state.totalPassiveRevenuePerSec * 1000),
                                    color = Color(0xFF4ADE80),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "+${MoneyFormatter.formatPerSec(state.totalPassiveRevenuePerSec)}/s",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status & Profile Customization Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (state.isOnlineSyncing) Color(0xFFFBBF24) else Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (state.isUserAuthenticated && !state.isUserAnonymous) "🔥 Google Auth • Firestore Direct" else if (state.isOnlineSyncing) "Synchronisation en cours..." else "⚡ Cloud Firestore Synchronisé",
                                    color = if (state.isOnlineSyncing) Color(0xFFFBBF24) else Color(0xFF4ADE80),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!state.isUserAuthenticated || state.isUserAnonymous) {
                                    Button(
                                        onClick = onSignInGoogle,
                                        modifier = Modifier.height(30.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("Google", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                Button(
                                    onClick = onOpenAccountSetup,
                                    modifier = Modifier.height(30.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Profil", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // 4. PODIUM (Top 3 Players)
                // =========================================================================
                if (top1 != null && top2 != null && top3 != null && searchQuery.isBlank() && selectedTab == LeaderboardTab.ALL) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 🥈 2nd Place Podium
                        PodiumPedestal(
                            player = top2,
                            rank = 2,
                            pedestalHeight = 78,
                            podiumColor = Color(0xFF94A3B8),
                            modifier = Modifier.weight(1f),
                            onClick = { inspectingPlayer = top2 }
                        )

                        // 🥇 1st Place Podium
                        PodiumPedestal(
                            player = top1,
                            rank = 1,
                            pedestalHeight = 96,
                            podiumColor = Color(0xFFFBBF24),
                            modifier = Modifier.weight(1.15f),
                            onClick = { inspectingPlayer = top1 }
                        )

                        // 🥉 3rd Place Podium
                        PodiumPedestal(
                            player = top3,
                            rank = 3,
                            pedestalHeight = 70,
                            podiumColor = Color(0xFFCD7F32),
                            modifier = Modifier.weight(1f),
                            onClick = { inspectingPlayer = top3 }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // =========================================================================
                // 5. FILTER TABS (Tous, Top 10, Près de Vous, National)
                // =========================================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LeaderboardTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF38BDF8) else Color(0xFF1E293B),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTab = tab }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${tab.icon} ${tab.label}",
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // =========================================================================
                // 6. SEARCH BAR
                // =========================================================================
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    placeholder = { Text("Rechercher un joueur, entreprise, pays...", color = Color(0xFF64748B), fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF131C31),
                        unfocusedContainerColor = Color(0xFF131C31),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // =========================================================================
                // 7. LIST OF PLAYERS
                // =========================================================================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(filteredPlayers) { index, player ->
                        val globalRank = allOnlinePlayers.indexOfFirst { it.playerId == player.playerId || it.playerName.equals(player.playerName, ignoreCase = true) } + 1
                        val displayRank = if (globalRank > 0) globalRank else (index + 1)
                        val isMe = player.playerName.equals(state.playerName, ignoreCase = true)
                        val rankColor = when (displayRank) {
                            1 -> Color(0xFFFBBF24) // Gold
                            2 -> Color(0xFF94A3B8) // Silver
                            3 -> Color(0xFFCD7F32) // Bronze
                            else -> Color(0xFF475569)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { inspectingPlayer = player },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isMe) Color(0xFF16233B) else Color(0xFF131C31)
                            ),
                            border = BorderStroke(
                                1.2.dp,
                                if (isMe) Color(0xFF22C55E) else if (displayRank <= 3) rankColor.copy(alpha = 0.7f) else Color(0xFF1E293B)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Rank Badge
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(rankColor.copy(alpha = 0.2f))
                                            .border(1.dp, rankColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (displayRank == 1) "🥇" else if (displayRank == 2) "🥈" else if (displayRank == 3) "🥉" else "#$displayRank",
                                            color = rankColor,
                                            fontSize = if (displayRank <= 3) 13.sp else 10.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(player.avatarEmoji, fontSize = 18.sp)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(player.countryFlag, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = player.playerName,
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (player.isVerifiedUser) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Verified,
                                                    contentDescription = "Vérifié",
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                            if (isMe) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "VOUS",
                                                    color = Color(0xFF4ADE80),
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }
                                        Text(
                                            text = "🏢 ${player.companyName}",
                                            color = Color(0xFF4ADE80),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = MoneyFormatter.format(player.netWorth.coerceAtLeast(player.totalCashEarned)),
                                        color = Color(0xFF4ADE80),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "+${MoneyFormatter.formatPerSec(player.peakRevenuePerSec)}/s",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Inspect Player Modal (Tycoon Dossier)
    inspectingPlayer?.let { player ->
        Dialog(onDismissRequest = { inspectingPlayer = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.2.dp, Color(0xFF334155))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(player.avatarEmoji, fontSize = 30.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(player.countryFlag, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = player.playerName,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (player.isVerifiedUser) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "🏢 ${player.companyName}",
                                    color = Color(0xFF4ADE80),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        IconButton(onClick = { inspectingPlayer = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = Color(0xFF94A3B8))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Fortune Totale :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text(MoneyFormatter.format(player.netWorth), color = Color(0xFF4ADE80), fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Revenus Passifs :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("+${MoneyFormatter.formatPerSec(player.peakRevenuePerSec)}/s", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Prestige de l'Empire :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("Niveau P${player.prestigeLevel}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Filiales en Exploitation :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("${player.businessesCount} entreprises", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Actifs Immobiliers & Luxe :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("${player.propertiesCount} propriétés", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Titre Honorifique :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text(player.playerTitle, color = Color(0xFFA855F7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Statut Réseau :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("🟢 En Ligne • Firebase Synchronisé", color = Color(0xFF4ADE80), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { inspectingPlayer = null },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Fermer le Dossier", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumPedestal(
    player: OnlinePlayerScore,
    rank: Int,
    pedestalHeight: Int,
    podiumColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Crown for 1st place
        if (rank == 1) {
            Text("👑", fontSize = 18.sp)
        } else {
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(1.5.dp, podiumColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(player.avatarEmoji, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Name + Flag
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(player.countryFlag, fontSize = 10.sp)
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = player.playerName,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Company
        Text(
            text = player.companyName,
            color = Color(0xFF94A3B8),
            fontSize = 8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Fortune
        Text(
            text = MoneyFormatter.format(player.netWorth),
            color = Color(0xFF4ADE80),
            fontSize = 9.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Pedestal base
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(podiumColor.copy(alpha = 0.4f), podiumColor.copy(alpha = 0.15f))
                    )
                )
                .border(1.dp, podiumColor.copy(alpha = 0.6f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (rank == 1) "🥇 1er" else if (rank == 2) "🥈 2e" else "🥉 3e",
                color = podiumColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
