package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdNetworkTier
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.DesignSystem
import com.example.viewmodel.GameUiState

@Composable
fun AdMonetizationScreen(
    state: GameUiState,
    onUnlockAdNetwork: (id: String) -> Unit,
    onOpenSponsorDeal: () -> Unit,
    onTriggerRewardedAd: (rewardDesc: String, bonusCash: Double, actionType: String) -> Unit,
    onTriggerInterstitialAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCpm = state.adNetworks.filter { it.isUnlocked }.sumOf { it.cpmRate }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ad_monetization_list"),
        contentPadding = PaddingValues(DesignSystem.Padding.screenOuter),
        verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small)
    ) {
        item {
            // Monetization Studio KPI Dashboard
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ad_studio_kpi_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.cardCompact)
                ) {
                    Text(
                        text = "DEPARTEMENT DES SPONSORS & PARTENARIATS",
                        color = AmberDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Négocie des partenariats stratégiques pour décupler tes revenus.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.small))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.extraSmall)
                    ) {
                        // Metric 1: CPM
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                                .border(1.dp, AmberPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(DesignSystem.Padding.cardCompact)
                        ) {
                            Column {
                                Text("Index Sponsoring", color = AmberDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${String.format("%.1f", totalCpm)} pts",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Metric 2: Impressions
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                                .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(DesignSystem.Padding.cardCompact)
                        ) {
                            Column {
                                Text("Campagnes Actives", color = CyberCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${state.adImpressionsCount} lancées",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        // Metric 3: Ad Revenue
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                                .border(1.dp, EmeraldPrimary.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(DesignSystem.Padding.cardCompact)
                        ) {
                            Column {
                                Text("Revenus Partenaires", color = EmeraldDark, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    MoneyFormatter.format(state.totalAdRevenueEarned),
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                    // Ad Connection Status
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF064E3B).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .border(1.dp, EmeraldPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(EmeraldPrimary, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Partenariats : Actifs",
                                color = EmeraldDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "Booster d'Empire",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Instant Rewarded Sponsor Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("instant_rewarded_ad_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.cardCompact)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CAMPAGNE SPONSORISÉE ÉCLAIR",
                                color = EmeraldDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                             )
                        }

                        Box(
                            modifier = Modifier
                                .background(EmeraldPrimary, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRÊT",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.micro))

                    Text(
                        text = "Signe un accord rapide (3s) pour débloquer du cash et un boost x2.0 (durée: 2H).",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                    Button(
                        onClick = {
                            onTriggerRewardedAd(
                                "Boost Global x2.0 (2h) & Cash Versement !",
                                state.totalPassiveRevenuePerSec * 300.0 + 500.0,
                                "BOOST"
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("watch_rewarded_ad_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LANCER LA CAMPAGNE SPONSORISÉE", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Institutional Sponsor Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interstitial_ad_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.cardCompact)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Handshake,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ACCORD DE SPONSORING INSTITUTIONNEL",
                                color = CyberCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(CyberCyan, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ACCORD INSTANTANÉ",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.micro))

                    Text(
                        text = "Valide un contrat d'expansion pour débloquer immédiatement un financement direct de 5 000 $.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                    Button(
                        onClick = onTriggerInterstitialAd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("show_interstitial_ad_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdsClick, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SIGNER L'ACCORD COMMERCIAL", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Active Instant Interactive Sponsor Offer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("featured_sponsor_deal_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.cardCompact)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = AmberDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DÉFI SPONSOR FLASH",
                                color = AmberDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(AmberPrimary, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DISPONIBLE",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.micro))

                    Text(
                        text = state.activeSponsorOffer?.title ?: "Offre Spéciale Partenaire",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = state.activeSponsorOffer?.description ?: "Participe à l'activité interactive pour booster ton cash.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                    Button(
                        onClick = onOpenSponsorDeal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .testTag("launch_sponsor_challenge_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("RELEVER LE DÉFI & GAGNER LE CASH", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // 30 Daily Notifications Preview & Test Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notification_scheduler_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(DesignSystem.Padding.cardCompact)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔔", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NOTIFICATIONS QUOTIDIENNES ANTI-SPAM",
                                color = CyberCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF0369A1), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("30 DISPONIBLES", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.micro))
                    Text(
                        text = "30 alertes programmées (finances, crises, bourses) sans aucun spam.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                    Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

                    val context = androidx.compose.ui.platform.LocalContext.current
                    Button(
                        onClick = {
                            val randomIndex = (0..29).random()
                            com.example.TycoonNotificationHelper.sendDailyNotification(context, randomIndex)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("TESTER UNE NOTIFICATION ALÉATOIRE", fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(DesignSystem.Spacing.micro))
            Text(
                text = "CATALOGUE DES CONTRATS DE SPONSORING",
                color = AmberDark,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }

        items(state.adNetworks, key = { it.id }) { net ->
            AdNetworkCard(
                net = net,
                playerCash = state.cash,
                onUnlock = { onUnlockAdNetwork(net.id) }
            )
        }
    }
}

@Composable
fun AdNetworkCard(
    net: AdNetworkTier,
    playerCash: Double,
    onUnlock: () -> Unit
) {
    val canAfford = playerCash >= net.unlockCost

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ad_network_card_${net.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (net.isUnlocked) EmeraldPrimary else DarkCardBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Padding.cardCompact)
        ) {
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
                            .size(36.dp)
                            .background(if (net.isUnlocked) Color(0xFF064E3B) else DarkSurfaceVariant, CircleShape)
                            .border(1.dp, if (net.isUnlocked) EmeraldPrimary else DarkCardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (net.isUnlocked) Icons.Default.AdsClick else Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (net.isUnlocked) EmeraldDark else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(DesignSystem.Spacing.small))

                    Column {
                        Text(
                            text = net.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+${MoneyFormatter.formatPerSec(net.autoAdIncomePerSec)} (Index: ${net.cpmRate})",
                            color = if (net.isUnlocked) EmeraldDark else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(DesignSystem.Spacing.extraSmall))

                if (!net.isUnlocked) {
                    Button(
                        onClick = onUnlock,
                        enabled = canAfford,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = DarkSurfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("unlock_ad_net_${net.id}")
                    ) {
                        Text(
                            text = MoneyFormatter.format(net.unlockCost),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ACTIF",
                            color = EmeraldDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.micro))

            Text(
                text = net.description,
                color = TextSecondary,
                fontSize = 10.sp,
                lineHeight = 13.sp
            )
        }
    }
}
