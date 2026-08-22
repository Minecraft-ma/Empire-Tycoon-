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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WheelOfFortuneDialog(
    isOpen: Boolean,
    isDailySpinAvailable: Boolean,
    timeUntilDailyResetFormatted: String,
    onStartSpin: () -> Boolean,
    onSpinReward: (cashPrize: Double, boostMult: Double) -> Unit,
    onWatchAdForSpin: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val haptic = LocalHapticFeedback.current
    var isSpinning by remember { mutableStateOf(false) }
    var targetRotation by remember { mutableFloatStateOf(0f) }
    var resultText by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val animatedRotation by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
        label = "wheelRotation",
        finishedListener = {
            isSpinning = false
        }
    )

    val rewardsList = listOf(
        Pair("💰 x50 CASH TAP", 50.0 to 1.0),
        Pair("🔥 FRENZY BOOST", 100.0 to 3.0),
        Pair("💎 JACKPOT METROPOLIS", 1000.0 to 5.0),
        Pair("⚡ BOOST PERMANENT", 200.0 to 2.0),
        Pair("🌟 x100 CASH BONUS", 100.0 to 1.0),
        Pair("🚀 MEGA BOOST X10", 500.0 to 10.0)
    )

    Dialog(
        onDismissRequest = { if (!isSpinning) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, AmberPrimary, RoundedCornerShape(20.dp))
                .testTag("wheel_of_fortune_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = AmberDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ROUE DE LA FORTUNE TYCOON",
                            color = AmberDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    IconButton(
                        onClick = { if (!isSpinning) onDismiss() },
                        enabled = !isSpinning,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tourne la roue pour remporter cash et multiplicateurs !",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Animated Spinning Wheel Canvas / Disk
                Box(
                    modifier = Modifier.size(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Wheel
                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .rotate(animatedRotation)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFB45309),
                                        Color(0xFF047857),
                                        Color(0xFF6D28D9),
                                        Color(0xFFBE123C),
                                        Color(0xFF0284C7),
                                        Color(0xFFB45309)
                                    )
                                )
                            )
                            .border(2.5.dp, AmberPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎰", fontSize = 42.sp)
                    }

                    // Top Pointer Pin
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .size(18.dp)
                            .background(CrimsonFrenzy, RoundedCornerShape(4.dp))
                            .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (resultText != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                    ) {
                        Text(
                            text = resultText ?: "",
                            color = EmeraldDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Spin Action Buttons
                Button(
                    onClick = {
                        if (!isSpinning && isDailySpinAvailable) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val sessionStarted = onStartSpin()
                            if (sessionStarted) {
                                isSpinning = true
                                val selectedIndex = (0 until rewardsList.size).random()
                                targetRotation += 1440f + (selectedIndex * 60f)
                                scope.launch {
                                    delay(2850)
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    val picked = rewardsList[selectedIndex]
                                    resultText = "GAGNÉ : ${picked.first} !"
                                    onSpinReward(picked.second.first, picked.second.second)
                                }
                            }
                        }
                    },
                    enabled = !isSpinning && isDailySpinAvailable,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("spin_wheel_free_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDailySpinAvailable) AmberPrimary else Color.Gray.copy(alpha = 0.5f),
                        contentColor = if (isDailySpinAvailable) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isDailySpinAvailable) "LANCER LE TIRAGE DU TYCOON !" else "REVIENS DANS : $timeUntilDailyResetFormatted",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        if (!isSpinning) {
                            onWatchAdForSpin()
                        }
                    },
                    enabled = !isSpinning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("spin_wheel_ad_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SPONSOR VIP : TIRAGE BONUS + CASH X2", fontSize = 10.sp, fontWeight = FontWeight.Black, maxLines = 1, softWrap = false)
                    }
                }
            }
        }
    }
}
