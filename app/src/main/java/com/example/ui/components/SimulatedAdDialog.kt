package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SimulatedAdDialog(
    rewardDescription: String,
    rewardBonusCash: Double,
    onAdCompleted: () -> Unit,
    onDismiss: () -> Unit
) {
    var secondsRemaining by remember { mutableIntStateOf(1) }
    var isAdFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(500L)
            secondsRemaining--
        }
        isAdFinished = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = ((2 - secondsRemaining) / 2f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "adProgress"
    )

    Dialog(
        onDismissRequest = { if (isAdFinished) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.80f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, if (isAdFinished) EmeraldPrimary else AmberPrimary, RoundedCornerShape(20.dp))
                    .testTag("simulated_ad_dialog"),
                color = DarkSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(AmberPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Campaign, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CAMPAGNE COMMERCIALE D'ENTREPRISE",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(6.dp))
                        ) {
                            Text(
                                text = if (isAdFinished) "VALIDÉE ✓" else "DIFFUSION...",
                                color = if (isAdFinished) EmeraldLight else AmberDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Media Simulation Box
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xFF1E1B4B),
                                            Color(0xFF0F172A),
                                            Color(0xFF064E3B).copy(alpha = 0.4f)
                                        )
                                    )
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(if (isAdFinished) EmeraldPrimary.copy(alpha = 0.2f) else CyberCyan.copy(alpha = 0.2f), CircleShape)
                                        .border(1.5.dp, if (isAdFinished) EmeraldPrimary else CyberCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isAdFinished) Icons.Default.CheckCircle else Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = if (isAdFinished) EmeraldLight else CyberCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (isAdFinished) "DIFFUSION TERMINÉE" else "DIFFUSION EN COURS SUR LE RÉSEAU",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = "Régie publicitaire interne d'entreprise",
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Indicator
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (isAdFinished) EmeraldPrimary else AmberPrimary,
                        trackColor = DarkSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Reward Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "RÉMUNÉRATION DU CONTRAT",
                                    color = EmeraldDark,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Text(
                                    text = rewardDescription,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                if (rewardBonusCash > 0) {
                                    Text(
                                        text = "+${MoneyFormatter.format(rewardBonusCash)} Liquidités Débloquées !",
                                        color = EmeraldLight,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action / Claim Button
                    Button(
                        onClick = {
                            if (isAdFinished) {
                                onAdCompleted()
                            }
                        },
                        enabled = isAdFinished,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("claim_simulated_ad_reward_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = DarkSurfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (isAdFinished) Color.Black else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAdFinished) "RÉCUPÉRER LA RÉCOMPENSE !" else "DIFFUSION (${secondsRemaining}s)...",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}
