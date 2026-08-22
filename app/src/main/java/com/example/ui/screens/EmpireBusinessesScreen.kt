package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Business
import com.example.model.BusinessCategory
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DesignSystem
import com.example.ui.theme.EmeraldDark
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
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(BusinessCategory.MAGASINS) }
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

    // Filter businesses by category
    val currentCategoryBusinesses = state.businesses.filter { biz ->
        biz.categoryGroup == selectedCategory.id || (selectedCategory == BusinessCategory.MAGASINS && biz.categoryGroup.isBlank())
    }

    val categoryTotalRev = currentCategoryBusinesses.sumOf { it.revenuePerSecond }
    val categoryUnlockedCount = currentCategoryBusinesses.count { it.isUnlocked }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("empire_businesses_list"),
        contentPadding = PaddingValues(DesignSystem.Padding.screenOuter),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section 1: 4 Category Tabs Switcher
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🏢 CONGLOMÉRAT D'ENTREPRISES",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Choisis une catégorie pour développer tes filiales",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4 Interactive Category Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    BusinessCategory.values().forEach { cat ->
                        val isSelected = cat == selectedCategory
                        val catBusinesses = state.businesses.filter { it.categoryGroup == cat.id }
                        val unlockedInCat = catBusinesses.count { it.isUnlocked }
                        val totalInCat = catBusinesses.size.coerceAtLeast(1)

                        val catColor = when (cat) {
                            BusinessCategory.MAGASINS -> EmeraldPrimary
                            BusinessCategory.BANQUE -> AmberPrimary
                            BusinessCategory.INDUSTRIE -> CyberCyan
                            BusinessCategory.TECH -> Color(0xFFC084FC)
                        }

                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) catColor.copy(alpha = 0.18f) else DarkSurface,
                            label = "catBg"
                        )
                        val borderColor by animateColorAsState(
                            targetValue = if (isSelected) catColor else DarkCardBorder,
                            label = "catBorder"
                        )

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat }
                                .testTag("category_tab_${cat.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 4.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${cat.emoji} ${cat.shortName}",
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (isSelected) catColor.copy(alpha = 0.25f) else DarkSurfaceVariant,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "$unlockedInCat/$totalInCat",
                                        color = if (isSelected) catColor else TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Selected Category Banner
        item {
            val catColor = when (selectedCategory) {
                BusinessCategory.MAGASINS -> EmeraldPrimary
                BusinessCategory.BANQUE -> AmberPrimary
                BusinessCategory.INDUSTRIE -> CyberCyan
                BusinessCategory.TECH -> Color(0xFFC084FC)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, catColor.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    catColor.copy(alpha = 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${selectedCategory.emoji} ${selectedCategory.title.uppercase()}",
                            color = catColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedCategory.description,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Revenu Catégorie",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "+${MoneyFormatter.formatPerSec(categoryTotalRev)}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Section 3: Buildings List for Selected Category
        items(currentCategoryBusinesses, key = { it.id }) { biz ->
            BusinessItemCard(
                biz = biz,
                playerCash = state.cash,
                categoryColor = when (selectedCategory) {
                    BusinessCategory.MAGASINS -> EmeraldPrimary
                    BusinessCategory.BANQUE -> AmberPrimary
                    BusinessCategory.INDUSTRIE -> CyberCyan
                    BusinessCategory.TECH -> Color(0xFFC084FC)
                },
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
                        modifier = Modifier.weight(1f, fill = false)
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

                        Column {
                            Text(
                                text = biz.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = biz.category,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onUnlock,
                        enabled = canAffordUnlock,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
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
                                text = "ACHETER",
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
                        modifier = Modifier.weight(1f, fill = false)
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

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = biz.name,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 1
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
                                Spacer(modifier = Modifier.width(2.dp))
                                Box(
                                    modifier = Modifier
                                        .background(categoryColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "Niv. ${biz.level}",
                                        color = categoryColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Text(
                                text = "+${MoneyFormatter.formatPerSec(biz.revenuePerSecond)}",
                                color = EmeraldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Upgrade Button
                    Button(
                        onClick = onUpgrade,
                        enabled = canAffordUpgrade,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
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

                // Manager Status Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(text = biz.managerAvatar, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (biz.managerHired) "Manager : ${biz.managerName}" else "Manuel (Non assigné)",
                            color = if (biz.managerHired) TextPrimary else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = if (biz.managerHired) FontWeight.Bold else FontWeight.Normal,
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
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Recruter (${MoneyFormatter.format(biz.managerCost)})",
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
