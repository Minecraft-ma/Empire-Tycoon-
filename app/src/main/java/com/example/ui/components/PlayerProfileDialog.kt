package com.example.ui.components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import com.example.BuildConfig
import com.example.updater.UpdateManager
import com.example.updater.UpdateCheckState
import com.example.R
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.GameRepository
import com.example.model.Achievement
import com.example.model.MoneyFormatter
import com.example.model.PlayerAvatar
import com.example.model.PlayerRank
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.AmberText
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantBackground
import com.example.ui.theme.VibrantCardBorder
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import com.example.ui.theme.DesignSystem
import com.example.viewmodel.GameUiState

@Composable
fun PlayerProfileDialog(
    state: GameUiState,
    onDismiss: () -> Unit,
    onUpdateName: (String) -> Unit,
    onSelectAvatar: (Int) -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onClaimAchievement: (String) -> Unit,
    onExportSave: () -> String?,
    onImportSave: (String) -> Boolean,
    onResetGame: () -> Unit,
    onToggleBatterySaver: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(state.playerName) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importCodeInput by remember { mutableStateOf("") }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }
    val clipboardManager = LocalClipboardManager.current

    val currentRank = PlayerRank.getRankForNetWorth(state.netWorth)
    val nextRankThreshold = currentRank.nextRankThreshold
    val rankProgress = if (nextRankThreshold == Double.MAX_VALUE) 1f else {
        ((state.netWorth - currentRank.minNetWorth) / (nextRankThreshold - currentRank.minNetWorth)).toFloat().coerceIn(0f, 1f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .fillMaxHeight(0.82f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, VibrantCardBorder, RoundedCornerShape(20.dp))
                .testTag("player_profile_dialog"),
            color = VibrantBackground
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(VibrantSurface)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👑 PROFIL DU JOUEUR & DONNÉES",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_profile_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Tabs Navigation
                val tabTitles = listOf(
                    "Profil & Rang",
                    "Statistiques",
                    "Succès (${state.achievements.count { it.isUnlocked }}/${state.achievements.size})",
                    "Sauvegarde"
                )
                val tabIcons = listOf(
                    Icons.Default.Person,
                    Icons.Default.Assessment,
                    Icons.Default.EmojiEvents,
                    Icons.Default.Save
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = VibrantSurface,
                    contentColor = EmeraldDark,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = EmeraldPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Medium,
                                    color = if (selectedTab == index) EmeraldDark else TextSecondary
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = tabIcons[index],
                                    contentDescription = null,
                                    tint = if (selectedTab == index) EmeraldDark else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> ProfileAndRankTab(
                            state = state,
                            currentRank = currentRank,
                            rankProgress = rankProgress,
                            isEditingName = isEditingName,
                            nameInput = nameInput,
                            onNameInputChange = { nameInput = it },
                            onToggleEditName = {
                                if (isEditingName && nameInput.isNotBlank()) {
                                    onUpdateName(nameInput.trim())
                                }
                                isEditingName = !isEditingName
                            },
                            onSelectAvatar = onSelectAvatar,
                            onToggleSound = onToggleSound,
                            onToggleHaptics = onToggleHaptics,
                            onToggleBatterySaver = onToggleBatterySaver
                        )
                        1 -> CareerStatsTab(state = state)
                        2 -> AchievementsTab(
                            achievements = state.achievements,
                            onClaim = onClaimAchievement
                        )
                        3 -> SaveManagementTab(
                            state = state,
                            onExport = {
                                val code = onExportSave()
                                if (code != null) {
                                    clipboardManager.setText(AnnotatedString(code))
                                    exportSuccessMessage = "Code de sauvegarde copié dans le presse-papiers !"
                                }
                            },
                            onOpenImport = { showImportDialog = true },
                            onOpenReset = { showResetConfirm = true },
                            exportMessage = exportSuccessMessage
                        )
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = {
                Text("⚠️ Réinitialiser la partie ?", fontWeight = FontWeight.Black, color = CrimsonFrenzy)
            },
            text = {
                Text("Attention : Toutes tes entreprises, actions, cash et statistiques seront effacés et remis à zéro. Cette action est irréversible !")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        onResetGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonFrenzy)
                ) {
                    Text("Oui, Tout Réinitialiser", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            }
        )
    }

    // Import Save Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text("📥 Importer une Sauvegarde", fontWeight = FontWeight.Black, color = TextPrimary)
            },
            text = {
                Column {
                    Text("Colle ici ton code de sauvegarde exporté précédemment :", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importCodeInput,
                        onValueChange = { importCodeInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Code de sauvegarde...", fontSize = 12.sp) },
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = onImportSave(importCodeInput)
                        if (success) {
                            showImportDialog = false
                        }
                    },
                    enabled = importCodeInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Restaurer la Partie", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProfileAndRankTab(
    state: GameUiState,
    currentRank: PlayerRank,
    rankProgress: Float,
    isEditingName: Boolean,
    nameInput: String,
    onNameInputChange: (String) -> Unit,
    onToggleEditName: () -> Unit,
    onSelectAvatar: (Int) -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onToggleBatterySaver: () -> Unit
) {
    val avatars: List<PlayerAvatar> = GameRepository.getDefaultAvatars()
    val currentAvatar = avatars.find { it.id == state.selectedAvatarId } ?: avatars.first()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Player Identity Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, VibrantCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(AmberLight)
                                    .border(2.dp, AmberPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = currentAvatar.emoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                if (isEditingName) {
                                    OutlinedTextField(
                                        value = nameInput,
                                        onValueChange = onNameInputChange,
                                        singleLine = true,
                                        modifier = Modifier.width(180.dp),
                                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    )
                                } else {
                                    Text(
                                        text = state.playerName,
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Text(
                                    text = currentAvatar.title,
                                    color = IndigoPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        IconButton(
                            onClick = onToggleEditName,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isEditingName) Icons.Default.CheckCircle else Icons.Default.Edit,
                                contentDescription = "Éditer le nom",
                                tint = if (isEditingName) EmeraldPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Rank Progress Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, VibrantCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentRank.badgeEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "RANG : ${currentRank.title}",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = currentRank.subtitle,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Text(
                            text = "${(rankProgress * 100).toInt()}%",
                            color = AmberDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { rankProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AmberPrimary,
                        trackColor = VibrantSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fortune nette : ${MoneyFormatter.format(state.netWorth)}",
                            color = EmeraldDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (currentRank.nextRankThreshold == Double.MAX_VALUE) "Rang Max Atteint !" else "Objectif : ${MoneyFormatter.format(currentRank.nextRankThreshold)}",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // Avatar Selection Section Header
        item {
            Text(
                text = "CHOISIR UN AVATAR DE PDG",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        // Avatar Grid Rows
        items(avatars.chunked(4)) { avatarRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                avatarRow.forEach { av ->
                    val isUnlocked = state.prestigeLevel >= av.unlockedByPrestige
                    val isSelected = state.selectedAvatarId == av.id

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AmberLight else VibrantSurface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) AmberPrimary else VibrantCardBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = isUnlocked) { onSelectAvatar(av.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(text = av.emoji, fontSize = 22.sp)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                Text("P${av.unlockedByPrestige}", fontSize = 9.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Preferences (Sound / Haptic toggles)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, VibrantCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "PRÉFÉRENCES DE JEU",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Effets Sonores & Ambiances", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Switch(
                            checked = state.soundEnabled,
                            onCheckedChange = { onToggleSound() },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary, checkedTrackColor = EmeraldLight)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = AmberDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retours Haptiques (Vibrations Taps)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        Switch(
                            checked = state.hapticsEnabled,
                            onCheckedChange = { onToggleHaptics() },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmberPrimary, checkedTrackColor = AmberLight)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Power, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Économiseur Batterie (Bus Mode)", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("Optimise le rendu à 2Hz (économie CPU/GPU)", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                        Switch(
                            checked = state.isBatterySaverEnabled,
                            onCheckedChange = { onToggleBatterySaver() },
                            colors = SwitchDefaults.colors(checkedThumbColor = EmeraldPrimary, checkedTrackColor = EmeraldLight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CareerStatsTab(state: GameUiState) {
    val stats = state.careerStats
    val hours = stats.totalPlayTimeSeconds / 3600
    val minutes = (stats.totalPlayTimeSeconds % 3600) / 60
    val playTimeStr = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

    val metrics = listOf(
        Pair("Deals Signés (Taps)", "${state.totalTaps}"),
        Pair("Meilleur Combo Tapping", "${stats.highestCombo}x"),
        Pair("Revenus Publicitaires", MoneyFormatter.format(state.totalAdRevenueEarned)),
        Pair("Crises Gérées avec Succès", "${stats.totalCrisesResolved}"),
        Pair("Défis Sponsors Remportés", "${stats.totalMiniGamesWon}"),
        Pair("Transactions Bourse", "${stats.totalStockTrades}"),
        Pair("Ventes d'Empire (Prestige)", "★ P${state.prestigeLevel}"),
        Pair("Temps Passé en Jeu", playTimeStr),
        Pair("Cash Total Historique", MoneyFormatter.format(state.totalCashEarned)),
        Pair("Fortune Actuelle Nette", MoneyFormatter.format(state.netWorth))
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "TABLEAU DE BORD DE CARRIÈRE DU TYCOON",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        items(metrics.chunked(2)) { pairRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pairRow.forEach { (label, value) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(VibrantSurface, RoundedCornerShape(14.dp))
                            .border(1.dp, VibrantCardBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
                if (pairRow.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AchievementsTab(
    achievements: List<Achievement>,
    onClaim: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small),
        modifier = Modifier.fillMaxSize()
    ) {
        items(achievements, key = { it.id }) { ach ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (ach.isClaimed) VibrantSurface.copy(alpha = 0.8f) else VibrantSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (ach.isUnlocked && !ach.isClaimed) EmeraldPrimary else VibrantCardBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.cardInner),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(if (ach.isUnlocked) AmberLight else VibrantSurfaceVariant, CircleShape)
                                .border(1.dp, if (ach.isUnlocked) AmberPrimary else VibrantCardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = ach.iconEmoji, fontSize = 20.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = ach.title,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = ach.description,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { ach.progressFraction },
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = EmeraldPrimary,
                                trackColor = VibrantSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (ach.isClaimed) {
                        Box(
                            modifier = Modifier
                                .background(EmeraldLight, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("RÉCLAMÉ", color = EmeraldDark, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    } else if (ach.isUnlocked) {
                        Button(
                            onClick = { onClaim(ach.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Réclamer ${ach.rewardLabel}", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(VibrantSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(ach.rewardLabel, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SaveManagementTab(
    state: GameUiState,
    onExport: () -> Unit,
    onOpenImport: () -> Unit,
    onOpenReset: () -> Unit,
    exportMessage: String?
) {
    val context = LocalContext.current
    val updateState by UpdateManager.updateState.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // App Version & Auto-Update Management
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberPrimary.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0A1128))
                                .border(1.5.dp, AmberPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_empire_logo_minimal),
                                contentDescription = "Logo Empire Tycoon",
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "EMPIRE TYCOON",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AmberLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "v${BuildConfig.VERSION_NAME}",
                                        color = AmberDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = "Système d'auto-mise à jour (GitHub / itch.io)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Update Status / Feedback
                    when (val s = updateState) {
                        is UpdateCheckState.Checking -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = AmberPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Recherche de mise à jour en cours...",
                                    fontSize = 11.sp,
                                    color = AmberDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        is UpdateCheckState.UpToDate -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(EmeraldLight)
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "✓ Votre jeu est parfaitement à jour (v${s.currentVersion}) !",
                                    fontSize = 11.sp,
                                    color = EmeraldDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        is UpdateCheckState.UpdateAvailable -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AmberLight)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "⚡ Nouvelle version disponible : ${s.info.latestVersion}",
                                    fontSize = 11.sp,
                                    color = AmberDark,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { UpdateManager.launchUpdateDownload(context, s.info.apkDownloadUrl) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Télécharger l'APK (${s.info.latestVersion})", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        is UpdateCheckState.Error -> {
                            Text(
                                text = "⚠ ${s.message}",
                                fontSize = 11.sp,
                                color = CrimsonFrenzy,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        UpdateCheckState.Idle -> {
                            // Default idle state
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { UpdateManager.checkForUpdates(context, isManual = true) },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Vérifier MAJ", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = { UpdateManager.launchUpdateDownload(context, UpdateManager.RELEASES_URL) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantCardBorder),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Releases Web", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Auto Save Status
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldLight),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SAUVEGARDE LOCALE ACTIVE",
                            color = EmeraldDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Toutes tes actions, revenus et améliorations sont enregistrés automatiquement sur ton appareil.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Export Save
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, VibrantCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "EXPORTER MA PROGRESSION (CODE CLOUD)",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Génère une clé de transfert sécurisée pour transférer ta partie sur un autre appareil.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onExport,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copier le Code de Sauvegarde", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (exportMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = exportMessage, color = EmeraldDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Import Save
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, VibrantCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "IMPORTER UNE PARTIE EXISTANTE",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Restaure une sauvegarde avec ton code de progression.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onOpenImport,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = IndigoPrimary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Saisir un Code de Sauvegarde", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Danger Zone: Reset
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CrimsonFrenzy.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "ZONE DANGER : REMISE À ZÉRO",
                        color = CrimsonFrenzy,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Efface définitivement toute la progression et recommence à zéro.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onOpenReset,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonFrenzy),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonFrenzy),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Réinitialiser Complètement la Partie", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}
