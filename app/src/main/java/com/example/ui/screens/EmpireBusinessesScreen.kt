package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.model.Business
import com.example.model.BusinessCategory
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
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun EmpireBusinessesScreen(
    state: GameUiState,
    onUpgradeBusiness: (id: String) -> Unit,
    onHireManager: (id: String) -> Unit,
    onUnlockBusiness: (id: String) -> Unit,
    onRenameBusiness: (id: String, name: String) -> Unit,
    onBuildingClick: (id: String) -> Unit = {},
    onPurchaseProperty: (id: String) -> Unit = {},
    onRenovateProperty: (id: String) -> Unit = {},
    onSellProperty: (id: String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeCategory by remember { mutableStateOf<BusinessCategory?>(null) }
    var editingBusiness by remember { mutableStateOf<Business?>(null) }
    var newBusinessName by remember { mutableStateOf("") }

    // Dialog for renaming business
    if (editingBusiness != null) {
        AlertDialog(
            onDismissRequest = { editingBusiness = null },
            title = {
                Text(
                    text = "Renommer l'entreprise",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "Choisis un nom unique pour ton bâtiment :",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = newBusinessName,
                        onValueChange = { newBusinessName = it },
                        placeholder = { Text(editingBusiness?.name ?: "", color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rename_business_input")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = editingBusiness
                        if (target != null && newBusinessName.isNotBlank()) {
                            onRenameBusiness(target.id, newBusinessName)
                        }
                        editingBusiness = null
                    }
                ) {
                    Text("Confirmer", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBusiness = null }) {
                    Text("Annuler", color = Color.White)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // =========================================================================
    // STATE 1: CATEGORY SELECTION HUB (Grid 4x2 matching the screenshot)
    // =========================================================================
    if (activeCategory == null) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("empire_category_grid_hub"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Bar matching reference screenshot
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Choose new category",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.3.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Clique sur un secteur pour gérer ses actifs",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }

                        // Cash Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF131C31))
                                .border(1.dp, Color(0xFF2E3D5C), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "VIZA ", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 11.sp)
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
            }

            // Grid of categories (4 rows x 2 columns)
            val categories = BusinessCategory.values().toList()
            val rows = categories.chunked(2)

            rows.forEach { rowCategories ->
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowCategories.forEach { cat ->
                            CategoryCardTile(
                                cat = cat,
                                state = state,
                                modifier = Modifier.weight(1f),
                                onSelect = {
                                    SoundManager.playTap()
                                    activeCategory = cat
                                }
                            )
                        }
                        // If row has only 1 item, fill remaining space
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    } else {
        // =========================================================================
        // STATE 2: DEDICATED CATEGORY VIEW (Replaces the page with back button)
        // =========================================================================
        val selectedCat = activeCategory!!
        val currentCategoryBusinesses = state.businesses.filter { biz ->
            biz.categoryGroup == selectedCat.id || (selectedCat == BusinessCategory.MAGASINS && (biz.categoryGroup.isBlank() || biz.categoryGroup == "MAGASINS"))
        }

        val categoryTotalRev = if (selectedCat == BusinessCategory.HOUSES) {
            state.totalRealEstateRentPerSec
        } else {
            currentCategoryBusinesses.sumOf { it.revenuePerSecond }
        }

        val catColor = when (selectedCat) {
            BusinessCategory.MAGASINS -> EmeraldPrimary
            BusinessCategory.HOUSES -> Color(0xFF22C55E)
            BusinessCategory.TAXI -> AmberPrimary
            BusinessCategory.LIVRAISON -> Color(0xFF38BDF8)
            BusinessCategory.BANQUE -> Color(0xFFFBBF24)
            BusinessCategory.INDUSTRIE -> CyberCyan
            BusinessCategory.TECH -> Color(0xFFC084FC)
            BusinessCategory.SPATIAL -> Color(0xFFEC4899)
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("empire_category_detail_screen"),
            contentPadding = PaddingValues(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // TOP BAR WITH BACK BUTTON
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF1E293B))
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
                            // Back Button
                            Button(
                                onClick = {
                                    SoundManager.playTap()
                                    activeCategory = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E293B),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("back_to_categories_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "←",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                    Text(
                                        text = "Catégories",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Cash Balance Pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF131C31))
                                    .border(1.dp, Color(0xFF2E3D5C), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "VIZA ", color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    Text(
                                        text = MoneyFormatter.format(state.cash),
                                        color = EmeraldLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Title & Banner Details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            catColor.copy(alpha = 0.20f),
                                            Color(0xFF1E293B).copy(alpha = 0.5f)
                                        )
                                    )
                                )
                                .border(1.dp, catColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${selectedCat.emoji} ${selectedCat.title.uppercase()}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedCat.description,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (selectedCat == BusinessCategory.HOUSES) "Loyers / sec" else "Revenu / sec",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "+${MoneyFormatter.formatPerSec(categoryTotalRev)}",
                                    color = EmeraldLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            // CONTENT: HOUSES MARKET OR BUSINESSES LIST
            if (selectedCat == BusinessCategory.HOUSES) {
                items(state.luxuryAssets, key = { it.id }) { property ->
                    RealEstatePropertyCard(
                        property = property,
                        playerCash = state.cash,
                        onBuy = { onPurchaseProperty(property.id) },
                        onRenovate = { onRenovateProperty(property.id) },
                        onSell = { onSellProperty(property.id) }
                    )
                }
            } else {
                items(currentCategoryBusinesses, key = { it.id }) { biz ->
                    BusinessItemCard(
                        biz = biz,
                        playerCash = state.cash,
                        categoryColor = catColor,
                        onUpgrade = { onUpgradeBusiness(biz.id) },
                        onHireManager = { onHireManager(biz.id) },
                        onUnlock = { onUnlockBusiness(biz.id) },
                        onCardClick = { onBuildingClick(biz.id) },
                        onRenameClick = {
                            editingBusiness = biz
                            newBusinessName = biz.name
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

/**
 * Modern Category Tile matching the reference screenshot design
 */
@Composable
fun CategoryCardTile(
    cat: BusinessCategory,
    state: GameUiState,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit
) {
    val cardBgColor = when (cat) {
        BusinessCategory.MAGASINS -> Color(0xFFB4E876) // Lime green as in Shop
        BusinessCategory.TAXI -> Color(0xFFFB923C)     // Warm orange as in Taxi
        BusinessCategory.LIVRAISON -> Color(0xFF7DD3FC) // Sky blue as in Delivery
        BusinessCategory.HOUSES -> Color(0xFF6EE7B7)   // Mint green for Houses
        BusinessCategory.BANQUE -> Color(0xFFFDE047)   // Golden yellow for Bank
        BusinessCategory.INDUSTRIE -> Color(0xFF94A3B8) // Slate gray for Industry
        BusinessCategory.TECH -> Color(0xFFC084FC)     // Violet purple for Tech
        BusinessCategory.SPATIAL -> Color(0xFFF472B6)  // Radiant pink for Spatial
    }

    val catImageRes = when (cat) {
        BusinessCategory.MAGASINS -> R.drawable.img_cat_shop_hd
        BusinessCategory.TAXI -> R.drawable.img_cat_taxi_hd
        BusinessCategory.LIVRAISON -> R.drawable.img_cat_delivery_hd
        BusinessCategory.HOUSES -> R.drawable.img_cat_houses_hd
        BusinessCategory.BANQUE -> R.drawable.img_cat_bank_hd
        BusinessCategory.INDUSTRIE -> R.drawable.img_cat_industry_hd
        BusinessCategory.TECH -> R.drawable.img_cat_tech_hd
        BusinessCategory.SPATIAL -> R.drawable.img_cat_spatial_hd
    }

    val startingPrice = when (cat) {
        BusinessCategory.MAGASINS -> "Dès $10"
        BusinessCategory.TAXI -> "Dès $350"
        BusinessCategory.LIVRAISON -> "Dès $8 000"
        BusinessCategory.HOUSES -> "Dès $185 000"
        BusinessCategory.BANQUE -> "Dès $120 000"
        BusinessCategory.INDUSTRIE -> "Dès $450 000"
        BusinessCategory.TECH -> "Dès $2.5M"
        BusinessCategory.SPATIAL -> "Dès $50M"
    }

    val displayName = when (cat) {
        BusinessCategory.MAGASINS -> "Boutiques & Commerces"
        BusinessCategory.TAXI -> "Flotte Taxis & VTC"
        BusinessCategory.LIVRAISON -> "Delivery & Fret"
        BusinessCategory.HOUSES -> "Maisons & Résidences"
        BusinessCategory.BANQUE -> "Banque & Finance"
        BusinessCategory.INDUSTRIE -> "Gigafactory & Usines"
        BusinessCategory.TECH -> "Tech & Datacenters"
        BusinessCategory.SPATIAL -> "Spatioport & Orbital"
    }

    val catAccentColor = when (cat) {
        BusinessCategory.MAGASINS -> EmeraldPrimary
        BusinessCategory.TAXI -> AmberPrimary
        BusinessCategory.LIVRAISON -> Color(0xFF38BDF8)
        BusinessCategory.HOUSES -> Color(0xFF22C55E)
        BusinessCategory.BANQUE -> Color(0xFFFBBF24)
        BusinessCategory.INDUSTRIE -> CyberCyan
        BusinessCategory.TECH -> Color(0xFFC084FC)
        BusinessCategory.SPATIAL -> Color(0xFFEC4899)
    }

    val ownedCountText = if (cat == BusinessCategory.HOUSES) {
        val owned = state.luxuryAssets.count { it.isPurchased }
        "$owned/${state.luxuryAssets.size} acquis"
    } else {
        val catBizzes = state.businesses.filter {
            it.categoryGroup == cat.id || (cat == BusinessCategory.MAGASINS && (it.categoryGroup.isBlank() || it.categoryGroup == "MAGASINS"))
        }
        val unlocked = catBizzes.count { it.isUnlocked }
        "$unlocked/${catBizzes.size.coerceAtLeast(1)} actifs"
    }

    Card(
        modifier = modifier
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onSelect() }
            .testTag("category_grid_${cat.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.5.dp, catAccentColor.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-bleed realistic image taking up entire card
            Image(
                painter = painterResource(id = catImageRes),
                contentDescription = displayName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Cinematic Multi-stop Dark Gradient Overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.75f),
                                Color.Black.copy(alpha = 0.20f),
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Overlaid Content Container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row (Name + Price + Badge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = cat.emoji,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = displayName,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        // Starting Price Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(catAccentColor.copy(alpha = 0.30f))
                                .border(0.8.dp, catAccentColor.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = startingPrice,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Owned Counter Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = ownedCountText,
                            color = catAccentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bottom Row (Category brief description + Open indicator)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cat.shortName,
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .border(0.8.dp, catAccentColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Gérer",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "→",
                                color = catAccentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessItemCard(
    biz: Business,
    playerCash: Double,
    categoryColor: Color,
    onUpgrade: () -> Unit,
    onHireManager: () -> Unit,
    onUnlock: () -> Unit,
    onCardClick: () -> Unit = {},
    onRenameClick: () -> Unit
) {
    val canAffordUpgrade = playerCash >= biz.currentCost
    val canAffordManager = playerCash >= biz.managerCost
    val canAffordUnlock = playerCash >= biz.baseCost

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = biz.isUnlocked) { onCardClick() }
            .testTag("business_card_${biz.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            1.dp,
            if (biz.isUnlocked) DarkCardBorder else DarkCardBorder.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Padding.cardCompact)
        ) {
            if (!biz.isUnlocked) {
                // Locked Business Card Layout
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
                                .size(40.dp)
                                .background(DarkSurfaceVariant, CircleShape)
                                .border(1.dp, DarkCardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = biz.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = biz.description,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onUnlock,
                        enabled = canAffordUnlock,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = DarkSurfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("unlock_biz_btn_${biz.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "DÉBLOQUER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = MoneyFormatter.format(biz.baseCost),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Unlocked Active Business Card
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
                                .size(40.dp)
                                .background(categoryColor.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, categoryColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = biz.getIcon(),
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = biz.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                IconButton(
                                    onClick = onRenameClick,
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Renommer",
                                        tint = TextMuted,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .background(categoryColor.copy(alpha = 0.22f), RoundedCornerShape(4.dp))
                                        .border(0.8.dp, categoryColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                ) {
                                    Text(
                                        text = "Niv. ${biz.level}",
                                        color = categoryColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1
                                    )
                                }
                            }

                            Text(
                                text = "+${MoneyFormatter.formatPerSec(biz.revenuePerSecond)}",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Upgrade Button
                    Button(
                        onClick = onUpgrade,
                        enabled = canAffordUpgrade,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAffordUpgrade) EmeraldPrimary else DarkSurfaceVariant,
                            contentColor = if (canAffordUpgrade) Color.Black else TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("upgrade_biz_btn_${biz.id}")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "NIV +1",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = MoneyFormatter.format(biz.currentCost),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar
                val animatedProgress = remember { Animatable(if (biz.managerHired) 1.0f else biz.currentCycleProgress) }
                LaunchedEffect(biz.currentCycleProgress, biz.managerHired) {
                    val targetValue = if (biz.managerHired) 1.0f else biz.currentCycleProgress
                    animatedProgress.animateTo(
                        targetValue = targetValue,
                        animationSpec = spring(
                            dampingRatio = 0.85f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }

                LinearProgressIndicator(
                    progress = { animatedProgress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = categoryColor,
                    trackColor = DarkSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Automation Status Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Icon(
                            imageVector = if (biz.managerHired) Icons.Default.FlashOn else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (biz.managerHired) EmeraldPrimary else TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (biz.managerHired) "Production continue active" else "Cycle manuel",
                            color = if (biz.managerHired) TextPrimary else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (biz.managerHired) FontWeight.SemiBold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    if (!biz.managerHired) {
                        OutlinedButton(
                            onClick = onHireManager,
                            enabled = canAffordManager,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (canAffordManager) CyberCyan else TextMuted
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (canAffordManager) CyberCyan else DarkCardBorder
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("hire_manager_btn_${biz.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Automatiser (${MoneyFormatter.format(biz.managerCost)})",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "AUTOMATISÉ",
                                color = EmeraldPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
