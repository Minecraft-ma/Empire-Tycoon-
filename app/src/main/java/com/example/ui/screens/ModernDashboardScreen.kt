package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Modèle de particule flottante lors du tap
 */
data class TapParticle(
    val id: Long,
    val text: String,
    val xOffset: Float,
    val yOffset: Float
)

@Composable
fun ModernDashboardScreen(
    state: GameUiState,
    onTap: (x: Float, y: Float) -> Unit,
    onTriggerRewardedAd: (rewardDesc: String, bonusCash: Double, actionType: String) -> Unit,
    onOpenWheel: () -> Unit,
    onActivateAutoTapper: () -> Unit,
    onOpenUpgradesStore: () -> Unit = {},
    onUpgradeClickLevel: () -> Unit = {},
    onTriggerAdBoost: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenLeaderboard: () -> Unit = {},
    onOpenRealEstateMarket: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenAccountSetup: () -> Unit = {},
    onUpdatePlayerName: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val particles = remember { mutableStateListOf<TapParticle>() }
    var isPressed by remember { mutableStateOf(false) }

    // Dialog pour modifier le nom du joueur
    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempPlayerName by remember { mutableStateOf(state.playerName) }

    val coreScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = tween(durationMillis = 80),
        label = "coreScale"
    )

    // Pulsation continue des anneaux du radar
    val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
    val pulseRing1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )
    val pulseRing2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = {
                Text("Modifier le nom du joueur", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                OutlinedTextField(
                    value = tempPlayerName,
                    onValueChange = { tempPlayerName = it },
                    singleLine = true,
                    label = { Text("Nom / Pseudo", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (tempPlayerName.isNotBlank()) {
                            onUpdatePlayerName(tempPlayerName)
                        }
                        showEditNameDialog = false
                    }
                ) {
                    Text("Enregistrer", color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF070C1A),
                        Color(0xFF0A1024),
                        Color(0xFF060914)
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .verticalScroll(rememberScrollState())
            .testTag("modern_dashboard_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // =========================================================================
        // 1. TOP HEADER: Avatar + Player Info (Name & Level) + Leaderboard & Settings
        // =========================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profil Joueur (Avatar + Nom + Niveau)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenAccountSetup() }
                    .padding(4.dp)
                    .testTag("profile_header_row")
            ) {
                // Avatar rond
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                        .border(1.5.dp, Color(0xFF38BDF8), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.avatarEmoji.ifBlank { "💼" }, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (state.playerName.isNotBlank()) state.playerName else "Player234",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = state.countryFlag.ifBlank { "🇫🇷" }, fontSize = 12.sp)
                    }
                    Text(
                        text = "Level ${state.clickLevel}",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Boutons d'action droite : Trophée Classement & Paramètres
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Trophée Classement Joueurs Réels
                IconButton(
                    onClick = onOpenLeaderboard,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131C31))
                        .border(1.dp, Color(0xFF22C55E).copy(alpha = 0.5f), CircleShape)
                        .testTag("leaderboard_header_button")
                ) {
                    Text(text = "🏆", fontSize = 18.sp)
                }

                // Paramètres
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF131C31))
                        .border(1.dp, Color(0xFF263550), CircleShape)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Paramètres",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // =========================================================================
        // 2. VIZA BANK CARD UI (Exact match with reference image)
        // =========================================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("viza_bank_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161F30)),
            border = androidx.compose.foundation.BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0xFF334155),
                        Color(0xFF1E293B),
                        Color(0xFF475569)
                    )
                )
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF131B2B),
                                Color(0xFF0F172A)
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                // Ligne 1 : VIZA logo + Card number dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "VIZA",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = "••••  8256",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ligne 2 : Label Balance + Nom du Joueur avec icône crayon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Balance",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenAccountSetup() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (state.playerName.isNotBlank()) state.playerName else "Player234",
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifier le compte",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Ligne 3 : Montant Géant du Solde + Date d'expiration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$ ${String.format("%,.2f", state.cash).replace(',', ' ')}",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "07/29",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // =========================================================================
        // 3. CLICK POWER & LEVEL UP BOX (Exact match with reference image)
        // =========================================================================
        val upgradeProgress = ((state.cash / state.clickUpgradeCost).toFloat()).coerceIn(0f, 1f)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    if (state.cash >= state.clickUpgradeCost) {
                        onUpgradeClickLevel()
                    }
                }
                .testTag("click_upgrade_container"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                // Top Row: Current $/click VS Next Level +$/click & level
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$ ${String.format("%.2f", state.cashPerTap)} per click",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "+ $ ${String.format("%.2f", state.nextClickPowerGain)} per click",
                            color = Color(0xFF4ADE80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "level ${state.clickLevel + 1}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Barre de progression verte
                LinearProgressIndicator(
                    progress = { upgradeProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF22C55E),
                    trackColor = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Montant requis pour l'upgrade aligné à droite (comme sur l'image)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$ ${String.format("%,.2f", state.clickUpgradeCost).replace(',', ' ')}",
                        color = if (state.cash >= state.clickUpgradeCost) Color(0xFF4ADE80) else Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // =========================================================================
        // 4. CONCENTRIC GLOWING RADAR TAP AREA (Dollar + Tap Hand)
        // =========================================================================
        Box(
            modifier = Modifier
                .size(210.dp)
                .scale(coreScale)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            isPressed = true
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { offset ->
                            val normalizedX = (offset.x / size.width).coerceIn(0.1f, 0.9f)
                            val normalizedY = (offset.y / size.height).coerceIn(0.1f, 0.9f)
                            onTap(normalizedX, normalizedY)

                            // Générer une particule visuelle
                            val pid = System.currentTimeMillis()
                            val textGain = "+$ ${String.format("%.2f", state.cashPerTap)}"
                            particles.add(TapParticle(pid, textGain, (offset.x - size.width / 2) * 0.7f, (offset.y - size.height / 2) * 0.7f))

                            coroutineScope.launch {
                                delay(650)
                                particles.removeAll { it.id == pid }
                            }
                        }
                    )
                }
                .testTag("radar_tap_area"),
            contentAlignment = Alignment.Center
        ) {
            // Anneau 1 : Halo externe vert pulsant
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(pulseRing2)
                    .clip(CircleShape)
                    .border(1.2.dp, Color(0xFF22C55E).copy(alpha = 0.35f), CircleShape)
            )

            // Anneau 2 : Cercle intermédiaire
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(pulseRing1)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF22C55E).copy(alpha = 0.65f), CircleShape)
            )

            // Anneau 3 : Disque central d'action
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF1E293B),
                                Color(0xFF0F172A),
                                Color(0xFF070C1A)
                            )
                        )
                    )
                    .border(2.5.dp, Color(0xFF22C55E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Symbole Dollar néon vert
                    Text(
                        text = "$",
                        color = Color(0xFF4ADE80),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black
                    )

                    // Icône de main pointeur
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Tap",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Particules flottantes
            particles.forEach { particle ->
                Text(
                    text = particle.text,
                    color = Color(0xFF4ADE80),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .offset(x = particle.xOffset.dp, y = (particle.yOffset - 35).dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Texte indicatif sous le radar
        Text(
            text = "Tap in this area to earn money",
            color = Color(0xFF64748B),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
