package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

@Composable
fun ActionTappingScreen(
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

    val coreScale by animateFloatAsState(
        targetValue = if (isPressed) 0.86f else 1.0f,
        animationSpec = spring(dampingRatio = 0.35f, stiffness = 700f),
        label = "coreScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "coreAura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(DesignSystem.Padding.screenOuter),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bureau du PDG Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (state.isFrenzyActive) CrimsonFrenzy else DarkCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = "Skyline Tycoon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF090D16).copy(alpha = 0.90f),
                                    Color(0xFF090D16).copy(alpha = 0.60f),
                                    Color(0xFF090D16).copy(alpha = 0.30f)
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👑", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "BUREAU DU CHEF D'ENTREPRISE",
                                color = AmberDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        }
                        Text(
                            text = if (state.isFrenzyActive) "🔥 FRÉNÉSIE BOURSIÈRE x10 ACTIVE !" else "Négocie des contrats & Développe ton empire",
                            color = if (state.isFrenzyActive) Color.White else Color(0xFFF1F5F9),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Multiplicateur Actif Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (state.isFrenzyActive) CrimsonFrenzy else Color(0xFF0F172A))
                            .border(1.dp, if (state.isFrenzyActive) Color.White else AmberPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (state.isFrenzyActive) "x10 FRENZY" else "x${String.format("%.1f", state.globalMultiplier)} BOOST",
                            color = if (state.isFrenzyActive) Color.White else AmberDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Jauge de Frénésie & Compteur Combo
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = if (state.isFrenzyActive) CrimsonFrenzy else AmberDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.isFrenzyActive) "FRÉNÉSIE ROYALE ACTIVE !" else "Jauge de Frénésie Boursière",
                            color = if (state.isFrenzyActive) CrimsonFrenzy else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Text(
                        text = "Combo: ${state.comboStreak}x  •  Total: ${state.totalTaps} deals",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress bar for frenzy
                val frenzyProgress = if (state.isFrenzyActive) {
                    1.0f
                } else {
                    (state.comboStreak % 25) / 25f
                }

                LinearProgressIndicator(
                    progress = { frenzyProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (state.isFrenzyActive) CrimsonFrenzy else EmeraldPrimary,
                    trackColor = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Centre de Tapping du PDG (Le Grand Sceau d'Or / Lingot du Boss)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            // Aura d'énergie pulsante
            Box(
                modifier = Modifier
                    .size(175.dp)
                    .scale(if (state.isFrenzyActive) auraScale * 1.15f else auraScale)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (state.isFrenzyActive) CrimsonFrenzy.copy(alpha = 0.45f)
                                else AmberPrimary.copy(alpha = 0.30f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )

            // Anneau néon rotatif
            Box(
                modifier = Modifier
                    .size(155.dp)
                    .rotate(ringRotation)
                    .background(
                        Brush.sweepGradient(
                            if (state.isFrenzyActive) listOf(CrimsonFrenzy, AmberDark, NeonPink, CrimsonFrenzy)
                            else listOf(AmberPrimary, EmeraldPrimary, CyberCyan, AmberPrimary)
                        ),
                        CircleShape
                    )
            )

            // Anneau sombre d'espacement
            Box(
                modifier = Modifier
                    .size(142.dp)
                    .background(DarkBackground, CircleShape)
            )

            // Sceau d'Or Central Tactile
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(coreScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (state.isFrenzyActive) listOf(CrimsonFrenzy, Color(0xFF7F1D1D))
                            else listOf(Color(0xFFFBBF24), Color(0xFFD97706), Color(0xFF78350F))
                        )
                    )
                    .border(
                        3.dp,
                        if (state.isFrenzyActive) Color.White else Color(0xFFFEF3C7),
                        CircleShape
                    )
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                isPressed = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onTap(offset.x / size.width.toFloat(), offset.y / size.height.toFloat())
                                tryAwaitRelease()
                                isPressed = false
                            }
                        )
                    }
                    .testTag("action_tap_core_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (state.isFrenzyActive) "🔥" else "💼",
                        fontSize = 32.sp
                    )
                    Text(
                        text = if (state.isFrenzyActive) "x10 CASH !" else "SIGNER CONTRAT",
                        color = if (state.isFrenzyActive) Color.White else Color(0xFF1E1B4B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (state.isFrenzyActive) Color(0xFF991B1B) else Color(0xFF451A03).copy(alpha = 0.4f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+${MoneyFormatter.format(state.cashPerTap)}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Particules de cash flottantes
            state.floatingCoins.forEach { coin ->
                Box(
                    modifier = Modifier
                        .offset(
                            x = (coin.x * 160 - 80).dp,
                            y = (coin.y * 140 - 90).dp
                        )
                ) {
                    Text(
                        text = coin.text,
                        color = if (coin.isCrit) CrimsonFrenzy else Color(0xFFFBBF24),
                        fontSize = if (coin.isCrit) 16.sp else 13.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bouton de signature tactile explicite (Optionnel mais très pratique sur mobile)
        Button(
            onClick = { onTap(0.5f, 0.5f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("action_tap_explicit_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isFrenzyActive) CrimsonFrenzy else AmberPrimary,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (state.isFrenzyActive) "🔥" else "✍️", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CLIC DU PATRON : +${MoneyFormatter.format(state.cashPerTap)} PAR DEAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // PUB RÉMUNÉRÉE FLASH : Le Super-Boost Sponsorisé
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_frenzy_ad_boost_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E19)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(EmeraldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "OFFRE SPONSOR : MÉGA BOOST CASH",
                                color = EmeraldDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Regarde une courte vidéo pour décupler tes profits !",
                                color = Color(0xFFCBD5E1),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(EmeraldPrimary, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "GRATUIT",
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onTriggerRewardedAd(
                            "Mode Frénésie Royale x10 + Injection Immédiate de Cash !",
                            state.cashPerTap * 250.0 + 1000.0,
                            "FRENZY_BOOST"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("action_frenzy_ad_boost_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎬", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DÉCLENCHER FRÉNÉSIE x10 + CASH BOOSTER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Outils & Boosts Rapides du Chef
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Assistant IA Auto Tapper
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(84.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (state.isAutoTapperActive) CrimsonFrenzy else CyberCyan.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SmartToy, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Assistant IA",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (state.isAutoTapperActive) {
                            Text(
                                text = "${state.autoTapperTimeRemainingSec}s",
                                color = CrimsonFrenzy,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Button(
                        onClick = onActivateAutoTapper,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .testTag("action_auto_tapper_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isAutoTapperActive) CrimsonFrenzy else CyberCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (state.isAutoTapperActive) "ACTIF 🔥" else "ACTIVER (30s)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Roue de la Fortune
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(84.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Casino, contentDescription = null, tint = AmberDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Roue Fortune",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "🎁",
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onOpenWheel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .testTag("action_open_wheel_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "TOURNER 🎰",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Boutique d'équipements & Multiplicateurs
        Button(
            onClick = onOpenUpgradesStore,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag("action_open_upgrades_store_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary.copy(alpha = 0.4f))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = AmberDark, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AMÉLIORER MON BUREAU & ACHETER DES BOOSTS ⚡",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
