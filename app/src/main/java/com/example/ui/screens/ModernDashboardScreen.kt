package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DesignSystem
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState
import kotlin.math.max

/**
 * Modèle de données pour une Carte de Sponsor avec mécanique de révélation de bonus.
 */
data class SponsorCardData(
    val id: String,
    val brandName: String,
    val category: String,
    val sponsorIcon: ImageVector,
    val brandColor: Color,
    val bonusTag: String,
    val teaserText: String,
    val bonusTitle: String,
    val bonusValueLabel: String,
    val bonusDescription: String,
    val actionType: String,
    val baseBonusMultiplier: Double = 1.0
)

@Composable
fun ModernDashboardScreen(
    state: GameUiState,
    onTap: (x: Float, y: Float) -> Unit,
    onTriggerRewardedAd: (rewardDesc: String, bonusCash: Double, actionType: String) -> Unit,
    onOpenWheel: () -> Unit,
    onActivateAutoTapper: () -> Unit,
    onOpenUpgradesStore: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var isPressed by remember { mutableStateOf(false) }
    var showExactBalance by remember { mutableStateOf(false) }

    // État de révélation pour chaque carte sponsor (id -> isRevealed)
    val revealedCards = remember { mutableStateMapOf<String, Boolean>() }
    // État de réclamation pour chaque carte sponsor (id -> isClaimed)
    val claimedCards = remember { mutableStateMapOf<String, Boolean>() }
    // Filtre de catégorie pour les sponsors
    var selectedCategory by remember { mutableStateOf("Tous") }

    // Animations pour l'icône interactive d'entreprise
    val coreScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.38f, stiffness = 650f),
        label = "businessCoreScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "businessAuraPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val haloRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "haloRotation"
    )

    // Définition de la liste des sponsors à fort engagement
    val sponsorCards = remember {
        listOf(
            SponsorCardData(
                id = "sponsor_apex_cash",
                brandName = "Apex Capital Partners",
                category = "Cash",
                sponsorIcon = Icons.Default.MonetizationOn,
                brandColor = EmeraldPrimary,
                bonusTag = "INJECTION FLASH",
                teaserText = "Contrat de Trésorerie Sécurisé",
                bonusTitle = "Subvention Royale Immédiate",
                bonusValueLabel = "+35% de Trésorerie Directe",
                bonusDescription = "Versement instantané sur votre compte en banque sans contrepartie.",
                actionType = "INSTANT_CASH_DROP",
                baseBonusMultiplier = 1.35
            ),
            SponsorCardData(
                id = "sponsor_quantum_turbo",
                brandName = "Quantum Dynamics Lab",
                category = "Boost",
                sponsorIcon = Icons.Default.RocketLaunch,
                brandColor = CyberCyan,
                bonusTag = "MULTI SURCHARGE",
                teaserText = "Accélérateur de Particules Financières",
                bonusTitle = "Super Boost Turbo x4.0",
                bonusValueLabel = "Revenus Globaux x4.0 (2 min)",
                bonusDescription = "Quadruple instantanément le débit passif de toutes vos filiales.",
                actionType = "SUPER_BOOST_4X",
                baseBonusMultiplier = 4.0
            ),
            SponsorCardData(
                id = "sponsor_titan_wallstreet",
                brandName = "Titan Wall Street Hedge",
                category = "Frénésie",
                sponsorIcon = Icons.Default.LocalFireDepartment,
                brandColor = CrimsonFrenzy,
                bonusTag = "CLICS DORÉS",
                teaserText = "Spéculation Ultra Haute Fréquence",
                bonusTitle = "Mode Frénésie Totale x10",
                bonusValueLabel = "+1000% Valeur de Clic",
                bonusDescription = "Passe immédiatement votre bureau en état de Frénésie avec combo maximal.",
                actionType = "FRENZY_BOOST",
                baseBonusMultiplier = 10.0
            ),
            SponsorCardData(
                id = "sponsor_nexus_ai",
                brandName = "Nexus Cyber-Intelligence",
                category = "Automatisation",
                sponsorIcon = Icons.Default.SmartToy,
                brandColor = ElectricPurple,
                bonusTag = "ROBOT AUTONOME",
                teaserText = "Algorithme de Signature Prédictive",
                bonusTitle = "Assistant IA Auto-Tapper VIP",
                bonusValueLabel = "Signature Automatique (45s)",
                bonusDescription = "L'ordinateur quantique signe des contrats à la chaîne automatiquement.",
                actionType = "AUTO_TAP_SPONSOR",
                baseBonusMultiplier = 1.0
            ),
            SponsorCardData(
                id = "sponsor_royal_monaco",
                brandName = "Monaco Golden Reserve",
                category = "Jackpot",
                sponsorIcon = Icons.Default.Casino,
                brandColor = AmberPrimary,
                bonusTag = "JACKPOT ÉTOILÉ",
                teaserText = "Consortium Bancaire Privé",
                bonusTitle = "Coffre d'Or & Tour de Roue VIP",
                bonusValueLabel = "Cash Surprise + Tour Garanti",
                bonusDescription = "Ouvre l'accès immédiat à la Roue de la Fortune sans temps d'attente.",
                actionType = "WHEEL_SPIN",
                baseBonusMultiplier = 2.0
            )
        )
    }

    val filteredSponsors = remember(selectedCategory, sponsorCards) {
        if (selectedCategory == "Tous") sponsorCards
        else sponsorCards.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ==========================================
        // 1. PROMINENT BALANCE COUNTER (Material3)
        // ==========================================
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    showExactBalance = !showExactBalance
                }
                .testTag("modern_dashboard_balance_card"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = DarkSurface
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                EmeraldLight.copy(alpha = 0.45f),
                                DarkSurface
                            ),
                            radius = 600f
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                AmberPrimary.copy(alpha = 0.7f),
                                EmeraldPrimary.copy(alpha = 0.5f),
                                DarkCardBorder
                            )
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header du Balance Counter avec status live
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TRÉSORERIE PRINCIPALE",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }

                        // Badge de multiplicateur actif
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (state.isFrenzyActive) CrimsonFrenzy else AmberLight,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (state.isFrenzyActive) Color.White.copy(alpha = 0.6f) else AmberPrimary.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (state.isFrenzyActive) Icons.Default.LocalFireDepartment else Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (state.isFrenzyActive) Color.White else AmberDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (state.isFrenzyActive) "x10 FRENZY" else "x${String.format("%.1f", state.globalMultiplier)} BOOST",
                                    color = if (state.isFrenzyActive) Color.White else AmberDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Grand Chiffre de Solde (Prominent Balance)
                    Text(
                        text = if (showExactBalance) "$${String.format("%,.2f", state.cash)}" else MoneyFormatter.format(state.cash),
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Débit Passif en Temps Réel (+$/sec)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = EmeraldPrimary.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+${MoneyFormatter.format(state.totalPassiveRevenuePerSec)} / sec",
                            color = EmeraldDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Sous-métriques de l'Empire (Fortune Nette, Clic, Taps)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Valeur par Clic", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "+${MoneyFormatter.format(state.cashPerTap)}",
                                color = AmberDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Fortune Nette", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = MoneyFormatter.format(state.netWorth),
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Niveau Prestige", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = "Niveau ${state.prestigeLevel}",
                                color = ElectricPurple,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 2. LARGE INTERACTABLE BUSINESS ICON
        // ==========================================
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bandeau de titre du Siège Mondial
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = AmberPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "QG DE L'EMPIRE COMMERCIAL",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grande Icône Interactif d'Entreprise avec auras et animations
            Box(
                modifier = Modifier
                    .size(200.dp)
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
                            }
                        )
                    }
                    .testTag("large_interactable_business_icon"),
                contentAlignment = Alignment.Center
            ) {
                // Anneau 1 : Halo pulsant extérieur
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (state.isFrenzyActive) CrimsonFrenzy.copy(alpha = 0.5f) else AmberPrimary.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Anneau 2 : Couronne rotative externe métallisée
                Box(
                    modifier = Modifier
                        .size(184.dp)
                        .rotate(haloRotation)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    AmberPrimary,
                                    EmeraldPrimary,
                                    CyberCyan,
                                    ElectricPurple,
                                    AmberPrimary
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Anneau 3 : Disque central d'entreprise
                Box(
                    modifier = Modifier
                        .size(154.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    DarkSurfaceVariant,
                                    DarkSurface,
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(
                            width = 3.dp,
                            color = if (state.isFrenzyActive) CrimsonFrenzy else AmberPrimary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Grand icône représentatif du gratte-ciel / entreprise
                        Icon(
                            imageVector = if (state.isFrenzyActive) Icons.Default.LocalFireDepartment else Icons.Default.BusinessCenter,
                            contentDescription = "Grande Icône d'Entreprise",
                            tint = if (state.isFrenzyActive) CrimsonFrenzy else AmberPrimary,
                            modifier = Modifier.size(62.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (state.isFrenzyActive) "FRÉNÉSIE !" else "TOUCHER",
                            color = if (state.isFrenzyActive) Color.White else AmberDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "+${MoneyFormatter.format(state.cashPerTap)}",
                            color = EmeraldDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Badge combo flottant sur l'icône
                if (state.comboStreak > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CrimsonFrenzy)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "x${state.comboStreak} 🔥",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Jauge de progression vers le mode Frénésie
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isFrenzyActive) "FRÉNÉSIE ROYALE ACTIVE (x10)" else "JAUGE DE FRÉNÉSIE",
                        color = if (state.isFrenzyActive) CrimsonFrenzy else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (state.isFrenzyActive) "${state.frenzyTimeRemainingSec}s" else "${(state.frenzyProgress * 100).toInt()}%",
                        color = if (state.isFrenzyActive) CrimsonFrenzy else AmberDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { if (state.isFrenzyActive) (state.frenzyTimeRemainingSec / 10f).coerceIn(0f, 1f) else state.frenzyProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (state.isFrenzyActive) CrimsonFrenzy else AmberPrimary,
                    trackColor = DarkSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Raccourcis rapides : Auto-Tapper & Roue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onActivateAutoTapper() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (state.isAutoTapperActive) CrimsonFrenzy else CyberCyan.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = if (state.isAutoTapperActive) CrimsonFrenzy else CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Auto-Tapper IA",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (state.isAutoTapperActive) "${state.autoTapperTimeRemainingSec}s restantes" else "Activer (30s)",
                                color = if (state.isAutoTapperActive) CrimsonFrenzy else TextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenWheel() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = AmberDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Roue de la Fortune",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (state.isDailySpinAvailable) "1 Tour Gratuit 🎁" else "Lancer",
                                color = if (state.isDailySpinAvailable) EmeraldDark else TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = if (state.isDailySpinAvailable) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =======================================================
        // 3. LIST OF 'SPONSOR' AD-CARDS THAT REVEAL BONUSES
        // =======================================================
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header de la section Sponsors
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SPONSORS & CONTRATS MYSTÈRES",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Text(
                        text = "Révélez les cartes partenaires pour débloquer des méga-bonus",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                // Compteur de cartes révélées
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "${revealedCards.size} / ${sponsorCards.size} révélés",
                        color = CyberCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filtres de catégories pour les cartes de sponsors
            val categories = listOf("Tous", "Cash", "Boost", "Frénésie", "Automatisation", "Jackpot")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 6.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EmeraldPrimary else DarkCardBorder
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Liste verticale des Cartes de Sponsors Interactives
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredSponsors.forEach { sponsor ->
                    val isRevealed = revealedCards[sponsor.id] == true
                    val isClaimed = claimedCards[sponsor.id] == true

                    SponsorAdCard(
                        sponsor = sponsor,
                        isRevealed = isRevealed,
                        isClaimed = isClaimed,
                        onReveal = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            revealedCards[sponsor.id] = true
                        },
                        onClaimBonus = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val bonusCash = when (sponsor.actionType) {
                                "INSTANT_CASH_DROP" -> max(2500.0, state.cash * 0.35)
                                "SUPER_BOOST_4X" -> max(1500.0, state.totalPassiveRevenuePerSec * 45.0)
                                "FRENZY_BOOST" -> max(1000.0, state.cashPerTap * 150.0)
                                else -> 2000.0
                            }

                            // Déclenche l'annonce récompensée AdMob / simulée
                            onTriggerRewardedAd(
                                "${sponsor.brandName} : ${sponsor.bonusTitle}",
                                bonusCash,
                                sponsor.actionType
                            )
                            claimedCards[sponsor.id] = true
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Composant individuel pour une Carte de Sponsor qui révèle son bonus avec Material 3.
 */
@Composable
fun SponsorAdCard(
    sponsor: SponsorCardData,
    isRevealed: Boolean,
    isClaimed: Boolean,
    onReveal: () -> Unit,
    onClaimBonus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isClaimed -> EmeraldPrimary.copy(alpha = 0.5f)
            isRevealed -> sponsor.brandColor.copy(alpha = 0.8f)
            else -> DarkCardBorder
        },
        label = "sponsorBorderColor"
    )

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sponsor_card_${sponsor.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = DarkSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isRevealed) 4.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, borderColor, RoundedCornerShape(16.dp))
                .background(
                    if (isRevealed) {
                        Brush.verticalGradient(
                            listOf(
                                sponsor.brandColor.copy(alpha = 0.12f),
                                DarkSurface
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                DarkSurface,
                                DarkSurfaceVariant
                            )
                        )
                    }
                )
                .padding(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header du Sponsor (Marque & Badge de Statut)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(sponsor.brandColor.copy(alpha = 0.2f))
                                .border(1.dp, sponsor.brandColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = sponsor.sponsorIcon,
                                contentDescription = null,
                                tint = sponsor.brandColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = sponsor.brandName,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = sponsor.bonusTag,
                                color = sponsor.brandColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Badge Scellé vs Découvert
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            isClaimed -> EmeraldLight
                            isRevealed -> sponsor.brandColor.copy(alpha = 0.2f)
                            else -> Color(0xFF1E293B)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when {
                                    isClaimed -> Icons.Default.CheckCircle
                                    isRevealed -> Icons.Default.LockOpen
                                    else -> Icons.Default.Lock
                                },
                                contentDescription = null,
                                tint = when {
                                    isClaimed -> EmeraldDark
                                    isRevealed -> sponsor.brandColor
                                    else -> TextSecondary
                                },
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when {
                                    isClaimed -> "RÉCLAMÉ"
                                    isRevealed -> "RÉVÉLÉ !"
                                    else -> "SCELLÉ"
                                },
                                color = when {
                                    isClaimed -> EmeraldDark
                                    isRevealed -> sponsor.brandColor
                                    else -> TextSecondary
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Contenu : Soit Masqué (À Révéler), Soit Découvert (Affichage du Bonus)
                AnimatedVisibility(
                    visible = !isRevealed,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = DarkSurfaceVariant.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎁 Offre Spéciale Partenaire Mystère",
                            color = AmberDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sponsor.teaserText,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onReveal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .testTag("btn_reveal_${sponsor.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = sponsor.brandColor,
                                contentColor = Color.Black
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "RÉVÉLER LE BONUS SPONSOR 🔍",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isRevealed,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF0F172A),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, sponsor.brandColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sponsor.bonusTitle,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = sponsor.bonusValueLabel,
                                color = sponsor.brandColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = sponsor.bonusDescription,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bouton d'action pour réclamer le bonus via vidéo publicitaire
                        Button(
                            onClick = onClaimBonus,
                            enabled = !isClaimed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("btn_claim_${sponsor.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isClaimed) Color(0xFF1E293B) else EmeraldPrimary,
                                contentColor = if (isClaimed) TextMuted else Color.Black,
                                disabledContainerColor = Color(0xFF1E293B),
                                disabledContentColor = TextMuted
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isClaimed) Icons.Default.CheckCircle else Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isClaimed) "BONUS DÉJÀ RÉCLAMÉ ✓" else "ACTIVER LE BONUS (PUB VIDÉO ADMOB) 🎬",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
