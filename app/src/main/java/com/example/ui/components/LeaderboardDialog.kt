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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground.copy(alpha = 0.96f))
                .padding(horizontal = 12.dp, vertical = 18.dp)
                .testTag("leaderboard_dialog")
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.5.dp, AmberPrimary.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        Brush.linearGradient(listOf(AmberPrimary, AmberDark)),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "CLASSEMENT TYCOON",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Hall of Fame & Records Historiques (Room DB)",
                                    color = AmberDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("close_leaderboard_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fermer le classement",
                                tint = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Player Live Status Card
                    PlayerRankSummaryCard(
                        state = state,
                        currentRank = currentRank,
                        totalScoresCount = scores.size,
                        scores = scores,
                        onSaveCurrentScore = onSaveCurrentScore
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = DarkSurfaceVariant,
                        contentColor = AmberPrimary,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = AmberPrimary,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Leaderboard, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("TOP GLOBAL (${scores.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            },
                            selectedContentColor = AmberPrimary,
                            unselectedContentColor = TextSecondary
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("MES RUNS (${playerRuns.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            },
                            selectedContentColor = AmberPrimary,
                            unselectedContentColor = TextSecondary
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Insights, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("STATISTIQUES", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            },
                            selectedContentColor = AmberPrimary,
                            unselectedContentColor = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        when (selectedTab) {
                            0 -> GlobalLeaderboardList(
                                scores = scores,
                                currentPlayerCash = state.totalCashEarned,
                                onDeleteScore = onDeleteScore
                            )
                            1 -> PlayerRunsList(
                                playerRuns = playerRuns,
                                state = state,
                                onSaveCurrentScore = onSaveCurrentScore,
                                onDeleteScore = onDeleteScore,
                                onClearPlayerRuns = onClearPlayerRuns
                            )
                            2 -> LeaderboardStatsView(
                                state = state,
                                scores = scores,
                                playerRuns = playerRuns,
                                currentRank = currentRank,
                                onResetToHistorical = onResetToHistorical
                            )
                        }
                    }
                }
            }
        }
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
        in 4..5 -> "⭐ TOP 5 ÉLITE"
        in 6..10 -> "🏆 TOP 10 TYCOON"
        else -> "#$currentRank CLASSEMENT"
    }

    val rankColor = when (currentRank) {
        1 -> AmberPrimary
        2 -> CyberCyan
        3 -> EmeraldPrimary
        else -> TextSecondary
    }

    // Find next rival above current score
    val nextRival = scores.filter { it.totalCashEarned > state.totalCashEarned }.minByOrNull { it.totalCashEarned }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("player_rank_summary_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = BorderStroke(1.dp, rankColor.copy(alpha = 0.5f))
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
                        fontSize = 24.sp,
                        modifier = Modifier
                            .background(DarkSurface, CircleShape)
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
                            text = "Cash total : ${MoneyFormatter.format(state.totalCashEarned)} • P${state.prestigeLevel}",
                            color = AmberDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(rankColor.copy(alpha = 0.2f))
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

            // Rival Target Bar or Save Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (nextRival != null) {
                    val cashDiff = nextRival.totalCashEarned - state.totalCashEarned
                    Text(
                        text = "🎯 Prochain rival : ${nextRival.playerName} (+${MoneyFormatter.format(cashDiff)})",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "👑 Tu occupes la 1ère place du Hall of Fame mondial !",
                        color = AmberLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onSaveCurrentScore,
                    modifier = Modifier
                        .height(30.dp)
                        .testTag("save_score_snapshot_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ENREGISTRER", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun GlobalLeaderboardList(
    scores: List<LeaderboardScoreEntity>,
    currentPlayerCash: Double,
    onDeleteScore: (Long) -> Unit
) {
    if (scores.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aucun score enregistré dans la base de données locale.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("global_leaderboard_lazy_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            itemsIndexed(scores, key = { _, item -> item.id }) { index, item ->
                LeaderboardScoreCard(
                    rank = index + 1,
                    score = item,
                    isCurrentPlayerRun = item.isPlayerRun,
                    onDelete = if (item.isPlayerRun) { { onDeleteScore(item.id) } } else null
                )
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
    onClearPlayerRuns: () -> Unit
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
                color = TextSecondary,
                fontSize = 11.sp
            )

            if (playerRuns.isNotEmpty()) {
                Text(
                    text = "Effacer mes runs",
                    color = CrimsonFrenzy,
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
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onSaveCurrentScore,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
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
                    LeaderboardScoreCard(
                        rank = index + 1,
                        score = item,
                        isCurrentPlayerRun = true,
                        onDelete = { onDeleteScore(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardScoreCard(
    rank: Int,
    score: LeaderboardScoreEntity,
    isCurrentPlayerRun: Boolean,
    onDelete: (() -> Unit)? = null
) {
    val (rankEmoji, rankBgColor, rankBorderColor) = when (rank) {
        1 -> Triple("🥇", Color(0xFF332A15), AmberPrimary)
        2 -> Triple("🥈", Color(0xFF1E293B), CyberCyan)
        3 -> Triple("🥉", Color(0xFF281F1A), EmeraldPrimary)
        else -> Triple("#$rank", DarkSurfaceVariant, if (isCurrentPlayerRun) EmeraldPrimary.copy(alpha = 0.5f) else DarkCardBorder)
    }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val dateStr = remember(score.timestamp) { dateFormatter.format(Date(score.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_score_card_$rank"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = rankBgColor),
        border = BorderStroke(if (rank <= 3 || isCurrentPlayerRun) 1.5.dp else 1.dp, rankBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
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
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (rank <= 3) rankBorderColor.copy(alpha = 0.25f) else DarkSurface)
                        .border(1.dp, rankBorderColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rankEmoji,
                        fontSize = if (rank <= 3) 16.sp else 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = score.avatarEmoji, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = score.playerName,
                            color = if (isCurrentPlayerRun) AmberLight else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (isCurrentPlayerRun) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldPrimary.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("TOI", color = EmeraldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (score.notes.isNotBlank()) score.notes else "$dateStr • ${score.businessesCount} entreprises",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right: Cash Score & Prestige & Delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = MoneyFormatter.format(score.totalCashEarned),
                        color = AmberPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (score.prestigeLevel > 0) {
                            Text(
                                text = "★ P${score.prestigeLevel}",
                                color = CrimsonFrenzy,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "${MoneyFormatter.format(score.peakRevenuePerSec)}/s",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }

                if (onDelete != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardStatsView(
    state: GameUiState,
    scores: List<LeaderboardScoreEntity>,
    playerRuns: List<LeaderboardScoreEntity>,
    currentRank: Int,
    onResetToHistorical: () -> Unit
) {
    val bestRun = playerRuns.maxByOrNull { it.totalCashEarned }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("leaderboard_stats_column"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ANALYSE DE PERFORMANCE GLOBALE",
                        color = AmberDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    StatRow(label = "Rang mondial actuel", value = "#$currentRank / ${scores.size}")
                    StatRow(label = "Empire Actuel (Cash cumulé)", value = MoneyFormatter.format(state.totalCashEarned))
                    StatRow(label = "Meilleur Run archivé", value = bestRun?.let { MoneyFormatter.format(it.totalCashEarned) } ?: "Aucun")
                    StatRow(label = "Nombre de runs enregistrés", value = "${playerRuns.size} run(s)")
                    StatRow(label = "Niveau de Prestige Actuel", value = "★ P${state.prestigeLevel}")
                    StatRow(label = "Total Contrats Signés", value = "${state.totalTaps} taps")
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "GESTION DE LA BASE DE DONNÉES LOCALE (ROOM)",
                        color = AmberDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Toutes les performances sont persistées en SQLite local via Room Database pour comparer tes runs hors-ligne.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onResetToHistorical,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("reset_historical_legends_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface, contentColor = AmberPrimary),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AmberPrimary)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RÉINITIALISER LES TYCOONS HISTORIQUES", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp)
        Text(text = value, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
