package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.LuxuryAsset
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
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

enum class RealEstateSortOption {
    MORE_EXPENSIVE,
    CHEAPEST,
    HIGHEST_RENT
}

@Composable
fun RealEstateMarketDialog(
    isOpen: Boolean,
    state: GameUiState,
    onPurchaseProperty: (String) -> Unit,
    onRenovateProperty: (String) -> Unit,
    onSellProperty: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .testTag("real_estate_market_screen"),
            color = DarkBackground
        ) {
            RealEstateMarketContent(
                state = state,
                onPurchaseProperty = onPurchaseProperty,
                onRenovateProperty = onRenovateProperty,
                onSellProperty = onSellProperty,
                onBack = onDismiss
            )
        }
    }
}

@Composable
fun RealEstateMarketContent(
    state: GameUiState,
    onPurchaseProperty: (String) -> Unit,
    onRenovateProperty: (String) -> Unit,
    onSellProperty: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortOption by remember { mutableStateOf(RealEstateSortOption.MORE_EXPENSIVE) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var onlyOwnedFilter by remember { mutableStateOf(false) }

    val categories = remember(state.luxuryAssets) {
        state.luxuryAssets.map { it.category }.distinct()
    }

    val filteredAndSortedAssets = remember(state.luxuryAssets, sortOption, selectedCategory, onlyOwnedFilter) {
        var list = state.luxuryAssets

        if (onlyOwnedFilter) {
            list = list.filter { it.isPurchased }
        } else if (selectedCategory != null) {
            list = list.filter { it.category == selectedCategory }
        }

        when (sortOption) {
            RealEstateSortOption.MORE_EXPENSIVE -> list.sortedByDescending { it.cost }
            RealEstateSortOption.CHEAPEST -> list.sortedBy { it.cost }
            RealEstateSortOption.HIGHEST_RENT -> list.sortedByDescending { it.rentRevenuePerSec }
        }
    }

    val totalOwned = state.luxuryAssets.count { it.isPurchased }
    val totalCount = state.luxuryAssets.size
    val totalRentPerSec = state.totalRealEstateRentPerSec
    val totalPortfolioValue = state.totalRealEstateEmpireValue

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top Header Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0C1021))
                .border(width = 1.dp, color = Color(0xFF1E293B))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back Button & Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("real_estate_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Real estate market",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Empire Tycoon Residences & Buildings",
                            color = EmeraldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Balance VIZA Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF131C31))
                        .border(1.dp, Color(0xFF2E3D5C), RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Balance: ",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "VIZA ",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                        Text(
                            text = MoneyFormatter.format(state.cash),
                            color = EmeraldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Real Estate Portfolio Quick Strip
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🏘️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Parc : $totalOwned / $totalCount acquis",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Loyers : +${MoneyFormatter.formatPerSec(totalRentPerSec)}",
                        color = EmeraldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Filter Pills Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // "More expensive" Pill (active red/coral style as in user screenshot)
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (sortOption == RealEstateSortOption.MORE_EXPENSIVE && !onlyOwnedFilter && selectedCategory == null)
                                Color(0xFFE11D48)
                            else Color(0xFF1E293B)
                        )
                        .clickable {
                            sortOption = RealEstateSortOption.MORE_EXPENSIVE
                            onlyOwnedFilter = false
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "More expensive",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // "Cheapest" Pill
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (sortOption == RealEstateSortOption.CHEAPEST && !onlyOwnedFilter && selectedCategory == null)
                                Color(0xFFE11D48)
                            else Color(0xFF1E293B)
                        )
                        .clickable {
                            sortOption = RealEstateSortOption.CHEAPEST
                            onlyOwnedFilter = false
                        }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Cheapest",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // "Mon Parc" (Owned) Pill
            item {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (onlyOwnedFilter) EmeraldDark else Color(0xFF1E293B)
                        )
                        .clickable {
                            onlyOwnedFilter = !onlyOwnedFilter
                            selectedCategory = null
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Mes Propriétés ($totalOwned)",
                        color = if (onlyOwnedFilter) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Category Pills
            items(categories) { cat ->
                val isSelected = selectedCategory == cat && !onlyOwnedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) Color(0xFF3B82F6) else Color(0xFF1E293B))
                        .clickable {
                            selectedCategory = if (isSelected) null else cat
                            onlyOwnedFilter = false
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // List of Real Estate Properties
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredAndSortedAssets, key = { it.id }) { property ->
                RealEstatePropertyCard(
                    property = property,
                    playerCash = state.cash,
                    onBuy = { onPurchaseProperty(property.id) },
                    onRenovate = { onRenovateProperty(property.id) },
                    onSell = { onSellProperty(property.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun RealEstatePropertyCard(
    property: LuxuryAsset,
    playerCash: Double,
    onBuy: () -> Unit,
    onRenovate: () -> Unit,
    onSell: () -> Unit
) {
    val isAffordable = playerCash >= property.cost && !property.isPurchased
    val canRenovate = property.isPurchased && property.renovationLevel < 5 && playerCash >= property.renovationCost

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("real_estate_card_${property.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        border = androidx.compose.foundation.BorderStroke(
            width = if (property.isPurchased) 1.5.dp else 1.dp,
            color = if (property.isPurchased) Color(0xFF22C55E) else Color(0xFF1F2937)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Visual Image Frame with HUD Corner Brackets
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFF0B0F19))
            ) {
                if (property.imageDrawableRes != null) {
                    Image(
                        painter = painterResource(id = property.imageDrawableRes),
                        contentDescription = property.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback Stylized Artwork
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
                            Text(text = property.iconEmoji, fontSize = 54.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = property.category.uppercase(),
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }

                // Dark gradient overlay on image bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                // White HUD Corner Brackets (As seen in the screenshot!)
                HudCornerBrackets(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )

                // Category & Status Tag (Top Left)
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
                        text = "${property.iconEmoji} ${property.category}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Owned Stamp / Prestige Stars (Top Right)
                if (property.isPurchased) {
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
                                text = "ACQUIS",
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
                            text = "+${property.prestigeScore} ⭐",
                            color = AmberDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Price Overlay on Bottom-Left of Image Frame (Exact Screenshot Style)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 14.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = MoneyFormatter.format(property.cost),
                        color = Color.White,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${property.name}, ${property.location}",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Buy Action Button on Bottom-Right of Image Frame (Exact Screenshot Style)
                if (!property.isPurchased) {
                    Button(
                        onClick = onBuy,
                        enabled = isAffordable,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF22C55E),
                            disabledContainerColor = Color(0xFF374151)
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 14.dp, bottom = 12.dp)
                            .height(38.dp)
                            .testTag("buy_property_${property.id}")
                    ) {
                        Text(
                            text = "Buy",
                            color = if (isAffordable) Color.Black else Color(0xFF9CA3AF),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Property Specs, Perks & Rental Flow Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = property.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Passive Rent Yield Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1E293B))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "💰", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Rendement Locatif :",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "+${MoneyFormatter.formatPerSec(property.effectiveRentRevenuePerSec)}",
                            color = EmeraldLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                // If Owned: Renovation & Management Actions
                if (property.isPurchased) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF1F2937), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Renovation Level Info
                        Column {
                            Text(
                                text = "Rénovation : Niv. ${property.renovationLevel} / 5",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (property.renovationLevel < 5)
                                    "Coût : ${MoneyFormatter.format(property.renovationCost)} (+25% loyer)"
                                else "Max Rénové (+125% loyers)",
                                color = if (property.renovationLevel < 5) EmeraldLight else AmberDark,
                                fontSize = 10.sp
                            )
                        }

                        // Renovation & Sell Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (property.renovationLevel < 5) {
                                Button(
                                    onClick = onRenovate,
                                    enabled = canRenovate,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF3B82F6),
                                        disabledContainerColor = Color(0xFF374151)
                                    ),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rénover", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = onSell,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("Vendre", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * HUD Corner Brackets Overlay mimicking high-end UI scanner / real estate luxury catalog
 */
@Composable
fun HudCornerBrackets(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val strokeW = 2.dp.toPx()
        val bracketLen = 14.dp.toPx()
        val w = size.width
        val h = size.height
        val bracketColor = Color.White.copy(alpha = 0.85f)

        // Top-Left
        drawLine(bracketColor, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(bracketLen, 0f), strokeWidth = strokeW)
        drawLine(bracketColor, start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(0f, bracketLen), strokeWidth = strokeW)

        // Top-Right
        drawLine(bracketColor, start = androidx.compose.ui.geometry.Offset(w, 0f), end = androidx.compose.ui.geometry.Offset(w - bracketLen, 0f), strokeWidth = strokeW)
        drawLine(bracketColor, start = androidx.compose.ui.geometry.Offset(w, 0f), end = androidx.compose.ui.geometry.Offset(w, bracketLen), strokeWidth = strokeW)

        // Bottom-Left
        drawLine(bracketColor, start = androidx.compose.ui.geometry.Offset(0f, h), end = androidx.compose.ui.geometry.Offset(bracketLen, h), strokeWidth = strokeW)
        drawLine(bracketColor, start = androidx.compose.ui.geometry.Offset(0f, h), end = androidx.compose.ui.geometry.Offset(0f, h - bracketLen), strokeWidth = strokeW)

        // Bottom-Right
        drawLine(bracketColor, start = androidx.compose.ui.geometry.Offset(w, h), end = androidx.compose.ui.geometry.Offset(w - bracketLen, h), strokeWidth = strokeW)
        drawLine(bracketColor, start = androidx.compose.ui.geometry.Offset(w, h), end = androidx.compose.ui.geometry.Offset(w, h - bracketLen), strokeWidth = strokeW)
    }
}
