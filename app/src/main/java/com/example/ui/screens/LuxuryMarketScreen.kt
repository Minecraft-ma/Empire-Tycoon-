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
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
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
import com.example.model.LuxuryAsset
import com.example.model.MoneyFormatter
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
fun LuxuryMarketScreen(
    state: GameUiState,
    onPurchaseAsset: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val categories = remember(state.luxuryAssets) {
        state.luxuryAssets.map { it.category }.distinct()
    }

    val filteredAssets = remember(state.luxuryAssets, selectedCategory) {
        if (selectedCategory == null) state.luxuryAssets
        else state.luxuryAssets.filter { it.category == selectedCategory }
    }

    val totalPrestigePoints = state.luxuryPrestigeScore
    val ownedCount = state.luxuryAssets.count { it.isPurchased }
    val totalCount = state.luxuryAssets.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("luxury_market_screen"),
        contentPadding = PaddingValues(DesignSystem.Padding.screenOuter),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.medium)
    ) {
        // Prestige & Wealth Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("luxury_market_summary_card"),
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
                            .background(AmberDark.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Diamond, contentDescription = null, tint = AmberDark, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "IMMOBILIER & RÉSIDENCES DE LUXE",
                            color = AmberDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Patrimoine : $ownedCount / $totalCount acquis • ⭐ $totalPrestigePoints pts de Prestige",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Tous les actifs (${state.luxuryAssets.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberDark.copy(alpha = 0.2f),
                            selectedLabelColor = AmberDark,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == null,
                            borderColor = if (selectedCategory == null) AmberDark else DarkCardBorder
                        )
                    )
                }
                items(categories) { cat ->
                    val count = state.luxuryAssets.count { it.category == cat }
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text("$cat ($count)", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberDark.copy(alpha = 0.2f),
                            selectedLabelColor = AmberDark,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedCategory == cat,
                            borderColor = if (selectedCategory == cat) AmberDark else DarkCardBorder
                        )
                    )
                }
            }
        }

        // Luxury Assets List
        items(filteredAssets, key = { it.id }) { asset ->
            LuxuryAssetCard(
                asset = asset,
                playerCash = state.cash,
                onPurchase = { onPurchaseAsset(asset.id) }
            )
        }
    }
}

@Composable
fun LuxuryAssetCard(
    asset: LuxuryAsset,
    playerCash: Double,
    onPurchase: () -> Unit
) {
    val isAffordable = playerCash >= asset.cost && !asset.isPurchased

    val cardBorder = if (asset.isPurchased) EmeraldDark else DarkCardBorder

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("luxury_asset_${asset.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (asset.isPurchased) DarkSurface else DarkSurface.copy(alpha = 0.95f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Emoji + Title + Prestige Points Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = asset.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = asset.name,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = asset.category,
                            color = CyberCyan,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }

                // Prestige Points Badge
                Box(
                    modifier = Modifier
                        .background(AmberDark.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .border(1.dp, AmberDark.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = AmberDark, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+${asset.prestigeScore} ⭐", color = AmberDark, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = asset.description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Perks Box (Click boost or Passive multiplier)
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
                        Icon(
                            if (asset.clickPowerBoostPercent > 0) Icons.Default.FlashOn else Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = if (asset.clickPowerBoostPercent > 0) AmberDark else EmeraldLight,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Avantage statutaire :",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = when {
                            asset.rentRevenuePerSec > 0 -> "+${MoneyFormatter.formatPerSec(asset.rentRevenuePerSec)} Loyers"
                            asset.clickPowerBoostPercent > 0 -> "+${(asset.clickPowerBoostPercent * 100).toInt()}% Puissance Clic"
                            else -> "+${(asset.passiveIncomeMultiplier * 100).toInt()}% Multiplicateur Global"
                        },
                        color = EmeraldLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp
                    )
                }
            }

            // Purchase / Owned Action Button
            Spacer(modifier = Modifier.height(12.dp))
            if (asset.isPurchased) {
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
                            text = "Actif de prestige dans votre patrimoine privé",
                            color = EmeraldLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Button(
                    onClick = onPurchase,
                    enabled = isAffordable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberDark,
                        disabledContainerColor = DarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("buy_luxury_${asset.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Acquérir cet actif de prestige",
                            color = if (isAffordable) DarkBackground else TextMuted,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                        Text(
                            text = MoneyFormatter.format(asset.cost),
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
