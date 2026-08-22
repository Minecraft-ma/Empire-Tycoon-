package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.DailyLoginReward
import com.example.model.DailyMilestoneChest
import com.example.model.DailyMission
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DesignSystem
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun DailyRewardsAndQuestsDialog(
    isOpen: Boolean,
    dailyRewards: List<DailyLoginReward>,
    dailyMissions: List<DailyMission>,
    milestoneChests: List<DailyMilestoneChest>,
    dailyStreakDays: Int,
    timeUntilReset: String,
    onClaimReward: (dayNumber: Int) -> Unit,
    onClaimMission: (missionId: String) -> Unit,
    onClaimChest: (milestoneTarget: Int) -> Unit,
    onClaimAll: () -> Unit = {},
    onNavigateToTab: (tabIndex: Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val claimableLoginReward = dailyRewards.find { it.isCurrentDay && !it.isClaimed }
    val initialTab = if (claimableLoginReward != null) 1 else 0
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    val completedMissionsCount = dailyMissions.count { it.isCompleted && !it.isClaimed } + milestoneChests.count { it.isUnlocked && !it.isClaimed }
    val claimable7d = dailyRewards.count { it.isCurrentDay && !it.isClaimed }
    val totalClaimable = completedMissionsCount + claimable7d

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = DesignSystem.Spacing.extraSmall)
                .testTag("daily_rewards_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.2.dp, DarkCardBorder),
            shadowElevation = 14.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignSystem.Padding.cardInner)
            ) {
                // Header (Compact & Actionable)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFFB45309), Color(0xFFF59E0B))),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EventAvailable,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "RÉCOMPENSES & MISSIONS",
                                    color = AmberLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(Color(0xFF7C2D12), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Whatshot,
                                        contentDescription = null,
                                        tint = Color(0xFFFB923C),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "Série $dailyStreakDays j",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Reset dans $timeUntilReset",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (totalClaimable > 0) {
                            Button(
                                onClick = onClaimAll,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EmeraldPrimary,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("claim_all_rewards_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.DoneAll,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "TOUT ($totalClaimable)",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("close_daily_rewards_button")
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Fermer",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                // Tabs (Missions du Jour vs Cadeaux 7 Jours)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkSurfaceVariant,
                    contentColor = AmberPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AmberPrimary,
                            height = 2.5.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.testTag("tab_daily_missions"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Missions du Jour",
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 0) Color.White else TextSecondary
                                )
                                if (completedMissionsCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(EmeraldPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$completedMissionsCount",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.testTag("tab_daily_rewards"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Cadeaux 7 Jours",
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 1) Color.White else TextSecondary
                                )
                                if (claimable7d > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(AmberPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "!",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.small))

                if (selectedTab == 0) {
                    // TAB 0: Daily Missions & Milestone Chests
                    val totalDone = dailyMissions.count { it.isCompleted || it.isClaimed }
                    val totalMissions = dailyMissions.size.coerceAtLeast(1)

                    // Milestone Chests Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "COFFRES DE PALIER",
                                    color = AmberLight,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "$totalDone / $totalMissions complétées",
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (totalDone.toFloat() / totalMissions.toFloat()).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = AmberPrimary,
                                trackColor = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Chests list
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                milestoneChests.forEach { chest ->
                                    val isReady = chest.isUnlocked && !chest.isClaimed
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 2.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                when {
                                                    chest.isClaimed -> Color(0xFF064E3B)
                                                    isReady -> Color(0xFF78350F)
                                                    else -> Color(0xFF0F172A)
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                when {
                                                    chest.isClaimed -> EmeraldPrimary
                                                    isReady -> AmberPrimary
                                                    else -> DarkCardBorder
                                                },
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable(enabled = isReady) {
                                                onClaimChest(chest.milestoneTarget)
                                            }
                                            .padding(4.dp)
                                            .testTag("chest_${chest.milestoneTarget}"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "${chest.milestoneTarget} missions",
                                                fontSize = 8.sp,
                                                color = if (isReady) AmberLight else TextSecondary,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(text = chest.iconEmoji, fontSize = 14.sp)
                                            Text(
                                                text = when {
                                                    chest.isClaimed -> "OUVERT ✓"
                                                    isReady -> "OUVRIR !"
                                                    else -> chest.rewardLabel
                                                },
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                color = when {
                                                    chest.isClaimed -> EmeraldDark
                                                    isReady -> Color(0xFFFDE047)
                                                    else -> TextMuted
                                                },
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                    // Mission List
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.tiny)
                    ) {
                        items(dailyMissions, key = { it.id }) { mission ->
                            val isReadyToClaim = mission.isCompleted && !mission.isClaimed
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        mission.isClaimed -> Color(0xFF064E3B).copy(alpha = 0.3f)
                                        isReadyToClaim -> Color(0xFF78350F).copy(alpha = 0.4f)
                                        else -> DarkSurfaceVariant
                                    }
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        mission.isClaimed -> EmeraldPrimary.copy(alpha = 0.4f)
                                        isReadyToClaim -> AmberPrimary
                                        else -> DarkCardBorder
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(DesignSystem.Padding.cardCompact),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Text(text = mission.iconEmoji, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(DesignSystem.Spacing.tiny))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = mission.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(DesignSystem.Spacing.tiny))
                                                Box(
                                                    modifier = Modifier
                                                        .background(Color(mission.category.colorHex).copy(alpha = 0.25f), RoundedCornerShape(3.dp))
                                                        .padding(horizontal = 3.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = mission.category.displayName,
                                                        fontSize = 7.sp,
                                                        color = AmberLight,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Text(
                                                text = mission.description,
                                                fontSize = 9.sp,
                                                color = TextSecondary
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                val animatedProgress by animateFloatAsState(
                                                    targetValue = mission.progressFraction,
                                                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                                                    label = "mission_progress_${mission.id}"
                                                )
                                                LinearProgressIndicator(
                                                    progress = { animatedProgress },
                                                    modifier = Modifier
                                                        .width(65.dp)
                                                        .height(4.dp)
                                                        .clip(RoundedCornerShape(2.dp)),
                                                    color = if (mission.isCompleted) EmeraldPrimary else AmberPrimary,
                                                    trackColor = Color(0xFF0F172A)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${mission.currentProgress}/${mission.targetProgress}",
                                                    fontSize = 8.sp,
                                                    color = TextSecondary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "• +${MoneyFormatter.format(mission.rewardCash)}",
                                                    fontSize = 8.sp,
                                                    color = EmeraldLight,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    when {
                                        mission.isClaimed -> {
                                            Icon(
                                                Icons.Default.TaskAlt,
                                                contentDescription = "Réclamé",
                                                tint = EmeraldDark,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        isReadyToClaim -> {
                                            Button(
                                                onClick = { onClaimMission(mission.id) },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = EmeraldPrimary,
                                                    contentColor = Color.Black
                                                ),
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier
                                                    .height(26.dp)
                                                    .testTag("claim_mission_${mission.id}")
                                            ) {
                                                Text(
                                                    text = "RÉCLAMER",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }
                                        else -> {
                                            OutlinedButton(
                                                onClick = {
                                                    onNavigateToTab(mission.targetTab)
                                                },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                modifier = Modifier.height(24.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(text = "ALLER", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(8.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // TAB 1: 7-Days Login Rewards (Full & Interactive)
                    val currentDayReward = dailyRewards.find { it.isCurrentDay } ?: dailyRewards.firstOrNull()
                    val isCurrentDayClaimable = currentDayReward?.let { it.isCurrentDay && !it.isClaimed } ?: false

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RÉCOLTE QUOTIDIENNE VIP (CYCLE 7 JOURS)",
                            color = AmberDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Série: $dailyStreakDays jours",
                            color = AmberLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 7-day horizontal overview
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(dailyRewards, key = { it.dayNumber }) { item ->
                            val isItemClaimable = item.isCurrentDay && !item.isClaimed
                            val isItemPastUnclaimed = item.dayNumber < ((dailyStreakDays - 1) % 7 + 1) && !item.isClaimed
                            val canClick = isItemClaimable || isItemPastUnclaimed

                            Box(
                                modifier = Modifier
                                    .width(72.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            item.isClaimed -> Color(0xFF064E3B)
                                            isItemClaimable -> Color(0xFF78350F)
                                            item.isCurrentDay -> Color(0xFF1E293B)
                                            else -> DarkSurfaceVariant
                                        }
                                    )
                                    .border(
                                        width = if (item.isCurrentDay) 1.8.dp else 1.dp,
                                        color = when {
                                            item.isClaimed -> EmeraldPrimary
                                            isItemClaimable -> AmberPrimary
                                            item.isCurrentDay -> Color(0xFFF59E0B)
                                            else -> DarkCardBorder
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable(enabled = canClick) {
                                        onClaimReward(item.dayNumber)
                                    }
                                    .padding(6.dp)
                                    .testTag("daily_reward_item_${item.dayNumber}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Jour ${item.dayNumber}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isClaimed) EmeraldLight else if (item.isCurrentDay) AmberLight else TextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = item.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.rewardLabel,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (item.isClaimed) EmeraldDark else Color.White,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    when {
                                        item.isClaimed -> {
                                            Text("FAIT ✓", fontSize = 8.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                                        }
                                        isItemClaimable -> {
                                            Text("DISPO !", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color(0xFFFDE047))
                                        }
                                        item.isCurrentDay -> {
                                            Text("AUJOURD'HUI", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = AmberLight)
                                        }
                                        else -> {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(10.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.small))

                    // HERO CURRENT DAY REWARD CARD & ACTION BUTTON
                    if (currentDayReward != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("hero_daily_reward_card"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentDayClaimable) Color(0xFF1E293B) else Color(0xFF0F172A)
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isCurrentDayClaimable) AmberPrimary else DarkCardBorder
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(DesignSystem.Padding.cardInner),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = currentDayReward.iconEmoji, fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Cadeau du Jour ${currentDayReward.dayNumber}",
                                                fontWeight = FontWeight.Black,
                                                fontSize = 13.sp,
                                                color = Color.White
                                            )
                                            Text(
                                                text = currentDayReward.rewardLabel,
                                                fontSize = 11.sp,
                                                color = AmberLight,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (currentDayReward.boostMultiplier > 1.0) {
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF7C2D12), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Stars, contentDescription = null, tint = AmberLight, modifier = Modifier.size(10.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "+${((currentDayReward.boostMultiplier - 1.0) * 100).toInt()}% BOOST",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action Button
                                if (isCurrentDayClaimable) {
                                    Button(
                                        onClick = { onClaimReward(currentDayReward.dayNumber) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .testTag("claim_daily_login_reward_button"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EmeraldPrimary,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(text = "🎁", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "RÉCUPÉRER MA RÉCOMPENSE",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.3.sp
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(40.dp),
                                        color = Color(0xFF064E3B).copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(10.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.6f))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = EmeraldLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Récompense du jour récupérée ! Prochaine dans $timeUntilReset",
                                                color = EmeraldLight,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "💡 Connectez-vous chaque jour pour cumuler votre série et débloquer les boosts géants du Jour 7 !",
                        fontSize = 9.sp,
                        color = TextSecondary,
                        lineHeight = 12.sp
                    )
                }
            }
        }
    }
}
