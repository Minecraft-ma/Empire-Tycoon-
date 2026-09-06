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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Executive
import com.example.model.MoneyFormatter
import com.example.ui.theme.DesignSystem
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
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

@Composable
fun ExecutivePrestigeScreen(
    state: GameUiState,
    onHireExecutive: (id: String) -> Unit,
    onUnlockTech: (id: String) -> Unit = {},
    onUpgradeMegaproject: (id: String) -> Unit = {},
    onPrestige: () -> Unit,
    onOpenUpgradesStore: () -> Unit = {},
    onUnlockExpandedTech: (id: String) -> Unit = {},
    onPurchaseLuxuryAsset: (id: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("executive_prestige_root")
    ) {
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = DarkSurface,
            contentColor = AmberDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = AmberDark,
                    height = 3.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Direction", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selectedContentColor = AmberDark,
                unselectedContentColor = TextSecondary
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("R&D (${state.expandedTechNodes.count { it.isUnlocked }}/${state.expandedTechNodes.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selectedContentColor = AmberDark,
                unselectedContentColor = TextSecondary
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Diamond, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Luxe (${state.luxuryAssets.count { it.isPurchased }})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selectedContentColor = AmberDark,
                unselectedContentColor = TextSecondary
            )
        }

        when (selectedSubTab) {
            1 -> TechTreeScreen(
                state = state,
                onUnlockTech = onUnlockExpandedTech
            )
            2 -> LuxuryMarketScreen(
                state = state,
                onPurchaseAsset = onPurchaseLuxuryAsset
            )
            else -> {
                val nextPrestigeCost = state.nextPrestigeThreshold
                val canPrestige = state.canPrestige

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("executive_prestige_list"),
                    contentPadding = PaddingValues(DesignSystem.Padding.screenOuter),
                    verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small)
                ) {
                    // Quick Access Banner to Productivity Multipliers Store
                    item {
                        Card(
                            onClick = onOpenUpgradesStore,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("direction_upgrades_store_banner"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary)
                        ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(AmberPrimary.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, AmberPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚡", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(DesignSystem.Spacing.small))
                        Column {
                            Text(
                                text = "MAGASIN D'AMÉLIORATIONS",
                                color = AmberPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "16 multiplicateurs permanents",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = onOpenUpgradesStore,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(text = "OUVRIR", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Prestige & Angel Investor Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("prestige_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.cardCompact)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "VENTE DE L'EMPIRE & PRESTIGE",
                                color = AmberDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Niveau de Prestige : ★ P${state.prestigeLevel} (+${((state.prestigeBonusMultiplier - 1.0) * 100).toInt()}%)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(DesignSystem.Spacing.extraSmall))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF78350F), CircleShape)
                                .border(1.dp, AmberPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = AmberLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.micro))

                    Text(
                        text = "Vends ton empire actuel à un fonds souverain pour un bonus permanent de +50% de production sur toute la partie !",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                    Button(
                        onClick = onPrestige,
                        enabled = canPrestige,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("trigger_prestige_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CrimsonFrenzy,
                            contentColor = Color.White,
                            disabledContainerColor = DarkSurfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        val progressPercent = ((state.totalCashEarned / nextPrestigeCost) * 100).toInt().coerceIn(0, 100)
                        Text(
                            text = if (canPrestige) "VENDRE ET PASSER AU PRESTIGE P${state.prestigeLevel + 1} (+50%)" else "Requis: ${MoneyFormatter.format(nextPrestigeCost)} cumulés ($progressPercent%)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // R&D Tech Lab Header
        item {
            Text(
                text = "LABORATOIRE R&D & TECHNOLOGIES",
                color = AmberDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }

        items(state.techUpgrades, key = { it.id }) { tech ->
            val canAfford = state.cash >= tech.cost
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (tech.isUnlocked) EmeraldPrimary else DarkCardBorder
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
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = tech.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(DesignSystem.Spacing.small))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tech.name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = tech.description,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.extraSmall))

                    if (tech.isUnlocked) {
                        Text(
                            text = "ACTIF",
                            color = EmeraldDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    } else {
                        Button(
                            onClick = { onUnlockTech(tech.id) },
                            enabled = canAfford,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyan,
                                contentColor = Color.Black,
                                disabledContainerColor = DarkSurfaceVariant,
                                disabledContentColor = TextMuted
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = MoneyFormatter.format(tech.cost),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "MÉGAPROJETS D'INVESTISSEMENT METROPOLIS",
                color = AmberDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }

        items(state.megaprojects, key = { it.id }) { mega ->
            val canAfford = state.cash >= mega.currentCost
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("megaproject_card_${mega.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (mega.isMaxed) EmeraldPrimary else DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.cardCompact),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = mega.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(DesignSystem.Spacing.small))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mega.name,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(DesignSystem.Spacing.extraSmall))
                                Box(
                                    modifier = Modifier
                                        .background(AmberPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "NIV. ${mega.stage}/${mega.maxStage}",
                                        color = AmberPrimary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1
                                    )
                                }
                            }
                            Text(
                                text = mega.description,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.extraSmall))

                    if (mega.isMaxed) {
                        Text(
                            text = "COMPLÉTÉ",
                            color = EmeraldDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    } else {
                        Button(
                            onClick = { onUpgradeMegaproject(mega.id) },
                            enabled = canAfford,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberPrimary,
                                contentColor = Color.Black,
                                disabledContainerColor = DarkSurfaceVariant,
                                disabledContentColor = TextMuted
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = MoneyFormatter.format(mega.currentCost),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "CONSEIL D'ADMINISTRATION & C-SUITE",
                color = AmberDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }

                    items(state.executives, key = { it.id }) { exec ->
                        ExecutiveCard(
                            exec = exec,
                            playerCash = state.cash,
                            onHire = { onHireExecutive(exec.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExecutiveCard(
    exec: Executive,
    playerCash: Double,
    onHire: () -> Unit
) {
    val canAfford = playerCash >= exec.cost

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exec_card_${exec.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (exec.hired) EmeraldPrimary else DarkCardBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Padding.cardCompact)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (exec.hired) Color(0xFF064E3B) else DarkSurfaceVariant, CircleShape)
                            .border(1.dp, if (exec.hired) EmeraldPrimary else DarkCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = exec.emoji, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.small))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = exec.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = exec.role,
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(DesignSystem.Spacing.extraSmall))

                if (!exec.hired) {
                    Button(
                        onClick = onHire,
                        enabled = canAfford,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = DarkSurfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("hire_exec_${exec.id}")
                    ) {
                        Text(
                            text = MoneyFormatter.format(exec.cost),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "EN POSTE",
                            color = EmeraldDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (exec.hired) Color(0xFF064E3B).copy(alpha = 0.4f) else DarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .border(1.dp, if (exec.hired) EmeraldPrimary.copy(alpha = 0.4f) else DarkCardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column {
                    Text(
                        text = "⭐ Perk : ${exec.perkTitle}",
                        color = if (exec.hired) EmeraldDark else CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = exec.perkDescription,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
