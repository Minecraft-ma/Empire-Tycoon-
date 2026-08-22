package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExpandedTechNode
import com.example.model.MoneyFormatter
import com.example.model.TechBranch
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DesignSystem
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun TechTreeScreen(
    state: GameUiState,
    onUnlockTech: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBranch by remember { mutableStateOf<TechBranch?>(null) }

    val filteredNodes = remember(state.expandedTechNodes, selectedBranch) {
        if (selectedBranch == null) state.expandedTechNodes
        else state.expandedTechNodes.filter { it.branch == selectedBranch }
    }

    val unlockedCount = state.expandedTechNodes.count { it.isUnlocked }
    val totalCount = state.expandedTechNodes.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tech_tree_screen"),
        contentPadding = PaddingValues(DesignSystem.Padding.screenOuter),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
    ) {
        // Hero Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tech_tree_hero_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(CyberCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CENTRE R&D & BREVETS DE POINTE",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Progrès technologique global : $unlockedCount / $totalCount brevets débloqués",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Branch Filters
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedBranch == null,
                        onClick = { selectedBranch = null },
                        label = { Text("Tous les pôles (${state.expandedTechNodes.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedBranch == null,
                            borderColor = if (selectedBranch == null) CyberCyan else DarkCardBorder
                        )
                    )
                }
                items(TechBranch.values()) { branch ->
                    val count = state.expandedTechNodes.count { it.branch == branch }
                    FilterChip(
                        selected = selectedBranch == branch,
                        onClick = { selectedBranch = branch },
                        label = { Text("${branch.displayName} ($count)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberCyan.copy(alpha = 0.2f),
                            selectedLabelColor = CyberCyan,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedBranch == branch,
                            borderColor = if (selectedBranch == branch) CyberCyan else DarkCardBorder
                        )
                    )
                }
            }
        }

        // Tech Nodes List
        items(filteredNodes, key = { it.id }) { techNode ->
            val prereqNode = techNode.requiresTechId?.let { reqId ->
                state.expandedTechNodes.find { it.id == reqId }
            }
            val isPrereqMet = prereqNode == null || prereqNode.isUnlocked

            TechNodeCard(
                node = techNode,
                playerCash = state.cash,
                prereqNode = prereqNode,
                isPrereqMet = isPrereqMet,
                onUnlock = { onUnlockTech(techNode.id) }
            )
        }
    }
}

@Composable
fun TechNodeCard(
    node: ExpandedTechNode,
    playerCash: Double,
    prereqNode: ExpandedTechNode?,
    isPrereqMet: Boolean,
    onUnlock: () -> Unit
) {
    val isAffordable = playerCash >= node.cost && isPrereqMet && !node.isUnlocked

    val branchColor = when (node.branch) {
        TechBranch.AI_COMPUTING -> CyberCyan
        TechBranch.ENERGY_FUSION -> AmberDark
        TechBranch.BIO_NANOTECH -> EmeraldLight
        TechBranch.SPACE_MINING -> Color(0xFFC084FC) // Purple
    }

    val cardBorder = when {
        node.isUnlocked -> EmeraldDark
        isPrereqMet -> branchColor.copy(alpha = 0.6f)
        else -> DarkCardBorder
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tech_node_${node.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (node.isUnlocked) DarkSurface else DarkSurface.copy(alpha = 0.95f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Row: Emoji + Name + Tier/Branch Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = node.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = node.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${node.branch.displayName} • Niveau ${node.tier}",
                            color = branchColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status Badge
                if (node.isUnlocked) {
                    Box(
                        modifier = Modifier
                            .background(EmeraldDark.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .border(1.dp, EmeraldDark, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("BREVETÉ", color = EmeraldLight, fontWeight = FontWeight.Black, fontSize = 10.sp)
                        }
                    }
                } else if (!isPrereqMet) {
                    Box(
                        modifier = Modifier
                            .background(DarkSurfaceVariant, RoundedCornerShape(20.dp))
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VERROUILLÉ", color = TextMuted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = node.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Effect Booster Badge Box
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = branchColor, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = node.bonusLabel,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "+${(node.multiplierBoost * 100).toInt()}% Boost",
                        color = EmeraldLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            // Prerequisite Note if locked
            if (!node.isUnlocked && prereqNode != null && !isPrereqMet) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = AmberDark, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Nécessite le brevet préalable : ${prereqNode.name}",
                        color = AmberDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Action Button
            Spacer(modifier = Modifier.height(12.dp))
            if (node.isUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldDark.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ Brevet actif appliqué en continu",
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            } else {
                Button(
                    onClick = onUnlock,
                    enabled = isAffordable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = branchColor,
                        disabledContainerColor = DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unlock_tech_${node.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (!isPrereqMet) "Prérequis requis" else "Déposer le brevet",
                            color = if (isAffordable) DarkBackground else TextMuted,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = MoneyFormatter.format(node.cost),
                            color = if (isAffordable) DarkBackground else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
