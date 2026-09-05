package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled. MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdNetworkTier
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

enum class AdSortOption {
    MOST_LUCRATIVE,
    CHEAPEST,
    HIGHEST_LEVEL
}

@Composable
fun AdMonetizationScreen(
    state: GameUiState,
    onUnlockAdNetwork: (id: String) -> Unit,
    onUpgradeAdNetwork: (id: String) -> Unit,
    onLaunchAdCampaign: (id: String) -> Unit,
    onOpenSponsorDeal: () -> Unit,
    onTriggerRewardedAd: (rewardDesc: String, bonusCash: Double, actionType: String) -> Unit,
    onTriggerInterstitialAd: () -> Unit,
    onSignSponsorshipContract: (id: String) -> Unit = {},
    onRenewSponsorshipContract: (id: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sortOption by remember { mutableStateOf(AdSortOption.MOST_LUCRATIVE) }
    var selectedChannel by remember { mutableStateOf<String?>(null) }
    var onlyUnlockedFilter by remember { mutableStateOf(false) }

    val channels = remember(state.adNetworks) {
        state.adNetworks.map { it.channelType }.distinct()
    }

    val filteredAndSortedNetworks = remember(state.adNetworks, sortOption, selectedChannel, onlyUnlockedFilter) {
        var list = state.adNetworks
        if (onlyUnlockedFilter) {
            list = list.filter { it.isUnlocked }
        } else if (selectedChannel != null) {
            list = list.filter { it.channelType == selectedChannel }
        }

        when (sortOption) {
            AdSortOption.MOST_LUCRATIVE -> list.sortedByDescending { it.currentRevenuePerSec }
            AdSortOption.CHEAPEST -> list.sortedBy { if (it.isUnlocked) it.nextUpgradeCost else it.unlockCost }
            AdSortOption.HIGHEST_LEVEL -> list.sortedByDescending { it.level }
        }
    }

    val totalUnlocked = state.adNetworks.count { it.isUnlocked }
    val totalCount = state.adNetworks.size
    val totalAdPassiveSec = state.adNetworks.filter { it.isUnlocked }.sumOf { it.currentRevenuePerSec }
    val totalAudienceReach = state.adNetworks.filter { it.isUnlocked }.sumOf { it.currentReach }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Hero Header KPI Card (Matching Real Estate Portfolio strip)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📡", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "RÉGIE PUBLICITAIRE & CANAUX",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Canaux actifs : $totalUnlocked / $totalCount • Portée : ${MoneyFormatter.format(totalAudienceReach.toDouble())} spectateurs",
                                color = EmeraldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(androidx.compose.material.icons.Icons.Default.MonetizationOn, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${MoneyFormatter.formatPerSec(totalAdPassiveSec)}",
                            color = EmeraldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Filter Pills Row (Exact Property Pattern)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (sortOption == AdSortOption.MOST_LUCRATIVE && !onlyUnlockedFilter && selectedChannel == null) Color(0xFFE11D48) else Color(0xFF1E293B))
                        .clickable {
                            sortOption = AdSortOption.MOST_LUCRATIVE
                            onlyUnlockedFilter = false
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(text = "Plus lucratif", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (sortOption == AdSortOption.CHEAPEST && !onlyUnlockedFilter && selectedChannel == null) Color(0xFFE11D48) else Color(0xFF1E293B))
                        .clickable {
                            sortOption = AdSortOption.CHEAPEST
                            onlyUnlockedFilter = false
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(text = "Moins cher", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (onlyUnlockedFilter) EmeraldDark else Color(0xFF1E293B))
                        .clickable {
                            onlyUnlockedFilter = !onlyUnlockedFilter
                            selectedChannel = null
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Canaux Acquis ($totalUnlocked)",
                        color = if (onlyUnlockedFilter) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(channels) { channel ->
                val isSelected = selectedChannel == channel && !onlyUnlockedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E293B))
                        .clickable {
                            selectedChannel = if (isSelected) null else channel
                            onlyUnlockedFilter = false
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = channel,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // List of Advertising Networks & Channels Cards (Matching RealEstatePropertyCard style)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("ad_networks_lazy_column"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredAndSortedNetworks, key = { it.id }) { network ->
                AdNetworkCard(
                    network = network,
                    playerCash = state.cash,
                    onUnlock = { onUnlockAdNetwork(network.id) },
                    onUpgrade = { onUpgradeAdNetwork(network.id) },
                    onLaunchCampaign = { onLaunchAdCampaign(network.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AdNetworkCard(
    network: AdNetworkTier,
    playerCash: Double,
    onUnlock: () -> Unit,
    onUpgrade: () -> Unit,
    onLaunchCampaign: () -> Unit
) {
    val isAffordableToUnlock = playerCash >= network.unlockCost && !network.isUnlocked
    val isAffordableToUpgrade = playerCash >= network.nextUpgradeCost && network.isUnlocked

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ad_network_card_${network.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        border = androidx.compose.foundation.BorderStroke(
            width = if (network.isUnlocked) 1.5.dp else 1.dp,
            color = if (network.isUnlocked) Color(0xFF10B981) else Color(0xFF1F2937)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual Image Frame with HUD Corner Brackets (Exact Real Estate Style)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(Color(0xFF0B0F19))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = network.iconEmoji, fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = network.channelType.uppercase(),
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                )

                // HUD Corner Brackets Overlay
                HudCornerBrackets(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )

                // Channel Type Tag (Top Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(0.8.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${network.iconEmoji} ${network.channelType}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Unlocked Stamp or Level Badge (Top Right)
                if (network.isUnlocked) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF15803D))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "NIVEAU ${network.level}",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "VERROUILLÉ 🔒",
                            color = AmberDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Name & Cost on Bottom-Left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 14.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = if (network.isUnlocked) MoneyFormatter.format(network.nextUpgradeCost) else MoneyFormatter.format(network.unlockCost),
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Public,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = network.name,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Action Button on Bottom-Right (Unlock or Upgrade)
                if (!network.isUnlocked) {
                    Button(
                        onClick = onUnlock,
                        enabled = isAffordableToUnlock,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22C55E),
                            disabledContainerColor = Color(0xFF374151)
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 14.dp, bottom = 12.dp)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Débloquer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onUpgrade,
                        enabled = isAffordableToUpgrade,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B82F6),
                            disabledContainerColor = Color(0xFF374151)
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 14.dp, bottom = 12.dp)
                            .height(38.dp)
                    ) {
                        Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Améliorer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Details and Revenue Section Below Image
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = network.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (network.isUnlocked) "+${MoneyFormatter.formatPerSec(network.currentRevenuePerSec)}/s" else "0 💰/s",
                            color = EmeraldLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Portée : ${MoneyFormatter.format(network.currentReach.toDouble())}",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (network.isUnlocked) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onLaunchCampaign,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Lancer Campagne Flash (+x2)", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
