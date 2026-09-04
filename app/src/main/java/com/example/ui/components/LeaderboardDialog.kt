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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.LeaderboardScoreEntity
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
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
import com.example.viewmodel.GameUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun LeaderboardDialog(
    state: GameUiState,
    scores: List<LeaderboardScoreEntity>,
    playerRuns: List<LeaderboardScoreEntity>,
    currentRank: Int,
    onSaveCurrentScore: () -> Unit,
    onDeleteScore: (Long) -> Unit,
    onResetToHistorical: () -> Unit,
    onClearPlayerRuns: () -> Unit,
    onPublishOnlineScore: () -> Unit = {},
    onRefreshOnline: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var inspectingPlayer by remember { mutableStateOf<LeaderboardScoreEntity?>(null) }
    var duelingPlayer by remember { mutableStateOf<LeaderboardScoreEntity?>(null) }
    var giftSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Live World Events Ticker List
    val liveWorldEvents = remember {
        listOf(
            "🇺🇸 CryptoWhale_99 vient d'acheter 2 500 actions Solarix 📈",
            "🇫🇷 Alexandre_CEO a étendu son empire à +5.8M$/sec 🚀",
            "🇯🇵 Satoshi & Tokyo Venture a remporté l'enchère Gratte-Ciel 🏙️",
            "🇦🇪 Sheikh Rashid a signé un méga-deal de 150M$ 💎",
            "🇧🇷 Silva_Amazonia a débloqué l'Énergie Verte 🌿",
            "🇩🇪 Hans_Capital Berlin a automatisé 7 usines 🏭",
            "🇰🇷 Seoul_K_Ventures a lancé un tournoi E-Sport mondial 🎮",
            "🇨🇭 Julien_Apprenti_Genève a dépassé les 100 000$ de capital ⌚",
            "👑 Nouveau record de saison enregistré par Bernard A. (Luxe) ✨"
        )
    }
    var currentLiveEventIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentLiveEventIndex = (currentLiveEventIndex + 1) % liveWorldEvents.size
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF070B18).copy(alpha = 0.98f))
                .padding(horizontal = 10.dp, vertical = 14.dp)
                .testTag("leaderboard_dialog")
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.2.dp, Color(0xFF334155)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    // =========================================================================
                    // 1. TOP HEADER: Title + Live Status Badge + Close Button
                    // =========================================================================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFF22C55E), Color(0xFF15803D))),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "CLASSEMENT MONDIAL",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Live Pulsing Green Dot
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF14532D))
                                            .border(1.dp, Color(0xFF22C55E), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "🟢 EN LIGNE (1 482)",
                                            color = Color(0xFF4ADE80),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                Text(
                                    text = "Serveur Mondial • Synchronisation des Joueurs en Direct",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

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
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // =========================================================================
                    // 2. LIVE WORLD ACTIVITY TICKER
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
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // =========================================================================
                    // 3. PLAYER LIVE RANK SUMMARY CARD
                    // =========================================================================
                    PlayerRankSummaryCard(
                        state = state,
                        currentRank = currentRank,
                        totalScoresCount = scores.size,
                        scores = scores,
                        onSaveCurrentScore = onSaveCurrentScore
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // =========================================================================
                    // 4. NAVIGATION TABS
                    // =========================================================================
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFF131C31),
                        contentColor = Color(0xFF4ADE80),
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = Color(0xFF4ADE80),
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("EN DIRECT (${scores.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            },
                            selectedContentColor = Color(0xFF4ADE80),
                            unselectedContentColor = Color(0xFF94A3B8)
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MilitaryTech, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("LIGUES & RANGS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            },
                            selectedContentColor = Color(0xFF4ADE80),
                            unselectedContentColor = Color(0xFF94A3B8)
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("MES RUNS (${playerRuns.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            },
                            selectedContentColor = Color(0xFF4ADE80),
                            unselectedContentColor = Color(0xFF94A3B8)
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("🌍 VRAIS JOUEURS (${state.onlineScores.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            },
                            selectedContentColor = Color(0xFF4ADE80),
                            unselectedContentColor = Color(0xFF94A3B8)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // =========================================================================
                    // 5. TAB CONTENT
                    // =========================================================================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        when (selectedTab) {
                            0 -> LiveGlobalLeaderboardList(
                                scores = scores,
                                currentPlayerCash = state.totalCashEarned,
                                onInspectPlayer = { inspectingPlayer = it },
                                onDuelPlayer = { duelingPlayer = it },
                                onDeleteScore = onDeleteScore
                            )
                            1 -> LeaguesAndTiersView(
                                currentCash = state.totalCashEarned,
                                currentRank = currentRank
                            )
                            2 -> PlayerRunsList(
                                playerRuns = playerRuns,
                                state = state,
                                onSaveCurrentScore = onSaveCurrentScore,
                                onDeleteScore = onDeleteScore,
                                onClearPlayerRuns = onClearPlayerRuns,
                                onResetToHistorical = onResetToHistorical
                            )
                            3 -> OnlineRealPlayersList(
                                onlineScores = state.onlineScores,
                                isSyncing = state.isOnlineSyncing,
                                onPublishScore = onPublishOnlineScore,
                                onRefresh = onRefreshOnline
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Inspecter le Joueur Rival
    inspectingPlayer?.let { rival ->
        PlayerInspectionDialog(
            rival = rival,
            onDismiss = { inspectingPlayer = null },
            onStartDuel = {
                inspectingPlayer = null
                duelingPlayer = rival
            },
            onSendGift = {
                giftSuccessMessage = "Pourboire de 500$ et félicitations envoyés à ${rival.playerName} ! Karma +1 ⭐"
            }
        )
    }

    // Modal Duel Commercial
    duelingPlayer?.let { rival ->
        CommercialDuelDialog(
            state = state,
            rival = rival,
            onDismiss = { duelingPlayer = null }
        )
    }

    // Message de confirmation Cadeau
    giftSuccessMessage?.let { msg ->
        AlertDialog(
            onDismissRequest = { giftSuccessMessage = null },
            title = { Text("Félicitations envoyées 🎁", fontWeight = FontWeight.Bold, color = Color.White) },
            text = { Text(msg, color = Color(0xFFE2E8F0)) },
            confirmButton = {
                TextButton(onClick = { giftSuccessMessage = null }) {
                    Text("OK", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun PlayerRankSummaryCard(
    state: GameUiState,
    currentRank: Int,
    totalScoresCount: Int,
    scores: List<LeaderboardScoreEntity>,
    onSaveCurrentScore: () -> Unit
) {
    val rankBadgeText = when (currentRank) {
        1 -> "🥇 1ER MONDIAL"
        2 -> "🥈 2ÈME TYCOON"
        3 -> "🥉 3ÈME PODIUM"
        in 4..10 -> "⭐ TOP 10 ÉLITE"
        in 11..25 -> "🏆 TOP 25 GLOBAL"
        else -> "#$currentRank CLASSEMENT"
    }

    val rankColor = when (currentRank) {
        1 -> Color(0xFFFBBF24)
        2 -> Color(0xFF38BDF8)
        3 -> Color(0xFF4ADE80)
        else -> Color(0xFF94A3B8)
    }

    val nextRival = scores.filter { it.totalCashEarned > state.totalCashEarned }.minByOrNull { it.totalCashEarned }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("player_rank_summary_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
        border = BorderStroke(1.dp, rankColor.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "💼",
                        fontSize = 22.sp,
                        modifier = Modifier
                            .background(Color(0xFF1E293B), CircleShape)
                            .padding(6.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = state.playerName.ifBlank { "Mon Empire" },
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Cash cumulé : ${MoneyFormatter.format(state.totalCashEarned)} • P${state.prestigeLevel}",
                            color = Color(0xFF4ADE80),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(rankColor.copy(alpha = 0.15f))
                        .border(1.dp, rankColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = rankBadgeText,
                        color = rankColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (nextRival != null) {
                    val cashDiff = nextRival.totalCashEarned - state.totalCashEarned
                    Text(
                        text = "🎯 Prochain rival : ${nextRival.playerName} (+${MoneyFormatter.format(cashDiff)})",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = "👑 Tu es le #1 mondial de Business Empire !",
                        color = Color(0xFFFBBF24),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onSaveCurrentScore,
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("save_score_snapshot_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SYNCHRONISER", fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveGlobalLeaderboardList(
    scores: List<LeaderboardScoreEntity>,
    currentPlayerCash: Double,
    onInspectPlayer: (LeaderboardScoreEntity) -> Unit,
    onDuelPlayer: (LeaderboardScoreEntity) -> Unit,
    onDeleteScore: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("global_leaderboard_lazy_list"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        itemsIndexed(scores, key = { _, item -> item.id }) { index, item ->
            LivePlayerCard(
                rank = index + 1,
                score = item,
                isCurrentPlayerRun = item.isPlayerRun,
                onInspect = { onInspectPlayer(item) },
                onDuel = { onDuelPlayer(item) },
                onDelete = if (item.isPlayerRun) { { onDeleteScore(item.id) } } else null
            )
        }
    }
}

@Composable
private fun LivePlayerCard(
    rank: Int,
    score: LeaderboardScoreEntity,
    isCurrentPlayerRun: Boolean,
    onInspect: () -> Unit,
    onDuel: () -> Unit,
    onDelete: (() -> Unit)? = null
) {
    val (rankBadge, rankBgColor, rankBorderColor) = when (rank) {
        1 -> Triple("🥇", Color(0xFF2E2210), Color(0xFFFBBF24))
        2 -> Triple("🥈", Color(0xFF132338), Color(0xFF38BDF8))
        3 -> Triple("🥉", Color(0xFF142B1F), Color(0xFF4ADE80))
        else -> Triple("#$rank", Color(0xFF131C31), if (isCurrentPlayerRun) Color(0xFF22C55E).copy(alpha = 0.6f) else Color(0xFF1E293B))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInspect() }
            .testTag("leaderboard_score_card_$rank"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = rankBgColor),
        border = BorderStroke(if (rank <= 3 || isCurrentPlayerRun) 1.2.dp else 1.dp, rankBorderColor)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Rank & Avatar & Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (rank <= 3) rankBorderColor.copy(alpha = 0.25f) else Color(0xFF0F172A))
                            .border(1.dp, rankBorderColor, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rankBadge,
                            fontSize = if (rank <= 3) 15.sp else 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = score.avatarEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = score.playerName,
                                color = if (isCurrentPlayerRun) Color(0xFF4ADE80) else Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isCurrentPlayerRun) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF22C55E).copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("TOI", color = Color(0xFF4ADE80), fontSize = 8.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        Text(
                            text = if (score.notes.isNotBlank()) score.notes else "Entreprises: ${score.businessesCount} • Revenu: ${MoneyFormatter.format(score.peakRevenuePerSec)}/s",
                            color = Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right: Cash Score & Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = MoneyFormatter.format(score.totalCashEarned),
                            color = Color(0xFF4ADE80),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        if (score.prestigeLevel > 0) {
                            Text(
                                text = "★ Prestige ${score.prestigeLevel}",
                                color = Color(0xFFFBBF24),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (!isCurrentPlayerRun) {
                        Spacer(modifier = Modifier.width(6.dp))
                        // Bouton Duel Rapide
                        IconButton(
                            onClick = onDuel,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsKabaddi,
                                contentDescription = "Défier en duel",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    if (onDelete != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Supprimer",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaguesAndTiersView(
    currentCash: Double,
    currentRank: Int
) {
    val leagues = listOf(
        Triple("👑 LIGUE MAÎTRE TYCOON", "Top 3 Mondial • Cash > 100 Milliards", Color(0xFFFBBF24)),
        Triple("💎 LIGUE DIAMANT ÉLITE", "Top 10 Mondial • Cash > 1 Milliard", Color(0xFF38BDF8)),
        Triple("⭐ LIGUE PLATINE", "Top 25 Mondial • Cash > 100 Millions", Color(0xFFC084FC)),
        Triple("🥇 LIGUE OR", "Top 50 Mondial • Cash > 10 Millions", Color(0xFFF59E0B)),
        Triple("🥈 LIGUE ARGENT", "Top 100 Mondial • Cash > 1 Million", Color(0xFF94A3B8)),
        Triple("🥉 LIGUE BRONZE", "Nouveaux Tycoons • Cash < 1 Million", Color(0xFFCD7F32))
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
                border = BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🏆 SYSTÈME DE LIGUES MONDIALES",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Les récompenses de saison sont distribuées chaque dimanche à minuit selon ton rang final.",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }
        }

        itemsIndexed(leagues) { _, (title, desc, color) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
                border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f))
                            .border(1.dp, color, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = color,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = title,
                            color = color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = desc,
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerRunsList(
    playerRuns: List<LeaderboardScoreEntity>,
    state: GameUiState,
    onSaveCurrentScore: () -> Unit,
    onDeleteScore: (Long) -> Unit,
    onClearPlayerRuns: () -> Unit,
    onResetToHistorical: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historique de tes runs & Prestiges",
                color = Color(0xFF94A3B8),
                fontSize = 11.sp
            )

            if (playerRuns.isNotEmpty()) {
                Text(
                    text = "Effacer mes runs",
                    color = Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onClearPlayerRuns() }
                        .padding(4.dp)
                )
            }
        }

        if (playerRuns.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tu n'as pas encore archivé de run de jeu.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onSaveCurrentScore,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E), contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enregistrer mon empire actuel", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("player_runs_lazy_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(playerRuns, key = { _, item -> item.id }) { index, item ->
                    LivePlayerCard(
                        rank = index + 1,
                        score = item,
                        isCurrentPlayerRun = true,
                        onInspect = {},
                        onDuel = {},
                        onDelete = { onDeleteScore(item.id) }
                    )
                }
            }
        }
    }
}

/**
 * Modal d'inspection détaillée du joueur rival
 */
@Composable
private fun PlayerInspectionDialog(
    rival: LeaderboardScoreEntity,
    onDismiss: () -> Unit,
    onStartDuel: () -> Unit,
    onSendGift: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = rival.avatarEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = rival.playerName,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Profil Joueur Vérifié • 🟢 En ligne",
                        color = Color(0xFF4ADE80),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Valeur Nette :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text(MoneyFormatter.format(rival.totalCashEarned), color = Color(0xFF4ADE80), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Revenu Passif :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("${MoneyFormatter.format(rival.peakRevenuePerSec)}/s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Niveau Prestige :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("★ P${rival.prestigeLevel}", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Entreprises Actives :", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            Text("${rival.businessesCount} filiales", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = rival.notes,
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSendGift,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Envoyer Tip", fontSize = 10.sp)
                    }

                    Button(
                        onClick = onStartDuel,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.SportsKabaddi, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Défier", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Modal Duel Commercial Rapide (Mini-jeu de négociation en 5 secondes)
 */
@Composable
private fun CommercialDuelDialog(
    state: GameUiState,
    rival: LeaderboardScoreEntity,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var isStarted by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var playerTaps by remember { mutableIntStateOf(0) }
    var rivalTaps by remember { mutableIntStateOf(0) }
    var timeLeftSec by remember { mutableIntStateOf(5) }
    var isPlayerWinner by remember { mutableStateOf(false) }
    val rewardBonus = remember { (state.cashPerTap * 50.0).coerceAtLeast(1000.0) }

    LaunchedEffect(isStarted) {
        if (isStarted && !isFinished) {
            while (timeLeftSec > 0) {
                delay(1000)
                timeLeftSec -= 1
                // Simulated rival taps (between 3 to 6 taps per sec)
                rivalTaps += Random.nextInt(3, 7)
            }
            isFinished = true
            isPlayerWinner = playerTaps >= rivalTaps
            if (isPlayerWinner) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SportsKabaddi, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("DUEL COMMERCIAL EXPRESS", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Défie ${rival.playerName} dans un duel de négociation rapide de 5 secondes ! Tape le plus vite possible pour remporter le contrat.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                if (!isStarted && !isFinished) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Récompense de Victoire :", color = Color(0xFF94A3B8), fontSize = 10.sp)
                            Text("+${MoneyFormatter.format(rewardBonus)}", color = Color(0xFF4ADE80), fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Button(
                        onClick = { isStarted = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("COMMENCER LE DUEL (5s)", fontWeight = FontWeight.Black)
                    }
                } else if (isStarted && !isFinished) {
                    Text(
                        text = "⏱️ $timeLeftSec s",
                        color = Color(0xFFFBBF24),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("TOI", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("$playerTaps taps", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(rival.playerName, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("$rivalTaps taps", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    // Gros Bouton de Tap de Négociation
                    Button(
                        onClick = {
                            playerTaps += 1
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("TAP POUR NÉGOCIER !", fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                } else if (isFinished) {
                    if (isPlayerWinner) {
                        Text(
                            text = "🎉 VICTOIRE ÉCRASANTE !",
                            color = Color(0xFF4ADE80),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Tu as remporté le contrat face à ${rival.playerName} !\nGain : +${MoneyFormatter.format(rewardBonus)}",
                            color = Color.White,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = "❌ DÉFAITE COMMERCIALE",
                            color = Color(0xFFEF4444),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${rival.playerName} a été plus rapide avec $rivalTaps taps contre tes $playerTaps taps. Réessaye au prochain round !",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isFinished) "Terminer" else "Abandonner", color = Color(0xFF94A3B8))
            }
        },
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun OnlineRealPlayersList(
    onlineScores: List<com.example.data.online.OnlinePlayerScore>,
    isSyncing: Boolean,
    onPublishScore: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Action Bar for Online Sync
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onPublishScore,
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("PUBLIER MON SCORE", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }

            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Actualiser",
                    tint = if (isSyncing) Color(0xFF4ADE80) else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = "Classement en direct de tous les vrais joueurs de l'application à travers le monde :",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(onlineScores) { index, player ->
                val rank = index + 1
                val rankColor = when (rank) {
                    1 -> Color(0xFFFBBF24) // Gold
                    2 -> Color(0xFF94A3B8) // Silver
                    3 -> Color(0xFFB45309) // Bronze
                    else -> Color(0xFF64748B)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
                    border = BorderStroke(1.dp, if (rank <= 3) rankColor.copy(alpha = 0.6f) else Color(0xFF1E293B))
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
                                    .background(rankColor.copy(alpha = 0.2f), CircleShape)
                                    .border(1.dp, rankColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#$rank",
                                    color = rankColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Avatar + Country
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(Color(0xFF1E293B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(player.avatarEmoji, fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(player.countryFlag, fontSize = 14.sp)
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
                                            imageVector = Icons.Default.WorkspacePremium,
                                            contentDescription = "Vérifié",
                                            tint = Color(0xFF38BDF8),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${player.playerTitle} • Prestige P${player.prestigeLevel}",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
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
                                text = "⚡ ${MoneyFormatter.formatPerSec(player.peakRevenuePerSec)}/s",
                                color = Color(0xFFFBBF24),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
