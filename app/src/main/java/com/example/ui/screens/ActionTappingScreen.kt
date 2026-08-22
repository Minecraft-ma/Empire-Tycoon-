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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MoneyFormatter
import com.example.ui.theme.DesignSystem
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
import com.example.ui.theme.RoseDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
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
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 600f),
        label = "coreScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "coreAura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
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
        // Hero Visual Cyber Banner (Compact Height)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                    Color(0xFF090D16).copy(alpha = 0.85f),
                                    Color(0xFF090D16).copy(alpha = 0.50f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = DesignSystem.Padding.cardInner),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "METROPOLIS FINANCIAL CORE",
                            color = AmberDark,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (state.isFrenzyActive) "🔥 MODE FRENZY X10 ACTIF !" else "Siège Mondial & Salle des Marchés",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignSystem.Spacing.small))

        // Arena Top Banner: Combo & Frenzy status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Combo Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (state.comboStreak > 10) CrimsonFrenzy else Color(0xFF1E293B)
                    )
                    .border(
                        1.dp,
                        if (state.comboStreak > 10) Color.White.copy(alpha = 0.6f) else DarkCardBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (state.comboStreak > 10) Color.White else AmberDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.micro))
                    Text(
                        text = "COMBO ${state.comboStreak}x",
                        color = if (state.comboStreak > 10) Color.White else AmberDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Total Taps Counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(DesignSystem.Spacing.micro))
                Text(
                    text = "${state.totalTaps} Deals Négociés",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignSystem.Spacing.small))

        // Center Action Arena (Tapping Canvas / Big Coin)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer Glowing Aura Rings
            Box(
                modifier = Modifier
                    .size(145.dp)
                    .scale(if (state.isFrenzyActive) auraScale * 1.15f else auraScale)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (state.isFrenzyActive) CrimsonFrenzy.copy(alpha = 0.40f)
                                else AmberDark.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )

            // Neon Rotating Circuit Ring
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .rotate(ringRotation)
                    .background(
                        Brush.sweepGradient(
                            if (state.isFrenzyActive) listOf(CrimsonFrenzy, AmberDark, NeonPink, CrimsonFrenzy)
                            else listOf(AmberPrimary, CyberCyan, ElectricPurple, AmberPrimary)
                        ),
                        CircleShape
                    )
            )

            // Inner Dark Spacer Ring
            Box(
                modifier = Modifier
                    .size(122.dp)
                    .background(DarkBackground, CircleShape)
            )

            // Interactive Deal Reactor Core
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .scale(coreScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            if (state.isFrenzyActive) listOf(CrimsonFrenzy, Color(0xFF881337))
                            else listOf(Color(0xFFF59E0B), Color(0xFFB45309))
                        )
                    )
                    .border(
                        2.5.dp,
                        if (state.isFrenzyActive) Color.White else AmberDark,
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
                        text = if (state.isFrenzyActive) "🔥" else "👑",
                        fontSize = 30.sp
                    )
                    Text(
                        text = if (state.isFrenzyActive) "FRENZY x10" else "SIGNER DEAL",
                        color = if (state.isFrenzyActive) Color.White else Color(0xFF1E293B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        softWrap = false
                    )
                    Text(
                        text = "+${MoneyFormatter.format(state.cashPerTap)}",
                        color = if (state.isFrenzyActive) AmberLight else Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Floating Animated Coins/Text
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
                        color = if (coin.isCrit) CrimsonFrenzy else AmberDark,
                        fontSize = if (coin.isCrit) 15.sp else 12.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(DesignSystem.Spacing.small))

        // Explicit Direct Action Tap Button
        Button(
            onClick = { onTap(0.5f, 0.5f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .testTag("action_tap_explicit_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isFrenzyActive) CrimsonFrenzy else AmberPrimary,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (state.isFrenzyActive) "🔥" else "👑", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CLIQUER ICI POUR SIGNER DEAL (+${MoneyFormatter.format(state.cashPerTap)})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        Spacer(modifier = Modifier.height(DesignSystem.Spacing.small))

        // Action Deck & Tactical Quick Boosts
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("action_deck_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignSystem.Padding.cardInner)
            ) {
                Text(
                    text = "STRATÉGIE RAPIDE D'ACTION & BOOSTS",
                    color = AmberDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                Button(
                    onClick = {
                        onTriggerRewardedAd(
                            "Mode Frenzy x10 + Multiplicateur x2.5 (4 Hours) !",
                            state.cashPerTap * 150.0,
                            "FRENZY_BOOST"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("action_frenzy_ad_boost_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(DesignSystem.Spacing.tiny))
                        Text(
                            text = "CONTRAT SPONSOR : FRENZY x10 & CASH BOOST",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                // Upgrades Store Button
                Button(
                    onClick = onOpenUpgradesStore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("action_open_upgrades_store_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(DesignSystem.Spacing.tiny))
                        Text(
                            text = "⚡ BOUTIQUE AMÉLIORATIONS & MULTIPLICATEURS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.extraSmall)
                ) {
                    // Auto Tapper Button
                    Button(
                        onClick = onActivateAutoTapper,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("action_auto_tapper_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isAutoTapperActive) CrimsonFrenzy else CyberCyan,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (state.isAutoTapperActive) "🤖 IA AUTO (${state.autoTapperTimeRemainingSec}s)" else "🤖 IA AUTO (30s)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    // Wheel of Fortune Button
                    Button(
                        onClick = onOpenWheel,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .testTag("action_open_wheel_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🎰 ROUE FORTUNE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(DesignSystem.Spacing.small))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small)
                ) {
                    // Tip Card 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(DesignSystem.Padding.cardCompact)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlashOn, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(DesignSystem.Spacing.tiny))
                                Text(
                                    text = "Taps Rapides",
                                    color = EmeraldDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Enchaîne les clics pour charger le Frenzy x10.",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                lineHeight = 12.sp
                            )
                        }
                    }

                    // Tip Card 2
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, AmberPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(DesignSystem.Padding.cardCompact)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(DesignSystem.Spacing.tiny))
                                Text(
                                    text = "Sponsors VIP",
                                    color = AmberDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Rejoins les mini-jeux pour des millions !",
                                color = TextSecondary,
                                fontSize = 9.sp,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
