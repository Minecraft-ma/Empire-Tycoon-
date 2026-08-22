package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MiniGameType
import com.example.model.SponsorOffer
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.AmberText
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.NeonPink
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantCardBorder
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantSurfaceVariant
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun MiniGameHostDialog(
    offer: SponsorOffer,
    onComplete: (success: Boolean, bonusFactor: Double, score: Int, maxScore: Int) -> Unit,
    onDismiss: () -> Unit,
    onRegisterTap: () -> Boolean = { true }
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.70f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.90f)
                    .testTag("minigame_dialog_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = VibrantSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    Brush.verticalGradient(listOf(EmeraldPrimary, AmberPrimary))
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                when (offer.miniGameType) {
                    MiniGameType.GOLD_RUSH_CATCH -> GoldRushCatchGame(offer, onComplete, onDismiss, onRegisterTap)
                    MiniGameType.DEAL_PITCH_SLIDER -> DealPitchSliderGame(offer, onComplete, onDismiss)
                    MiniGameType.CRYPTO_FAST_PUMP -> CryptoFastPumpGame(offer, onComplete, onDismiss, onRegisterTap)
                    MiniGameType.LUCKY_VIP_SPIN -> LuckyVipWheelGame(offer, onComplete, onDismiss)
                    MiniGameType.VIRAL_AD_CAMPAIGN -> ViralCampaignGame(offer, onComplete, onDismiss, onRegisterTap)
                }
            }
        }
    }
}

// Mini Game 1: Gold Rush Catch
@Composable
fun GoldRushCatchGame(
    offer: SponsorOffer,
    onComplete: (Boolean, Double, Int, Int) -> Unit,
    onDismiss: () -> Unit,
    onRegisterTap: () -> Boolean
) {
    var timeLeft by remember { mutableIntStateOf(5) }
    var score by remember { mutableIntStateOf(0) }

    data class FallingItem(val id: Int, var x: Float, var y: Float, var collected: Boolean = false)
    val items = remember {
        mutableStateListOf(
            FallingItem(1, 0.2f, 0.15f),
            FallingItem(2, 0.5f, 0.10f),
            FallingItem(3, 0.8f, 0.20f),
            FallingItem(4, 0.35f, 0.35f),
            FallingItem(5, 0.65f, 0.40f),
            FallingItem(6, 0.25f, 0.55f),
            FallingItem(7, 0.75f, 0.60f)
        )
    }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        val bonus = if (score >= 4) 1.5 else if (score >= 2) 1.0 else 0.5
        delay(400)
        onComplete(score >= 2, bonus, score, 7)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💰 ${offer.title}",
                color = AmberText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Quitter", tint = TextMuted)
            }
        }

        Text(
            text = "Touche un maximum de sacs d'or en $timeLeft s !",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(AmberLight.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .border(1.5.dp, AmberPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
        ) {
            items.forEach { item ->
                if (!item.collected) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (item.x * 250).dp,
                                y = (item.y * 180).dp
                            )
                            .size(52.dp)
                            .background(
                                Brush.radialGradient(listOf(AmberLight, AmberPrimary)),
                                CircleShape
                            )
                            .border(2.dp, AmberDark, CircleShape)
                            .clickable {
                                if (!item.collected && timeLeft > 0) {
                                    val valid = onRegisterTap()
                                    if (valid) {
                                        item.collected = true
                                        score++
                                    }
                                }
                            }
                            .testTag("catch_gold_item_${item.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "💰", fontSize = 24.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Sacs Attrapés : $score / 7", color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(text = "Temps : ${timeLeft}s", color = CrimsonFrenzy, fontWeight = FontWeight.Black)
        }
    }
}

// Mini Game 2: Deal Pitch Slider
@Composable
fun DealPitchSliderGame(
    offer: SponsorOffer,
    onComplete: (Boolean, Double, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sliderOsc")
    val cursorPosition by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor"
    )

    var locked by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🤝 ${offer.title}", color = IndigoPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Quitter", tint = TextMuted)
            }
        }

        Text(
            text = "Appuie sur le bouton quand le curseur est dans la zone verte (40% - 60%) !",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .clip(RoundedCornerShape(19.dp))
                .background(VibrantSurfaceVariant)
                .border(2.dp, VibrantCardBorder, RoundedCornerShape(19.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(EmeraldLight)
                    .border(1.5.dp, EmeraldPrimary, RoundedCornerShape(4.dp))
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.07f)
                    .fillMaxSize()
                    .align(Alignment.CenterStart)
                    .offset(x = (cursorPosition * 260).dp)
                    .background(AmberPrimary, RoundedCornerShape(6.dp))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (resultText != null) {
            Text(
                text = resultText ?: "",
                color = if (resultText?.contains("PARFAIT") == true || resultText?.contains("ACCEPTÉ") == true) EmeraldDark else CrimsonFrenzy,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!locked) {
                    locked = true
                    val isInSweetZone = cursorPosition in 0.35f..0.65f
                    val isPerfect = cursorPosition in 0.45f..0.55f
                    val score = if (isPerfect) 2 else if (isInSweetZone) 1 else 0
                    val factor = if (isPerfect) 2.0 else if (isInSweetZone) 1.2 else 0.5
                    resultText = if (isInSweetZone) "🎉 PITCH ACCEPTÉ ! MULTI x$factor !" else "❌ HORS ZONE : DEAL ÉCHOUÉ"

                    onComplete(isInSweetZone, factor, score, 2)
                }
            },
            enabled = !locked,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("lock_deal_button"),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "VERROUILLER LE DEAL !", fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
    }
}

// Mini Game 3: Crypto Fast Pump
@Composable
fun CryptoFastPumpGame(
    offer: SponsorOffer,
    onComplete: (Boolean, Double, Int, Int) -> Unit,
    onDismiss: () -> Unit,
    onRegisterTap: () -> Boolean
) {
    val haptic = LocalHapticFeedback.current
    var tapCount by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(4) }
    var isFinished by remember { mutableStateOf(false) }
    val targetTaps = 15

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        if (!isFinished) {
            isFinished = true
            val success = tapCount >= targetTaps
            val factor = if (tapCount >= 20) 2.0 else if (success) 1.2 else 0.5
            onComplete(success, factor, tapCount, 50)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "⚡ ${offer.title}", color = EmeraldDark, fontSize = 16.sp, fontWeight = FontWeight.Black)
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Quitter", tint = TextMuted)
            }
        }

        Text(
            text = "Tappe le bloc le plus vite possible ($targetTaps taps en $timeLeft s) !",
            color = TextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(listOf(EmeraldPrimary, EmeraldDark)),
                    RoundedCornerShape(24.dp)
                )
                .border(2.5.dp, EmeraldBorder, RoundedCornerShape(24.dp))
                .clickable {
                    if (!isFinished && timeLeft > 0) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val valid = onRegisterTap()
                        if (valid) {
                            tapCount++
                        }
                    }
                }
                .testTag("crypto_mining_tap_block"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CurrencyBitcoin, contentDescription = null, tint = Color.White, modifier = Modifier.size(42.dp))
                Text(text = "$tapCount / $targetTaps", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { (tapCount.toFloat() / targetTaps).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = EmeraldPrimary,
            trackColor = VibrantSurfaceVariant
        )
    }
}

// Mini Game 4: Lucky VIP Wheel
@Composable
fun LuckyVipWheelGame(
    offer: SponsorOffer,
    onComplete: (Boolean, Double, Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var spinning by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val animatedAngle by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
        label = "wheelRotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🎡 ${offer.title}", color = NeonPink, fontSize = 16.sp, fontWeight = FontWeight.Black)
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Quitter", tint = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .size(150.dp)
                .rotate(animatedAngle)
                .background(
                    Brush.sweepGradient(
                        listOf(AmberPrimary, NeonPink, CyberCyan, EmeraldPrimary, IndigoPrimary, AmberPrimary)
                    ),
                    CircleShape
                )
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🎁", fontSize = 38.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (!spinning && !isFinished) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    spinning = true
                    rotationAngle += 1440f + Random.nextInt(0, 360)
                }
            },
            enabled = !spinning && !isFinished,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("spin_wheel_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonPink, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = if (spinning) "TIRAGE EN COURS..." else if (isFinished) "TIRAGE COMPLÉTÉ" else "TOURNER LA ROUE VIP !", fontWeight = FontWeight.Black)
        }

        LaunchedEffect(spinning) {
            if (spinning && !isFinished) {
                delay(2700)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                isFinished = true
                onComplete(true, 1.8, 1, 1)
            }
        }
    }
}

// Mini Game 5: Viral Campaign
@Composable
fun ViralCampaignGame(
    offer: SponsorOffer,
    onComplete: (Boolean, Double, Int, Int) -> Unit,
    onDismiss: () -> Unit,
    onRegisterTap: () -> Boolean
) {
    var poppedCount by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(5) }
    var isFinished by remember { mutableStateOf(false) }
    var clickedIndices by remember { mutableStateOf(setOf<Int>()) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        if (!isFinished) {
            isFinished = true
            val success = poppedCount >= 4
            onComplete(success, if (poppedCount >= 6) 2.0 else 1.2, poppedCount, 6)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🔥 ${offer.title}", color = ElectricPurple, fontSize = 16.sp, fontWeight = FontWeight.Black)
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Quitter", tint = TextMuted)
            }
        }

        Text(
            text = "Éclate les notifications virales ($poppedCount / 6 en $timeLeft s) !",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("🚀", "💎", "⭐", "🔥", "📈", "👑").forEachIndexed { index, emoji ->
                val isClicked = clickedIndices.contains(index)
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(if (isClicked) IndigoLight.copy(alpha = 0.4f) else IndigoLight, CircleShape)
                        .border(1.5.dp, if (isClicked) Color.Gray else ElectricPurple, CircleShape)
                        .clickable {
                            if (!isFinished && timeLeft > 0 && !isClicked) {
                                val valid = onRegisterTap()
                                if (valid) {
                                    clickedIndices = clickedIndices + index
                                    poppedCount++
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 22.sp, modifier = Modifier.alpha(if (isClicked) 0.5f else 1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Text(text = "Buzz Score: $poppedCount", color = ElectricPurple, fontWeight = FontWeight.Bold)
    }
}
