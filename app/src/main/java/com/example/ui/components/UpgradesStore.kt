package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MoneyFormatter
import com.example.model.ProductivityUpgrade
import com.example.model.UpgradeCategory
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

/**
 * UpgradesStore is the high-productivity marketplace where players spend their capital
 * on permanent productivity multipliers (Click revenue +X%, Passive income +Y%, Ad & Sponsor bonus +Z%, Stock yield +W%).
 */
@Composable
fun UpgradesStoreDialog(
    state: GameUiState,
    onBuyUpgrade: (String) -> Unit,
    onBuyMaxUpgrade: (String) -> Unit,
    onTriggerRewardedAd: (rewardDesc: String, bonusCash: Double, actionType: String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, AmberPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .testTag("upgrades_store_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                UpgradesStoreContent(
                    state = state,
                    onBuyUpgrade = onBuyUpgrade,
                    onBuyMaxUpgrade = onBuyMaxUpgrade,
                    onTriggerRewardedAd = onTriggerRewardedAd,
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UpgradesStoreContent(
    state: GameUiState,
    onBuyUpgrade: (String) -> Unit,
    onBuyMaxUpgrade: (String) -> Unit,
    onTriggerRewardedAd: (rewardDesc: String, bonusCash: Double, actionType: String) -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<UpgradeCategory?>(null) } // null = ALL

    val filteredUpgrades = remember(state.productivityUpgrades, selectedCategory) {
        if (selectedCategory == null) {
            state.productivityUpgrades
        } else {
            state.productivityUpgrades.filter { it.category == selectedCategory }
        }
    }

    // Multiplier summaries
    val clickBonus = state.productivityUpgrades
        .filter { it.category == UpgradeCategory.CLICK_POWER }
        .sumOf { it.level * it.multiplierPerLevel * 100.0 }

    val passiveBonus = state.productivityUpgrades
        .filter { it.category == UpgradeCategory.PASSIVE_BUSINESS }
        .sumOf { it.level * it.multiplierPerLevel * 100.0 }

    val adBonus = state.productivityUpgrades
        .filter { it.category == UpgradeCategory.ADS_AND_SPONSORS }
        .sumOf { it.level * it.multiplierPerLevel * 100.0 }

    val financeBonus = state.productivityUpgrades
        .filter { it.category == UpgradeCategory.FINANCE_AND_MARKET }
        .sumOf { it.level * it.multiplierPerLevel * 100.0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkSurface)
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            DarkSurfaceVariant
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AmberPrimary.copy(alpha = 0.2f))
                            .border(1.dp, AmberPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚡", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MAGASIN D'AMÉLIORATIONS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = AmberPrimary,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Multiplicateurs permanents & Dopez l'économie",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("upgrades_store_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = TextSecondary
                        )
                    }
                }
            }
        }

        // Live Active Multipliers Summary Bar
        Surface(
            color = Color(0xFF0F172A),
            modifier = Modifier.fillMaxWidth()
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MultiplierChip(
                    emoji = "👆",
                    label = "Clics",
                    bonusPercent = clickBonus,
                    color = AmberDark
                )
                MultiplierChip(
                    emoji = "🏢",
                    label = "Passif",
                    bonusPercent = passiveBonus,
                    color = EmeraldDark
                )
                MultiplierChip(
                    emoji = "📺",
                    label = "Pubs & Sponsors",
                    bonusPercent = adBonus,
                    color = CyberCyan
                )
                MultiplierChip(
                    emoji = "📈",
                    label = "Bourse",
                    bonusPercent = financeBonus,
                    color = ElectricPurple
                )
            }
        }

        // Available Player Cash Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "FONDS DISPONIBLES :",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Text(
                text = MoneyFormatter.format(state.cash),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = EmeraldPrimary
            )
        }

        // Category Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                CategoryTabChip(
                    title = "TOUT (16)",
                    emoji = "⭐",
                    isSelected = selectedCategory == null,
                    onClick = { selectedCategory = null }
                )
            }
            item {
                CategoryTabChip(
                    title = "CLICS (+${String.format("%.0f", clickBonus)}%)",
                    emoji = "⚡",
                    isSelected = selectedCategory == UpgradeCategory.CLICK_POWER,
                    onClick = { selectedCategory = UpgradeCategory.CLICK_POWER }
                )
            }
            item {
                CategoryTabChip(
                    title = "PASSIF (+${String.format("%.0f", passiveBonus)}%)",
                    emoji = "🏢",
                    isSelected = selectedCategory == UpgradeCategory.PASSIVE_BUSINESS,
                    onClick = { selectedCategory = UpgradeCategory.PASSIVE_BUSINESS }
                )
            }
            item {
                CategoryTabChip(
                    title = "PUBS & SPONSORS (+${String.format("%.0f", adBonus)}%)",
                    emoji = "📺",
                    isSelected = selectedCategory == UpgradeCategory.ADS_AND_SPONSORS,
                    onClick = { selectedCategory = UpgradeCategory.ADS_AND_SPONSORS }
                )
            }
            item {
                CategoryTabChip(
                    title = "FINANCE (+${String.format("%.0f", financeBonus)}%)",
                    emoji = "📈",
                    isSelected = selectedCategory == UpgradeCategory.FINANCE_AND_MARKET,
                    onClick = { selectedCategory = UpgradeCategory.FINANCE_AND_MARKET }
                )
            }
        }

        // Upgrade Items List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Instant Ad Boost Banner inside the Store
            item {
                StoreAdTurboCard(
                    state = state,
                    onTriggerRewardedAd = onTriggerRewardedAd
                )
            }

            items(filteredUpgrades, key = { it.id }) { upgrade ->
                ProductivityUpgradeItemCard(
                    upgrade = upgrade,
                    playerCash = state.cash,
                    onBuy = { onBuyUpgrade(upgrade.id) },
                    onBuyMax = { onBuyMaxUpgrade(upgrade.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun MultiplierChip(
    emoji: String,
    label: String,
    bonusPercent: Double,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$label : ",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
            Text(
                text = "+${String.format("%.0f", bonusPercent)}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Composable
fun CategoryTabChip(
    title: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = "$emoji $title",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AmberPrimary,
            selectedLabelColor = Color.Black,
            containerColor = DarkSurfaceVariant,
            labelColor = TextSecondary
        ),
        shape = RoundedCornerShape(10.dp),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = if (isSelected) AmberPrimary else DarkCardBorder,
            selectedBorderColor = AmberPrimary,
            enabled = true,
            selected = isSelected
        )
    )
}

@Composable
fun ProductivityUpgradeItemCard(
    upgrade: ProductivityUpgrade,
    playerCash: Double,
    onBuy: () -> Unit,
    onBuyMax: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val canAfford = playerCash >= upgrade.currentCost && !upgrade.isMaxed
    val tagColor = Color(upgrade.tagHexColor)
    val progress = (upgrade.level.toFloat() / upgrade.maxLevel.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (canAfford) tagColor.copy(alpha = 0.5f) else DarkCardBorder,
                RoundedCornerShape(14.dp)
            )
            .testTag("upgrade_card_${upgrade.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (canAfford) Color(0xFF1E293B) else DarkSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header Row: Emoji, Name, Badge, Level
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(tagColor.copy(alpha = 0.2f))
                            .border(1.dp, tagColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = upgrade.iconEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = upgrade.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = tagColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = upgrade.badgeText,
                                    color = tagColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+${String.format("%.0f", upgrade.totalBonusPercent)}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Level badge
                Surface(
                    color = if (upgrade.isMaxed) EmeraldDark.copy(alpha = 0.3f) else Color(0xFF0F172A),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (upgrade.isMaxed) EmeraldPrimary else DarkCardBorder
                    )
                ) {
                    Text(
                        text = if (upgrade.isMaxed) "MAX" else "Niv. ${upgrade.level}/${upgrade.maxLevel}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (upgrade.isMaxed) EmeraldPrimary else AmberPrimary,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = upgrade.description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Level progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = tagColor,
                trackColor = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (upgrade.isMaxed) {
                    Button(
                        onClick = {},
                        enabled = false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = EmeraldDark.copy(alpha = 0.3f),
                            disabledContentColor = EmeraldLight
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "AMÉLIORATION MAXIMISÉE", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                } else {
                    // Buy 1 Level Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onBuy()
                        },
                        enabled = canAfford,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .testTag("upg_buy_btn_${upgrade.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = Color(0xFF334155),
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "+1 Niv • ${MoneyFormatter.format(upgrade.currentCost)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1
                        )
                    }

                    // Buy Max Button
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onBuyMax()
                        },
                        enabled = canAfford,
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("upg_max_btn_${upgrade.id}"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CyberCyan,
                            disabledContentColor = TextMuted
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canAfford) CyberCyan else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "MAX",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Turbo Ad Accelerator Card inside Upgrades Store: allows players to trigger
 * instant 4x multipliers or immediate passive revenue injections.
 */
@Composable
fun StoreAdTurboCard(
    state: GameUiState,
    onTriggerRewardedAd: (rewardDesc: String, bonusCash: Double, actionType: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .testTag("store_ad_turbo_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F2338)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyberCyan.copy(alpha = 0.2f))
                        .border(1.dp, CyberCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ACCÉLÉRATEUR PUBLICITAIRE GRATUIT",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberCyan
                        )
                    }
                    Text(
                        text = "Visionne une annonce pour décupler instantanément tes revenus",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: 4x Super Multiplier
                Button(
                    onClick = {
                        onTriggerRewardedAd(
                            "⚡ Surcharge 4x Activée pendant 60 secondes !",
                            0.0,
                            "SUPER_BOOST_4X"
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("store_ad_boost_4x_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⚡ BOOST 4X (60s)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                }

                // Button 2: Instant Cash Grant (5 min of passive income)
                val instantCash = (state.totalPassiveRevenuePerSec * 300.0).coerceAtLeast(10_000.0)
                Button(
                    onClick = {
                        onTriggerRewardedAd(
                            "🎁 Injecté +${MoneyFormatter.format(instantCash)} de trésorerie !",
                            instantCash,
                            "INSTANT_CASH_DROP"
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("store_ad_cash_grant_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "💵 +${MoneyFormatter.format(instantCash)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
