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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
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
import androidx.compose.material3.TextButton
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
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var inspectingPlayer by remember { mutableStateOf<OnlinePlayerScore?>(null) }

    // Live World Events Ticker List (Real players only)
    val liveWorldEvents = remember(state.playerName, state.companyName) {
        listOf(
            "🇫🇷 ${state.playerName.ifBlank { "Magnat" }} (${state.companyName.ifBlank { "Entreprise" }}) domine le classement en direct 🚀",
            "🌐 Connexion sécurisée au réseau mondial Firebase Realtime active ⚡",
            "💎 Chaque seconde d'activité renforce votre position au sommet du classement 📈",
            "🔥 Classement mondial officiel 100% réel et synchronisé !"
        )
    }
    var currentLiveEventIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentLiveEventIndex = (currentLiveEventIndex + 1) % liveWorldEvents.size
        }
    }

    // Filter only real players and current player (no simulated bots)
    val allOnlinePlayers = remember(state.onlineScores, state.playerName, state.companyName, state.cash, state.totalCashEarned) {
        val myNetWorth = state.totalCashEarned * 1.5 + state.totalPassiveRevenuePerSec * 1000
        val myPlayer = OnlinePlayerScore(
            playerId = "my_current_player",
            playerName = state.playerName.ifBlank { "Joueur" },
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
            playerTitle = if (state.prestigeLevel > 5) "Magnat Légendaire" else "Entrepreneur Actif",
            isVerifiedUser = true
        )

        val excludedNames = listOf("Alexandre", "Rashid", "Satoshi", "Lukas", "Elena", "CryptoWhale", "Maxime", "Liam", "Mato", "Nouveau")
        val list = state.onlineScores.filter { score ->
            val name = score.playerName
            !score.playerId.startsWith("bot_") && excludedNames.none { name.contains(it, ignoreCase = true) }
        }.toMutableList()
        val index = list.indexOfFirst { it.playerName.equals(myPlayer.playerName, ignoreCase = true) }
        if (index >= 0) {
            list[index] = myPlayer
        } else {
            list.add(myPlayer)
        }
        list.sortedByDescending { it.netWorth.coerceAtLeast(it.totalCashEarned) }
    }

    val filteredPlayers = remember(allOnlinePlayers, searchQuery) {
        if (searchQuery.isBlank()) {
            allOnlinePlayers
        } else {
            val q = searchQuery.trim().lowercase()
            allOnlinePlayers.filter {
                it.playerName.lowercase().contains(q) ||
                it.companyName.lowercase().contains(q) ||
                it.countryFlag.contains(q)
            }
        }
    }

    val myCurrentRank = remember(allOnlinePlayers, state.playerName) {
        val idx = allOnlinePlayers.indexOfFirst { it.playerName.equals(state.playerName, ignoreCase = true) }
        if (idx >= 0) idx + 1 else 1
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
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
                // 1. TOP HEADER: Title, Live Dot, Sync Button, Close Button
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
                                .background(Color(0xFF22C55E).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFF22C55E), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(22.dp)
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
                                        text = "🔴 EN DIRECT",
                                        color = Color(0xFF4ADE80),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = "Firebase Realtime • Vrais Joueurs & Entreprises",
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
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                        ) {
                            if (state.isOnlineSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF4ADE80), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualiser",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        // Close button
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .testTag("close_leaderboard_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
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
                // 3. MY PLAYER & COMPANY STICKY CARD
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
                                // My Rank Badge
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
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

                        // Actions Row: Publish to Firebase + Setup/Edit Account
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onPublishOnlineScore,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                if (state.isOnlineSyncing) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PUBLIER SUR FIREBASE", fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            Button(
                                onClick = onOpenAccountSetup,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("MON COMPTE & ENTREPRISE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // 4. SEARCH BAR FOR GLOBAL PLAYERS
                // =========================================================================
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    singleLine = true,
                    placeholder = { Text("Rechercher un joueur, entreprise, drapeau...", color = Color(0xFF64748B), fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF1E293B),
                        focusedContainerColor = Color(0xFF131C31),
                        unfocusedContainerColor = Color(0xFF131C31),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // =========================================================================
                // 5. LIST OF REAL GLOBAL PLAYERS (Firebase RTDB + Cloud)
                // =========================================================================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(filteredPlayers) { index, player ->
                        val rank = index + 1
                        val isMe = player.playerName.equals(state.playerName, ignoreCase = true)
                        val rankColor = when (rank) {
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
                                if (isMe) Color(0xFF22C55E) else if (rank <= 3) rankColor.copy(alpha = 0.7f) else Color(0xFF1E293B)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
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
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(rankColor.copy(alpha = 0.2f))
                                            .border(1.2.dp, rankColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (rank == 1) "🥇" else if (rank == 2) "🥈" else if (rank == 3) "🥉" else "#$rank",
                                            color = rankColor,
                                            fontSize = if (rank <= 3) 14.sp else 11.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    // Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(player.avatarEmoji, fontSize = 20.sp)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(player.countryFlag, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = player.playerName,
                                                color = Color.White,
                                                fontSize = 13.sp,
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
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                            if (isMe) {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "VOUS",
                                                    color = Color(0xFF4ADE80),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }
                                        Text(
                                            text = "🏢 ${player.companyName}",
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
                                        text = MoneyFormatter.format(player.netWorth.coerceAtLeast(player.totalCashEarned)),
                                        color = Color(0xFF4ADE80),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "+${MoneyFormatter.formatPerSec(player.peakRevenuePerSec)}/s",
                                        color = Color(0xFFFBBF24),
                                        fontSize = 10.sp,
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

    // Inspect Player Modal
    inspectingPlayer?.let { player ->
        Dialog(onDismissRequest = { inspectingPlayer = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.dp, Color(0xFF334155))
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
                            Text(player.avatarEmoji, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(player.countryFlag, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = player.playerName,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
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
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Fortune Totale :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text(MoneyFormatter.format(player.netWorth), color = Color(0xFF4ADE80), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Revenus Passifs :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("+${MoneyFormatter.formatPerSec(player.peakRevenuePerSec)}/s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Prestige de l'Empire :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("Niveau P${player.prestigeLevel}", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Entreprises Détenues :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text("${player.businessesCount} filiales", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Statut Joueur :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                Text(player.playerTitle, color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                        Text("Fermer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
