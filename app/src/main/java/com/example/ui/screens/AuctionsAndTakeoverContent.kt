package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AuctionLot
import com.example.model.CorporateTakeover
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DesignSystem
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun AuctionsAndTakeoverContent(
    state: GameUiState,
    onPlaceBid: (String) -> Unit,
    onBuyoutLot: (String) -> Unit,
    onResetLot: (String) -> Unit,
    onAcquireStake: (String) -> Unit,
    onOpenLiveWarModal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("auctions_and_takeovers_view")
    ) {
        // Sub-Navigation Tabs
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
                        Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enchères (${state.auctionLots.count { !it.isExpired }})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                        Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Fusions & OPA (${state.corporateTakeovers.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selectedContentColor = AmberDark,
                unselectedContentColor = TextSecondary
            )
        }

        if (selectedSubTab == 0) {
            AuctionsListSection(
                state = state,
                onPlaceBid = onPlaceBid,
                onBuyoutLot = onBuyoutLot,
                onResetLot = onResetLot,
                onOpenLiveWarModal = onOpenLiveWarModal
            )
        } else {
            CorporateTakeoversSection(
                state = state,
                onAcquireStake = onAcquireStake
            )
        }
    }
}

@Composable
private fun AuctionsListSection(
    state: GameUiState,
    onPlaceBid: (String) -> Unit,
    onBuyoutLot: (String) -> Unit,
    onResetLot: (String) -> Unit,
    onOpenLiveWarModal: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("auction_lots_list"),
        contentPadding = PaddingValues(DesignSystem.Padding.screenOuter),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
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
                            .size(44.dp)
                            .background(AmberDark.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = AmberDark, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GUERRE DES ENCHÈRES EN DIRECT",
                            color = AmberDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Surclassez les conglomérats rivaux et remportez des brevets & actifs exclusifs.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        items(state.auctionLots, key = { it.id }) { lot ->
            AuctionLotCard(
                lot = lot,
                playerCash = state.cash,
                onPlaceBid = { onPlaceBid(lot.id) },
                onBuyout = { onBuyoutLot(lot.id) },
                onReset = { onResetLot(lot.id) },
                onOpenLiveModal = { onOpenLiveWarModal(lot.id) }
            )
        }
    }
}

@Composable
fun AuctionLotCard(
    lot: AuctionLot,
    playerCash: Double,
    onPlaceBid: () -> Unit,
    onBuyout: () -> Unit,
    onReset: () -> Unit,
    onOpenLiveModal: () -> Unit
) {
    val isAffordableBid = playerCash >= lot.nextMinBid
    val isAffordableBuyout = playerCash >= lot.instantBuyoutPrice

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val borderColor = when {
        lot.isWonByPlayer -> EmeraldDark
        lot.isPlayerWinning -> AmberDark
        lot.isExpired -> CrimsonFrenzy.copy(alpha = 0.5f)
        else -> DarkCardBorder
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("auction_lot_${lot.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Emoji + Title + Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = lot.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = lot.title,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = lot.category,
                            color = CyberCyan,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }

                // Status Badge
                when {
                    lot.isWonByPlayer -> {
                        Box(
                            modifier = Modifier
                                .background(EmeraldDark.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                .border(1.dp, EmeraldDark, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ACQUIS", color = EmeraldLight, fontWeight = FontWeight.Black, fontSize = 10.sp)
                            }
                        }
                    }
                    lot.isExpired -> {
                        Box(
                            modifier = Modifier
                                .background(CrimsonFrenzy.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .border(1.dp, CrimsonFrenzy, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("TERMINÉE", color = CrimsonFrenzy, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (lot.isPlayerWinning) AmberDark.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                    RoundedCornerShape(20.dp)
                                )
                                .border(1.dp, if (lot.isPlayerWinning) AmberDark else DarkCardBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.HourglassBottom,
                                    contentDescription = null,
                                    tint = if (lot.timeRemainingSec <= 10) CrimsonFrenzy else AmberDark,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${lot.timeRemainingSec}s",
                                    color = if (lot.timeRemainingSec <= 10) CrimsonFrenzy else TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = lot.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Reward Yield Box
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
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = AmberDark, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Rendement de l'actif :",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = if (lot.permanentMultiplier > 0) "+${(lot.permanentMultiplier * 100).toInt()}% Multiplicateur Global"
                               else "+${MoneyFormatter.format(lot.bonusCashYieldPerSec)}/sec",
                        color = EmeraldLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            // Current Highest Bidder Info
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Offre en tête :", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = MoneyFormatter.format(lot.currentBid),
                        color = if (lot.isPlayerWinning) EmeraldLight else AmberDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Meilleur enchérisseur :", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = if (lot.isPlayerWinning) "👑 Vous (En tête)" else lot.highestBidderName,
                        color = if (lot.isPlayerWinning) EmeraldLight else TextSecondary,
                        fontWeight = if (lot.isPlayerWinning) FontWeight.Black else FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Rivals Avatars List
            if (!lot.isWonByPlayer && !lot.isExpired) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Rivaux en lice :", color = TextMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    lot.activeRivals.forEach { rival ->
                        Text(
                            text = rival.avatar,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }

            // Action Buttons
            Spacer(modifier = Modifier.height(12.dp))
            if (lot.isWonByPlayer) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldDark.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🎉 Actif acquis ! Les bonus sont actifs sur vos profits.",
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            } else if (lot.isExpired) {
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(containerColor = AmberDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Relancer la vente aux enchères", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Standard Bid Button (+15%)
                    Button(
                        onClick = onPlaceBid,
                        enabled = isAffordableBid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (lot.isPlayerWinning) AmberDark.copy(alpha = 0.8f) else AmberDark,
                            disabledContainerColor = DarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("bid_btn_${lot.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (lot.isPlayerWinning) "Surenchérir" else "Enchérir",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isAffordableBid) DarkBackground else TextMuted
                            )
                            Text(
                                text = MoneyFormatter.format(lot.nextMinBid),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = if (isAffordableBid) DarkBackground else TextMuted
                            )
                        }
                    }

                    // Instant Buyout Button
                    OutlinedButton(
                        onClick = onBuyout,
                        enabled = isAffordableBuyout,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAffordableBuyout) CyberCyan else DarkCardBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("buyout_btn_${lot.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Achat Immédiat",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = if (isAffordableBuyout) CyberCyan else TextMuted
                            )
                            Text(
                                text = MoneyFormatter.format(lot.instantBuyoutPrice),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = if (isAffordableBuyout) CyberCyan else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CorporateTakeoversSection(
    state: GameUiState,
    onAcquireStake: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("corporate_takeovers_list"),
        contentPadding = PaddingValues(DesignSystem.Padding.screenOuter),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
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
                            .size(44.dp)
                            .background(CyberCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PieChart, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FUSIONS & ACQUISITIONS (M&A)",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Achetez des blocs d'actions par tranche de 25% jusqu'à l'OPA totale pour encaisser de lourds dividendes.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        items(state.corporateTakeovers, key = { it.id }) { takeover ->
            CorporateTakeoverCard(
                takeover = takeover,
                playerCash = state.cash,
                onAcquireStake = { onAcquireStake(takeover.id) }
            )
        }
    }
}

@Composable
fun CorporateTakeoverCard(
    takeover: CorporateTakeover,
    playerCash: Double,
    onAcquireStake: () -> Unit
) {
    val isAffordable = playerCash >= takeover.nextStakeCost && !takeover.isFullyAcquired
    val stakeRatio = takeover.ownedStakePercentage / 100f

    val cardBorder = if (takeover.isFullyAcquired) EmeraldDark else DarkCardBorder

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("takeover_card_${takeover.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Emoji + Name + Valuation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = takeover.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = takeover.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = takeover.industry,
                            color = CyberCyan,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Valuation :", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = MoneyFormatter.format(takeover.totalEnterpriseValue),
                        color = AmberDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = takeover.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Board Seats Perk description
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AmberDark, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dividendes : +${MoneyFormatter.format(takeover.currentPassiveIncome)}/s",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Stake Percentage Progress Bar
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Participation détenue :",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "${takeover.ownedStakePercentage}%",
                    color = if (takeover.isFullyAcquired) EmeraldLight else AmberDark,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { stakeRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (takeover.isFullyAcquired) EmeraldLight else AmberDark,
                trackColor = DarkSurfaceVariant
            )

            // Current Dividends Generated
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dividendes passifs :",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "+${MoneyFormatter.format(takeover.currentPassiveIncome)}/sec",
                    color = EmeraldLight,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            // Acquisition Button
            Spacer(modifier = Modifier.height(12.dp))
            if (takeover.isFullyAcquired) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldDark.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OPA 100% Réussie • Filiale Intégrée",
                            color = EmeraldLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Button(
                    onClick = onAcquireStake,
                    enabled = isAffordable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        disabledContainerColor = DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("acquire_stake_${takeover.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Acquérir +25% des parts",
                            color = if (isAffordable) DarkBackground else TextMuted,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = MoneyFormatter.format(takeover.nextStakeCost),
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
